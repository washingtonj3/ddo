package org.sakaiproject.ddo.tool.panels;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.sakaiproject.ddo.utils.StatisticType;

import java.util.Date;

/**
 * Created by dbauer1 on 4/7/17.
 */
public class LazyLoadStatPanel extends AjaxLazyLoadPanel {
    private StatisticType statisticType;
    private Date startDate;
    private Date endDate;
    private String statusString;

    public LazyLoadStatPanel(String id, StatisticType statisticType, Date startDate, Date endDate, String status) {
        super(id);

        this.statisticType = statisticType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.statusString = status;
    }

    @Override
    public Component getLazyLoadComponent(String panelId) {
        switch (this.statisticType) {
            case NUMBEROFSUBMISSION:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate, this.statusString);
            case NUMBEROFUNIQUEUSERS:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate, this.statusString);
            case NUMBEROFREPEATUSERS:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate, this.statusString);
            case AVGNUMBEROFSUBMISSIONS:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate, this.statusString);
            case NUMBEROFCONSULTANTS:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate, this.statusString);
            case NUMBEROFREVIEWSPERCONSULTANT:
                return new StatTablePanel(panelId, this.statisticType, this.startDate, this.endDate);
            case AVGTURNAROUNDTIME:
                return new StatPanel(panelId, this.statisticType, this.startDate, this.endDate, this.statusString);
            case TOPTHREEINSTURCTORS:
                return new StatTablePanel(panelId, this.statisticType, this.startDate, this.endDate);
            case TOPTHREESECTIONS:
                return new StatTablePanel(panelId, this.statisticType, this.startDate, this.endDate);
            default:
                return null;
        }
    }
}