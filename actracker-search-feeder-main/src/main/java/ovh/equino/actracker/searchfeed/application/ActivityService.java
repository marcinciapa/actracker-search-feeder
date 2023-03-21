package ovh.equino.actracker.searchfeed.application;

import ovh.equino.actracker.searchfeed.domain.model.activity.Activity;
import ovh.equino.actracker.searchfeed.domain.model.activity.ActivityId;
import ovh.equino.actracker.searchfeed.domain.model.activity.ActivityIndex;
import ovh.equino.actracker.searchfeed.domain.model.activity.ActivityStore;

import java.util.Optional;

public class ActivityService {

    private final ActivityIndex activityIndex;
    private final ActivityStore activityStore;

    ActivityService(ActivityIndex activityIndex, ActivityStore activityStore) {
        this.activityIndex = activityIndex;
        this.activityStore = activityStore;
    }

    public void indexActivity(Activity activity) {
        ActivityId activityId = activity.id();
        Optional<Activity> storedActivity = activityStore.get(activityId);

        storedActivity.ifPresentOrElse(
                (stored) -> reindexIfOutdated(stored, activity),
                () -> index(activity)
        );
    }

    private void reindexIfOutdated(Activity stored, Activity activity) {
        if (activity.shouldReplace(stored)) {
            index(activity);
        }
    }

    private void index(Activity activity) {
        activityStore.put(activity.id(), activity);
        activityIndex.index(activity);
    }
}