package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.sakaiproject.ddo.tool.panels.LazyLoadStatPanel;
import org.sakaiproject.ddo.utils.StatisticType;

import java.util.*;

/**
 * Created by dbauer1 on 4/7/17.
 */
public class StatisticsPage extends BasePage {
    private Date startDate;
    private Date endDate;
    private String status;
    private DateTextField startDatePicker;
    private DateTextField endDatePicker;
    private DropDownChoice<String> statusSet;

    public StatisticsPage() {
        Calendar now = Calendar.getInstance();
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentMonth = currentMonth + 1;//For clarity in switch statement
        endDate = now.getTime();
        switch (currentMonth) {//Sets the start date to the start of the current semester.
            case 1:  now.add(Calendar.MONTH, -0);//Start of the Spring semester;
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 2:  now.add(Calendar.MONTH, -1);
                break;
            case 3:  now.add(Calendar.MONTH, -2);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 4:  now.add(Calendar.MONTH, -3);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 5://End of the Spring semester and the start of the summer semester. Set the Split 11 days into the month to account for final papers.
                if (currentDay < 11){
                    now.add(Calendar.MONTH, -4);
                    now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                } else {
                    now.add(Calendar.MONTH, -0);
                    now.add(Calendar.DAY_OF_MONTH, -(currentDay - 11));
                }
                break;
            case 6:  now.add(Calendar.MONTH, -1);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 7:  now.add(Calendar.MONTH, -2);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 8:  now.add(Calendar.MONTH, -10);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 9:  now.add(Calendar.MONTH, -0); //Start of the Fall semester.
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 10: now.add(Calendar.MONTH, -1);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 11: now.add(Calendar.MONTH, -2);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            case 12: now.add(Calendar.MONTH, -3);
                now.add(Calendar.DAY_OF_MONTH, -(currentDay - 1));
                break;
            default: ;
                break;
        }
        startDate = now.getTime();
        status = "";
    }

    public StatisticsPage(Date startDate, Date endDate, String status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        add(new Label("statisicsHeader", getString("statistics.header")));
        add(new Label("statisicsUsageHeader", getString("statistics.totalsHeader")));
        add(new Label("statisicsConsultantHeader", getString("statistics.consultantHeader")));

        Link<Void> refreshPage = new Link<Void>("refreshPage") {
            public void onClick() {
                setResponsePage(new StatisticsPage());
            }
        };
        add(refreshPage);
        disableLink(statisticsPageLink);

        final Form<?> StatisticsDateForm = new Form<Void>("StatisticsDateForm") {
            @Override
            protected void onSubmit() {
                final List<String> allowedStatuses = new ArrayList<>();
                allowedStatuses.add("Awaiting Review");
                allowedStatuses.add("Under Review");
                allowedStatuses.add("Reviewed");
                allowedStatuses.add("Archived");

                String status = statusSet.getModelObject();
                if (!allowedStatuses.contains(status)) {
                    status = "";
                }
                setResponsePage(new StatisticsPage(startDatePicker.getModelObject(), endDatePicker.getModelObject(), status));
            }
        };
        add(StatisticsDateForm);

        startDatePicker =new DateTextField("startDatePicker",new PropertyModel<Date>(this, "startDate"));
        DatePicker datePickerStart = new DatePicker();
        datePickerStart.setShowOnFieldClick(true);
        datePickerStart.setAutoHide(true);
        startDatePicker.add(datePickerStart);
        add(new Label("startDatePicker", startDatePicker));
        StatisticsDateForm.add(startDatePicker);

        endDatePicker =new DateTextField("endDatePicker",new PropertyModel<Date>(this, "endDate"));
        DatePicker datePickerEnd = new DatePicker();
        datePickerEnd.setShowOnFieldClick(true);
        datePickerEnd.setAutoHide(true);
        endDatePicker.add(datePickerEnd);
        add(new Label("endDatePicker", endDatePicker));
        StatisticsDateForm.add(endDatePicker);

        SubmitLink submit = new SubmitLink("submitLink");
        StatisticsDateForm.add(submit);

        List<String> stringList = new ArrayList<String>();
        stringList.add("All");
        stringList.add("Awaiting Review");
        stringList.add("Under Review");
        stringList.add("Reviewed");
        stringList.add("Archived");


        DropDownChoice<String> iDD = new DropDownChoice<String>("statusSet", Model.of(status), stringList);
        iDD.setNullValid(false);
        StatisticsDateForm.add(statusSet = iDD);

        WebMarkupContainer exampleWebMarkupContainer = new WebMarkupContainer("number-of-submissions");
        exampleWebMarkupContainer.add(new Label("number-of-submissions-label", StatisticType.NUMBEROFSUBMISSION.getStatisticName()));
        exampleWebMarkupContainer.add(new LazyLoadStatPanel("statistic-panel", StatisticType.NUMBEROFSUBMISSION, startDate, endDate, status));
        add(exampleWebMarkupContainer);

        WebMarkupContainer uniqueUsersContainer = new WebMarkupContainer("unique-users");
        uniqueUsersContainer.add(new Label("unique-users-label", StatisticType.NUMBEROFUNIQUEUSERS.getStatisticName()));
        uniqueUsersContainer.add(new LazyLoadStatPanel("unique-users-panel", StatisticType.NUMBEROFUNIQUEUSERS, startDate, endDate, status));
        add(uniqueUsersContainer);

        WebMarkupContainer repeatUsersContainer = new WebMarkupContainer("repeat-users");
        repeatUsersContainer.add(new Label("repeat-users-label", StatisticType.NUMBEROFREPEATUSERS.getStatisticName()));
        repeatUsersContainer.add(new LazyLoadStatPanel("repeat-users-panel", StatisticType.NUMBEROFREPEATUSERS, startDate, endDate, status));
        add(repeatUsersContainer);

        WebMarkupContainer avgSubmissionsContainer = new WebMarkupContainer("average-submissions");
        avgSubmissionsContainer.add(new Label("average-submissions-label", StatisticType.AVGNUMBEROFSUBMISSIONS.getStatisticName()));
        avgSubmissionsContainer.add(new LazyLoadStatPanel("average-submissions-panel", StatisticType.AVGNUMBEROFSUBMISSIONS, startDate, endDate, status));
        add(avgSubmissionsContainer);

        WebMarkupContainer numberOfConsultantsContainer = new WebMarkupContainer("number-of-consultants");
        numberOfConsultantsContainer.add(new Label("number-of-consultants-label", StatisticType.NUMBEROFCONSULTANTS.getStatisticName()));
        numberOfConsultantsContainer.add(new LazyLoadStatPanel("number-of-consultants-panel", StatisticType.NUMBEROFCONSULTANTS, startDate, endDate, status));
        add(numberOfConsultantsContainer);

        WebMarkupContainer numberOfReviewsPerConsultantContainer = new WebMarkupContainer("number-of-reviews-per-consultant");
        numberOfReviewsPerConsultantContainer.add(new Label("number-of-reviews-per-consultant-label", StatisticType.NUMBEROFREVIEWSPERCONSULTANT.getStatisticName()));
        numberOfReviewsPerConsultantContainer.add(new LazyLoadStatPanel("number-of-reviews-per-consultant-panel", StatisticType.NUMBEROFREVIEWSPERCONSULTANT, startDate, endDate, status));
        add(numberOfReviewsPerConsultantContainer);

        WebMarkupContainer avgTurnAroundTimeContainer = new WebMarkupContainer("average-turnaround-time");
        avgTurnAroundTimeContainer.add(new Label("average-turnaround-time-label", StatisticType.AVGTURNAROUNDTIME.getStatisticName()));
        avgTurnAroundTimeContainer.add(new LazyLoadStatPanel("average-turnaround-time-panel", StatisticType.AVGTURNAROUNDTIME, startDate, endDate, status));
        add(avgTurnAroundTimeContainer);

        WebMarkupContainer topThreeInstructorsContainer = new WebMarkupContainer("top-three-instructors");
        topThreeInstructorsContainer.add(new Label("top-three-instructors-label", StatisticType.TOPTHREEINSTURCTORS.getStatisticName()));
        topThreeInstructorsContainer.add(new LazyLoadStatPanel("top-three-instructors-panel", StatisticType.TOPTHREEINSTURCTORS, startDate, endDate, status));
        add(topThreeInstructorsContainer);

        WebMarkupContainer topThreeSectionsContainer = new WebMarkupContainer("top-three-sections");
        topThreeSectionsContainer.add(new Label("top-three-sections-label", StatisticType.TOPTHREESECTIONS.getStatisticName()));
        topThreeSectionsContainer.add(new LazyLoadStatPanel("top-three-sections-panel", StatisticType.TOPTHREESECTIONS, startDate, endDate, status));
        add(topThreeSectionsContainer);

    }
}
