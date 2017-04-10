package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.sakaiproject.ddo.tool.panels.LazyLoadStatPanel;
import org.sakaiproject.ddo.utils.StatisticType;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dbauer1 on 4/7/17.
 */
public class StatisticsPage extends BasePage {

    private Date startDate;
    private Date endDate;

    public StatisticsPage() {
        disableLink(statisticsPageLink);

        // Testing ... get data for the last three months
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.add(Calendar.MONTH, -3);
        startDate = now.getTime();
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        // Example of first panel
        WebMarkupContainer exampleWebMarkupContainer = new WebMarkupContainer("number-of-submissions");

        exampleWebMarkupContainer.add(new Label("number-of-submissions-label", StatisticType.NUMBEROFSUBMISSION.getStatisticName()));
        exampleWebMarkupContainer.add(new LazyLoadStatPanel("statistic-panel", StatisticType.NUMBEROFSUBMISSION, startDate, endDate));

        add(exampleWebMarkupContainer);
    }
}
