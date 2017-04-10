package org.sakaiproject.ddo.tool.panels;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.sakaiproject.ddo.utils.StatisticType;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dbauer1 on 4/7/17.
 */
public class LazyLoadStatPanel extends AjaxLazyLoadPanel {
    private StatisticType statisticType;
    private Date startDate;
    private Date endDate;

    public LazyLoadStatPanel(String id, StatisticType statisticType, Date startDate, Date endDate) {
        super(id);

        this.statisticType = statisticType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Component getLazyLoadComponent(String panelId) {

        try {
            // Testing ... delay to show functionality
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (this.statisticType) {
            case NUMBEROFSUBMISSION:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate);
            default:
                return null;
        }
    }
}
