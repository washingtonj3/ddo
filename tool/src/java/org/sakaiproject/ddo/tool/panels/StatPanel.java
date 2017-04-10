package org.sakaiproject.ddo.tool.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.sakaiproject.ddo.utils.StatisticType;

import java.util.Date;

/**
 * Created by dbauer1 on 4/7/17.
 */
public class StatPanel extends Panel {

    public StatPanel(String id, StatisticType statisticType, Date startDate, Date endDate) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        // Get the statistic based on type.
    }
}
