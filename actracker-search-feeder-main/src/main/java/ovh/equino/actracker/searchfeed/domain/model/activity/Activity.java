package ovh.equino.actracker.searchfeed.domain.model.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import ovh.equino.actracker.searchfeed.domain.model.Entity;
import ovh.equino.actracker.searchfeed.domain.model.Version;
import ovh.equino.actracker.searchfeed.domain.model.creator.CreatorId;
import ovh.equino.actracker.searchfeed.domain.model.metricValue.MetricValue;
import ovh.equino.actracker.searchfeed.domain.model.tag.TagId;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

public final class Activity extends Entity<ActivityId> {

    private final String title;
    private final Instant startTime;
    private final Instant endTime;
    private final String comment;
    private final Set<TagId> tags;
    private final Collection<MetricValue> metricValues;

    public Activity(@JsonProperty("id") ActivityId id,
                    @JsonProperty("version") Version version,
                    @JsonProperty("softDeleted") boolean softDeleted,
                    @JsonProperty("creatorId") CreatorId creatorId,
                    @JsonProperty("title") String title,
                    @JsonProperty("startTime") Instant startTime,
                    @JsonProperty("endTime") Instant endTime,
                    @JsonProperty("comment") String comment,
                    @JsonProperty("tags") Set<TagId> tags,
                    @JsonProperty("metricValues") Collection<MetricValue> metricValues) {

        super(id, version, softDeleted, creatorId);
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.comment = comment;
        this.tags = tags;
        this.metricValues = metricValues;
    }

    public String title() {
        return title;
    }

    public Instant startTime() {
        return startTime;
    }

    public Instant endTime() {
        return endTime;
    }

    public String comment() {
        return comment;
    }

    public Set<TagId> tags() {
        return tags;
    }

    public Collection<MetricValue> metricValues() {
        return metricValues;
    }
}
