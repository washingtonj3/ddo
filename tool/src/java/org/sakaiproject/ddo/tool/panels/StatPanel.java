package org.sakaiproject.ddo.tool.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.ddo.logic.ProjectLogic;
import org.sakaiproject.ddo.utils.StatisticType;

import java.util.Date;


/**
 * Created by dbauer1 on 4/7/17.
 */
public class StatPanel extends Panel {

    @SpringBean(name="org.sakaiproject.ddo.logic.ProjectLogic")
    protected ProjectLogic projectLogic;
    private Date startDate;
    private Date endDate;
    private String statusString;
    private StatisticType statisticTypeString;

    public StatPanel(String id, StatisticType statisticType, Date startDateRaw, Date endDateRaw, String status) {
        super(id);
        this.statisticTypeString = statisticType;
        this.startDate = startDateRaw;
        this.endDate = endDateRaw;
        this.statusString = status;
    }


    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        // Get the statistic based on type.
        if(statisticTypeString == StatisticType.NUMBEROFSUBMISSION){
            final int numberOfSubmissions = projectLogic.getNumberofSubmissionsLogic(startDate, endDate, statusString);
            add(new Label("statisticLabel", numberOfSubmissions));
        }else if (statisticTypeString == StatisticType.NUMBEROFUNIQUEUSERS){
            final int numberOfUniqueUsers = projectLogic.getNumberOfUniqueUsersLogic(startDate, endDate);
            add(new Label("statisticLabel", numberOfUniqueUsers));
        }else if (statisticTypeString == StatisticType.NUMBEROFREPEATUSERS){
            final int numberOfRepeatUsers = projectLogic.getNumberOfRepeatUsersLogic(startDate, endDate);
            add(new Label("statisticLabel", numberOfRepeatUsers));
        }else if (statisticTypeString == StatisticType.NUMBEROFCONSULTANTS){
            final int numberOfConsultants = projectLogic.getNumberOfConsultantsLogic(startDate, endDate);
            add(new Label("statisticLabel", numberOfConsultants));
        }else if (statisticTypeString == StatisticType.AVGTURNAROUNDTIME){
            final double avgTurnaroundTime = this.projectLogic.getAvgTurnaroundTimeLogic(startDate, endDate);
            final String turnAroundTimeString = projectLogic.millisecondsToTime(avgTurnaroundTime);
            add(new Label("statisticLabel", turnAroundTimeString));
        }else if (statisticTypeString == StatisticType.AVGNUMBEROFSUBMISSIONS){
            final double avgNumberOfSubmissions = this.projectLogic.getAvgNumberofSubmissionsLogic(startDate, endDate);
            if(avgNumberOfSubmissions == 0){ // If statement to change a 0.0 to a 0 for a better looking output.
                add(new Label("statisticLabel", 0));
            }
            else{
                add(new Label("statisticLabel", avgNumberOfSubmissions));
            }
        }else {
            add(new Label("statisticLabel", getString("statistics.statPanel.error")));
        }

    }

}