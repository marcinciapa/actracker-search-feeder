package ovh.equino.actracker.searchfeed.infrastructure.index.elasticsearch.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.PutAliasRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class TagSetIndex {

    private static final Logger LOG = LoggerFactory.getLogger(TagSetIndex.class);

    private static final String COMMON_MAPPINGS_DIR_PATH = "/elasticsearch/mappings";
    private static final String INDEX_NAME = "tagset";
    private static final String MAPPING_FILE_EXTENSION = ".json";

    private final String mappingsDirPath;
    private final ElasticsearchClient client;
    private final List<VersionedIndex> versionedIndices;

    public TagSetIndex(ElasticsearchClient client, String environment) {
        this.client = client;
        this.mappingsDirPath = "%s/%s".formatted(COMMON_MAPPINGS_DIR_PATH, INDEX_NAME);
        List<String> indexVersions = getIndexVersionsFromMappingsFiles();
        versionedIndices = indexVersions.stream()
                .map(version -> new VersionedIndex(COMMON_MAPPINGS_DIR_PATH, INDEX_NAME, version, environment, client))
                .toList();
    }

    private List<String> getIndexVersionsFromMappingsFiles() {
        try {
            List<Path> mappingsPaths = getMappingsPaths();
            return mappingsPaths.stream()
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(path -> substringBefore(path, MAPPING_FILE_EXTENSION))
                    .toList();
        } catch (Exception e) {
            String message = "Could not find index mapping files in resource %s".formatted(MAPPING_FILE_EXTENSION);
            throw new RuntimeException(message, e);
        }
    }

    private List<Path> getMappingsPaths() throws IOException, URISyntaxException {
        URL mappingsDirUrl = getClass().getResource(mappingsDirPath);
        URI mappingsDirUri = requireNonNull(mappingsDirUrl).toURI();

        if ("jar".equals(mappingsDirUri.getScheme())) {
            return getMappingsPathsFromJar(mappingsDirUri);
        } else {
            return getMappingsPathsFromFilesystem(mappingsDirUri);
        }
    }

    private List<Path> getMappingsPathsFromJar(URI resourceUri) throws IOException {
        List<Path> mappingsPaths;
        try (FileSystem fileSystem = FileSystems.newFileSystem(resourceUri, Collections.emptyMap())) {
            Path resourcesPath = fileSystem.getPath("/");
            mappingsPaths = extractMappingsPaths(resourcesPath);
        }
        return mappingsPaths;
    }

    private List<Path> getMappingsPathsFromFilesystem(URI resourceUri) throws IOException {
        Path resourcesPath = Paths.get(resourceUri);
        return extractMappingsPaths(resourcesPath);
    }

    private List<Path> extractMappingsPaths(Path resourcesPath) throws IOException {
        return Files.walk(resourcesPath)
                .filter(Objects::nonNull)
                .filter(path -> path.toString().contains(mappingsDirPath))
                .filter(path -> path.toString().endsWith(MAPPING_FILE_EXTENSION))
                .toList();
    }

    public void create() {
        versionedIndices.forEach(VersionedIndex::create);
//        recreateIndexAlias();
        LOG.info("Elasticsearch index {} created", INDEX_NAME);
    }

    private void recreateIndexAlias() {
        String aliasedVersion = getAliasedVersionFromFile();
        String aliasedIndex = getIndexMatchingVersion(aliasedVersion).indexName();
        PutAliasRequest putAliasRequest = new PutAliasRequest.Builder()
                .name(INDEX_NAME)
                .index(aliasedIndex)
                .build();
        try {
            client.indices().putAlias(putAliasRequest);
            LOG.info("Elasticsearch alias {} created aliasing index {}", INDEX_NAME, aliasedIndex);
        } catch (IOException e) {
            String message = "Couldn't create Elasticsearch alias %s matching index %s"
                    .formatted(INDEX_NAME, aliasedIndex);
            throw new RuntimeException(message, e);
        }
    }

    private String getAliasedVersionFromFile() {
        String aliasedVersion;
        String aliasFilePath = "%s/alias".formatted(mappingsDirPath);
        try (InputStream aliasFileContent = getClass().getResourceAsStream(aliasFilePath)) {
            aliasedVersion = new String(requireNonNull(aliasFileContent).readAllBytes());

        } catch (IOException e) {
            String message = "Cannot create alias %s".formatted(INDEX_NAME);
            throw new RuntimeException(message, e);
        }
        return aliasedVersion;
    }

    private VersionedIndex getIndexMatchingVersion(String aliasedVersion) {
        VersionedIndex indexToAlias = versionedIndices.stream()
                .filter(versionedIndex -> aliasedVersion.equals(versionedIndex.version()))
                .findAny()
                .orElseThrow(() -> {
                    String message = "Could not find index with version %s to alias it.".formatted(aliasedVersion);
                    return new RuntimeException(message);
                });
        return indexToAlias;
    }


}
