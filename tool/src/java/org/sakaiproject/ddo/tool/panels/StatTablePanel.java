package org.sakaiproject.ddo.tool.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.list.ListView;
import org.sakaiproject.ddo.logic.ProjectLogic;

import org.sakaiproject.ddo.model.NumStatistics;
import org.sakaiproject.ddo.utils.StatisticType;
import org.apache.wicket.model.PropertyModel;
import java.util.Date;
import java.util.List;


/**
 * Created by dbauer1 on 4/7/17.org
 */
public class StatTablePanel extends Panel {

    @SpringBean(name="org.sakaiproject.ddo.logic.ProjectLogic")
    protected ProjectLogic projectLogic;
    private Date startDate;
    private Date endDate;
    private StatisticType statisticTypeString;

    public StatTablePanel(String id, StatisticType statisticType, Date startDateRaw, Date endDateRaw ) {
        super(id);
        this.statisticTypeString = statisticType;
        this.startDate = startDateRaw;
        this.endDate = endDateRaw;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        if (statisticTypeString == StatisticType.NUMBEROFREVIEWSPERCONSULTANT){
            final List<NumStatistics> numberOfReviewsPerConsultant = projectLogic.numberOfReviewsPerConsultantLogic(startDate, endDate);
            add(new Label("statisticTableId",getString("statistics.statTablePanel.consultantTableHead")));
            add(new Label("statisticTableCount",getString("statistics.statTablePanel.consultantTableCount")));
            add(new ListView<NumStatistics>("statTablePanels", numberOfReviewsPerConsultant) {
                @Override
                protected void populateItem(ListItem<NumStatistics> item) {
                    item.add(new Label("Id", new PropertyModel(item.getModel(), "Id")));
                    item.add(new Label("statisticCount", new PropertyModel(item.getModel(), "statisticCount")));
                }
            });
        }else if (statisticTypeString == StatisticType.TOPTHREEINSTURCTORS){
            final List<NumStatistics> topThreeInstructorsStatsList = projectLogic.topThreeInstructorsStatsLogic(startDate, endDate);
            add(new Label("statisticTableId",getString("statistics.statTablePanel.instructorName")));
            add(new Label("statisticTableCount",getString("statistics.statTablePanel.instructorCount")));
            add(new ListView<NumStatistics>("statTablePanels", topThreeInstructorsStatsList) {
                @Override
                protected void populateItem(ListItem<NumStatistics> item) {
                    item.add(new Label("Id", new PropertyModel(item.getModel(), "Id")));
                    item.add(new Label("statisticCount", new PropertyModel(item.getModel(), "statisticCount")));
                }
            });
        }else if (statisticTypeString == StatisticType.TOPTHREESECTIONS){
            final List<NumStatistics> topThreeSectionsStatsList = projectLogic.topThreeSectionsStatsLogic(startDate, endDate);
            add(new Label("statisticTableId",getString("statistics.statTablePanel.sectionHeader")));
            add(new Label("statisticTableCount",getString("statistics.statTablePanel.sectionCount")));
            add(new ListView<NumStatistics>("statTablePanels", topThreeSectionsStatsList) {
                @Override
                protected void populateItem(ListItem<NumStatistics> item) {
                    item.add(new Label("Id", new PropertyModel(item.getModel(), "Id")));
                    item.add(new Label("statisticCount", new PropertyModel(item.getModel(), "statisticCount")));
                }
            });
        }else {
            add(new Label("statisticTableId",getString("statistics.statTablePanel.error")));
            add(new Label("statisticTableCount",getString("statistics.statTablePanel.error")));
            add(new ListView<NumStatistics>("statTablePanels") {
                @Override
                protected void populateItem(ListItem<NumStatistics> item) {
                    item.add(new Label("Id", "ERROR"));
                    item.add(new Label("statisticCount", "ERROR"));
                }
            });
        }
    }
}
