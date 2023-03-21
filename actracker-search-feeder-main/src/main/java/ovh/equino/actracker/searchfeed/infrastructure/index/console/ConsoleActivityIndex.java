package ovh.equino.actracker.searchfeed.infrastructure.index.console;

import ovh.equino.actracker.searchfeed.domain.model.activity.Activity;
import ovh.equino.actracker.searchfeed.domain.model.activity.ActivityIndex;

class ConsoleActivityIndex implements ActivityIndex {

    @Override
    public void index(Activity activity) {
        System.out.printf("Indexed activity: %s%n", activity.id());
    }
}