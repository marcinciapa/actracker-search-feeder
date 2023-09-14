package ovh.equino.actracker.searchfeed.domain.services.dashboard;

import ovh.equino.actracker.searchfeed.domain.model.dashboard.*;
import ovh.equino.actracker.searchfeed.domain.services.EntityIndexer;

public final class DashboardIndexer extends EntityIndexer<DashboardId, Dashboard, DashboardGraph> {


    DashboardIndexer(DashboardStore dashboardStore, DashboardIndex dashboardIndex) {
        super(dashboardStore, dashboardIndex);
    }

    public void indexDashboard(Dashboard dashboardToIndex) {
        super.index(dashboardToIndex);
    }

    @Override
    protected DashboardGraph buildEntityGraph(Dashboard entity) {
        return new DashboardGraph();
    }
}
