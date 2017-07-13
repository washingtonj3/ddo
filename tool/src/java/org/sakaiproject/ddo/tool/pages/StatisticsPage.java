package org.sakaiproject.ddo.tool.pages;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.*;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;
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
import org.apache.wicket.spring.injection.annot.SpringBean;;
import org.sakaiproject.ddo.logic.ProjectLogic;
import org.sakaiproject.ddo.logic.SakaiProxy;
import org.sakaiproject.ddo.model.Export;
import org.sakaiproject.ddo.tool.panels.LazyLoadStatPanel;
import org.sakaiproject.ddo.utils.StatisticType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import org.apache.wicket.util.resource.FileResourceStream;

/**
 * Created by dbauer1 on 4/7/17.
 */
public class StatisticsPage extends BasePage {

    @SpringBean(name="org.sakaiproject.ddo.logic.ProjectLogic")
    protected ProjectLogic projectLogic;
    @SpringBean(name="org.sakaiproject.ddo.logic.SakaiProxy")
    protected SakaiProxy sakaiProxy;
    private Date startDate;
    private Date endDate;
    private Date modifiedStartDate;
    private Date modifiedEndDate;
    private Date startDateExport;
    private Date endDateExport;
    private Date modifiedStartDateExport;
    private Date modifiedEndDateExport;
    private String status;
    private DateTextField startDatePicker;
    private DateTextField endDatePicker;
    private DateTextField startDatePickerExport;
    private DateTextField endDatePickerExport;
    private DropDownChoice<String> statusSet;
    boolean includeSubmissionId = true;
    boolean includeDocumentRef = true;
    boolean includeSubmissionDate = true;
    boolean includeSubmittedBy = true;
    boolean includeStatus = true;
    boolean includeAssignmentTitle = true;
    boolean includeInstructorRequireMents = true;
    boolean includeCourseTitle = true;
    boolean includeInstructor = true;
    boolean includeDueDate = true;
    boolean includePrimaryLanguageIsEnglish = true;
    boolean includePrimaryLanguage = true;
    boolean includeFeedBackFocus = true;
    boolean includeFeedbackId = true;
    boolean includeReviewedBy = true;
    boolean includeReviewDate = true;
    boolean includeComments = true;
    boolean includeReviewDocumentRef = true;
    boolean feedbackExists = true;
    boolean exportExcelFormat = true;
    Long feedbackIdHolder;
    private int rowCounter = 0;
    private int index = 0;
    private String holder = "";
    Calendar startDateModder = Calendar.getInstance();
    Calendar startDateExportModder = Calendar.getInstance();
    Calendar endDateModder = Calendar.getInstance();//Used to add the hours that are stripped off by the date pickers so the querys check up to the end of the day instead of the start of the day
    Calendar endDateExportModder = Calendar.getInstance();
    Calendar test = Calendar.getInstance();

    enum ExportFormat {
        CSV,
        XLS
    }

    public StatisticsPage() {
        Calendar now = Calendar.getInstance();
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentMonth = currentMonth + 1;//For clarity in switch statement
        endDate = now.getTime();
        endDateExport = now.getTime();
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
            case 8:  now.add(Calendar.MONTH, -3);
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
        startDateExport = now.getTime();
        status = "";
        startDateModder.setTime(startDate);
        this.startDateModder.set(Calendar.HOUR_OF_DAY, 0);
        this.startDateModder.set(Calendar.MINUTE, 0);
        this.startDateModder.set(Calendar.SECOND, 1);
        modifiedStartDate = startDateModder.getTime();
        endDateModder.setTime(endDate);
        this.endDateModder.set(Calendar.HOUR_OF_DAY, 23);
        this.endDateModder.set(Calendar.MINUTE, 59);
        this.endDateModder.set(Calendar.SECOND, 59);
        modifiedEndDate = endDateModder.getTime();
    }

    public StatisticsPage(Date startDate, Date endDate, String status, Date startDateExport, Date endDateExport) {
        test.setTime(endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.startDateExport = startDateExport;
        this.endDateExport = endDateExport;
        startDateModder.setTime(startDate);
        this.startDateModder.set(Calendar.HOUR_OF_DAY, 0);
        this.startDateModder.set(Calendar.MINUTE, 0);
        this.startDateModder.set(Calendar.SECOND, 1);
        modifiedStartDate = startDateModder.getTime();
        endDateModder.setTime(endDate);
        this.endDateModder.set(Calendar.HOUR_OF_DAY, 23);
        this.endDateModder.set(Calendar.MINUTE, 59);
        this.endDateModder.set(Calendar.SECOND, 59);
        modifiedEndDate = endDateModder.getTime();
    }

    public StatisticsPage(Date startDate, Date endDate, String status, Date startDateExport, Date endDateExport , int on) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.startDateExport = startDateExport;
        this.endDateExport = endDateExport;
        startDateModder.setTime(startDate);
        this.startDateModder.set(Calendar.HOUR_OF_DAY, 0);
        this.startDateModder.set(Calendar.MINUTE, 0);
        this.startDateModder.set(Calendar.SECOND, 1);
        modifiedStartDate = startDateModder.getTime();
        endDateModder.setTime(endDate);
        this.endDateModder.set(Calendar.HOUR_OF_DAY, 23);
        this.endDateModder.set(Calendar.MINUTE, 59);
        this.endDateModder.set(Calendar.SECOND, 59);
        modifiedEndDate = endDateModder.getTime();
    }
    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        add(new Label("statisticsHeader", getString("statistics.header")));
        add(new Label("statisticsUsageHeader", getString("statistics.totalsHeader")));
        add(new Label("statisticsConsultantHeader", getString("statistics.consultantHeader")));

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
                setResponsePage(new StatisticsPage(startDatePicker.getModelObject(), endDatePicker.getModelObject(), status, startDatePickerExport.getModelObject(),endDatePickerExport.getModelObject()));
            }
        };
        add(StatisticsDateForm);

        final Form<?> exportForm = new Form<Void>("exportForm") {
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
                File file = buildExcelFile(startDatePickerExport.getModelObject(), endDatePickerExport.getModelObject());
                IResourceStream fileResourceStream = new FileResourceStream(file);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(fileResourceStream)
                        .setFileName(file.getName())
                        .setContentDisposition(ContentDisposition.ATTACHMENT)
                );
            }
        };
        add(exportForm);

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

        startDatePickerExport =new DateTextField("startDatePickerExport",new PropertyModel<Date>(this, "startDateExport"));
        DatePicker datePickerStartExport = new DatePicker();
        datePickerStartExport.setShowOnFieldClick(true);
        datePickerStartExport.setAutoHide(true);
        startDatePickerExport.add(datePickerStartExport);
        add(new Label("startDatePickerExport", startDatePickerExport));
        exportForm.add(startDatePickerExport);

        endDatePickerExport =new DateTextField("endDatePickerExport",new PropertyModel<Date>(this, "endDateExport"));
        DatePicker datePickerEndExport = new DatePicker();
        datePickerEndExport.setShowOnFieldClick(true);
        datePickerEndExport.setAutoHide(true);
        endDatePickerExport.add(datePickerEndExport);
        add(new Label("endDatePickerExport", endDatePickerExport));
        exportForm.add(endDatePickerExport);

        exportForm.add(new Label("downloadGuide", getString("exportPanel.Header")));
        exportForm.add(new Label("downloadInfo", getString("exportpanel.info")));

        SubmitLink submit = new SubmitLink("submitLink");
        StatisticsDateForm.add(submit);
        SubmitLink submit2 = new SubmitLink("submitLink2");
        exportForm.add(submit2);

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
        exampleWebMarkupContainer.add(new LazyLoadStatPanel("statistic-panel", StatisticType.NUMBEROFSUBMISSION, modifiedStartDate, modifiedEndDate, status));
        add(exampleWebMarkupContainer);

        WebMarkupContainer uniqueUsersContainer = new WebMarkupContainer("unique-users");
        uniqueUsersContainer.add(new Label("unique-users-label", StatisticType.NUMBEROFUNIQUEUSERS.getStatisticName()));
        uniqueUsersContainer.add(new LazyLoadStatPanel("unique-users-panel", StatisticType.NUMBEROFUNIQUEUSERS, modifiedStartDate, modifiedEndDate, status));
        add(uniqueUsersContainer);

        WebMarkupContainer repeatUsersContainer = new WebMarkupContainer("repeat-users");
        repeatUsersContainer.add(new Label("repeat-users-label", StatisticType.NUMBEROFREPEATUSERS.getStatisticName()));
        repeatUsersContainer.add(new LazyLoadStatPanel("repeat-users-panel", StatisticType.NUMBEROFREPEATUSERS, modifiedStartDate, modifiedEndDate, status));
        add(repeatUsersContainer);

        WebMarkupContainer avgSubmissionsContainer = new WebMarkupContainer("average-submissions");
        avgSubmissionsContainer.add(new Label("average-submissions-label", StatisticType.AVGNUMBEROFSUBMISSIONS.getStatisticName()));
        avgSubmissionsContainer.add(new LazyLoadStatPanel("average-submissions-panel", StatisticType.AVGNUMBEROFSUBMISSIONS, modifiedStartDate, modifiedEndDate, status));
        add(avgSubmissionsContainer);

        WebMarkupContainer numberOfConsultantsContainer = new WebMarkupContainer("number-of-consultants");
        numberOfConsultantsContainer.add(new Label("number-of-consultants-label", StatisticType.NUMBEROFCONSULTANTS.getStatisticName()));
        numberOfConsultantsContainer.add(new LazyLoadStatPanel("number-of-consultants-panel", StatisticType.NUMBEROFCONSULTANTS, modifiedStartDate, modifiedEndDate, status));
        add(numberOfConsultantsContainer);

        WebMarkupContainer numberOfReviewsPerConsultantContainer = new WebMarkupContainer("number-of-reviews-per-consultant");
        numberOfReviewsPerConsultantContainer.add(new Label("number-of-reviews-per-consultant-label", StatisticType.NUMBEROFREVIEWSPERCONSULTANT.getStatisticName()));
        numberOfReviewsPerConsultantContainer.add(new LazyLoadStatPanel("number-of-reviews-per-consultant-panel", StatisticType.NUMBEROFREVIEWSPERCONSULTANT, modifiedStartDate, modifiedEndDate, status));
        add(numberOfReviewsPerConsultantContainer);

        WebMarkupContainer avgTurnAroundTimeContainer = new WebMarkupContainer("average-turnaround-time");
        avgTurnAroundTimeContainer.add(new Label("average-turnaround-time-label", StatisticType.AVGTURNAROUNDTIME.getStatisticName()));
        avgTurnAroundTimeContainer.add(new LazyLoadStatPanel("average-turnaround-time-panel", StatisticType.AVGTURNAROUNDTIME, modifiedStartDate, modifiedEndDate, status));
        add(avgTurnAroundTimeContainer);

        WebMarkupContainer topThreeInstructorsContainer = new WebMarkupContainer("top-three-instructors");
        topThreeInstructorsContainer.add(new Label("top-three-instructors-label", StatisticType.TOPTHREEINSTURCTORS.getStatisticName()));
        topThreeInstructorsContainer.add(new LazyLoadStatPanel("top-three-instructors-panel", StatisticType.TOPTHREEINSTURCTORS, modifiedStartDate, modifiedEndDate, status));
        add(topThreeInstructorsContainer);

        WebMarkupContainer topThreeSectionsContainer = new WebMarkupContainer("top-three-sections");
        topThreeSectionsContainer.add(new Label("top-three-sections-label", StatisticType.TOPTHREESECTIONS.getStatisticName()));
        topThreeSectionsContainer.add(new LazyLoadStatPanel("top-three-sections-panel", StatisticType.TOPTHREESECTIONS, modifiedStartDate, modifiedEndDate, status));
        add(topThreeSectionsContainer);

    }

    private File buildExcelFile(Date exportStartDate, Date exportEndDate) {

        File tempFile;
        this.startDateExportModder.setTime(exportStartDate);
        this.startDateExportModder.set(Calendar.HOUR_OF_DAY, 0);
        this.startDateExportModder.set(Calendar.MINUTE, 0);
        this.startDateExportModder.set(Calendar.SECOND, 1);
        this.modifiedStartDateExport = startDateExportModder.getTime();
        this.endDateExportModder.setTime(exportEndDate);
        this.endDateExportModder.set(Calendar.HOUR_OF_DAY, 23);
        this.endDateExportModder.set(Calendar.MINUTE, 59);
        this.endDateExportModder.set(Calendar.SECOND, 59);
        this.modifiedEndDateExport = endDateExportModder.getTime();
        try {
            String prefix = buildFileNamePrefix();
            String suffix = buildFileNameSuffix();
            tempFile = File.createTempFile(prefix, suffix);



            final HSSFWorkbook wb = new HSSFWorkbook();

            // Create new sheet
            HSSFSheet mainSheet = wb.createSheet("Export");

            // Create Excel header
            final List<String> header = new ArrayList<String>();

            header.add("SUBMISSIONID");
            header.add("DOCUMENTREF");
            header.add("SUBMISSIONDATE");
            header.add("SUBMITTEDBY (Display Name)");
            header.add("SUBMITTEDBY (User Name)");
            header.add("SUBMITTEDBY (Banner ID)");
            header.add("STATUS");
            header.add("ASSIGNMENTTITLE");
            header.add("INSTRUCTORREQUIREMENTS");
            header.add("COURSETITLE (Roster ID)");
            header.add("COURSETITLE (Site title / Udayton ID)");
            header.add("INSTRUCTOR (Display Name)");
            header.add("INSTRUCTOR (Username)");
            header.add("INSTRUCTOR (Banner ID)");
            header.add("DUEDATE");
            header.add("PRIMARYLANGUAGEISENGLISH");
            header.add("PRIMARYLANGUAGE");
            header.add("FEEDBACKFOCUS");
            header.add("FEEDBACKID");
            header.add("REVIEWEDBY (Display Name)");
            header.add("REVIEWEDBY (User Name)");
            header.add("REVIEWEDBY (Banner ID)");
            header.add("REVIEWDATE");
            header.add("COMMENTS");
            header.add("REVIEWEDDOCUMENTREF");

            // get list of assignments. this allows us to build the columns and then fetch the grades for each student for each assignment from the map
            final List<Export> assignments = projectLogic.statsGetAllSubmissionsLogic (exportStartDate, exportEndDate);

            HSSFFont boldFont = wb.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            boldFont.setUnderline(HSSFFont.U_SINGLE);
            HSSFCellStyle boldStyle = wb.createCellStyle();
            boldStyle.setFont(boldFont);

            // Create the Header row
            HSSFRow headerRow = mainSheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                HSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(header.get(i));
                cell.setCellType(cell.CELL_TYPE_STRING);
                cell.setCellStyle(boldStyle);
            }

            final int[] rowCount = {1};
            final List<Export> statisticsSheet = projectLogic.statsGetAllSubmissionsLogic (modifiedStartDateExport, modifiedEndDateExport);
            final int[] cellCount = {0};
            rowCounter = 0;


            Collections.sort(statisticsSheet, new Comparator<Export>() {
                @Override
                public int compare(Export statisticsSheet, Export t1) {
                    Long statisticsSheetLong = statisticsSheet.getSubmissionId();
                    Long t1Long = t1.getSubmissionId();
                    return statisticsSheetLong.compareTo(t1Long);
                }
            });

            statisticsSheet.forEach(SUBMISSIONID -> {

                HSSFRow row = mainSheet.createRow(rowCount[0]);

                cellCount[0] = 0;

                if (this.includeSubmissionId) {
                    this.holder = String.valueOf(statisticsSheet.get(this.rowCounter).getSubmissionId());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeDocumentRef) {
                    this.holder = statisticsSheet.get(this.rowCounter).getDocumentRef();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if ((this.includeSubmissionDate)) {
                    this.holder = statisticsSheet.get(this.rowCounter).getSubmissionDate().toString();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeSubmittedBy) {
                    this.holder = sakaiProxy.getUserDisplayName(statisticsSheet.get(this.rowCounter).getSubmittedBy());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);;
                    cellCount[0]++;
                }
                if (this.includeSubmittedBy) {
                    this.holder = sakaiProxy.getUserDisplayId(statisticsSheet.get(this.rowCounter).getSubmittedBy());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);;
                    cellCount[0]++;
                }
                if (this.includeSubmittedBy) {
                    this.holder = statisticsSheet.get(this.rowCounter).getSubmittedBy();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);;
                    cellCount[0]++;
                }
                if (this.includeStatus) {
                    this.holder = statisticsSheet.get(this.rowCounter).getStatus();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeAssignmentTitle) {
                    this.holder = statisticsSheet.get(this.rowCounter).getAssignmentTitle();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeInstructorRequireMents) {
                    this.holder = statisticsSheet.get(this.rowCounter).getInstructorRequirements();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeCourseTitle) {
                    //this.holder = sakaiProxy.getCourseOfferingTitleForSection(statisticsSheet2.get(this.rowCounter).getCourseTitle());
                    this.holder = statisticsSheet.get(this.rowCounter).getCourseTitle();//Line above crashes when it can't match a course and the user might not input the course properly.
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeCourseTitle) {
                    this.holder = statisticsSheet.get(this.rowCounter).getCourseTitle();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeInstructor) {
                    this.holder = sakaiProxy.getUserDisplayName(statisticsSheet.get(this.rowCounter).getInstructor());
                    if((statisticsSheet.get(this.rowCounter).getInstructor()).contains("(")){
                        this.holder = statisticsSheet.get(this.rowCounter).getInstructor();
                    }
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeInstructor) {
                    this.holder = sakaiProxy.getUserDisplayId(statisticsSheet.get(this.rowCounter).getInstructor());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeInstructor) {
                    this.holder = sakaiProxy.getUserEid(statisticsSheet.get(this.rowCounter).getInstructor());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeDueDate) {
                    this.holder = statisticsSheet.get(this.rowCounter).getDueDate().toString();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includePrimaryLanguageIsEnglish) {
                    if(statisticsSheet.get(this.rowCounter).getPrimaryLanguageIsEnglish()){
                        this.holder = "True";
                    }
                    else{
                        this.holder = "False";
                    }
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includePrimaryLanguage) {
                    this.holder = statisticsSheet.get(this.rowCounter).getPrimaryLanguage();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);;
                    cellCount[0]++;
                }
                if (this.includeFeedBackFocus) {
                    this.holder = statisticsSheet.get(this.rowCounter).getFeedbackFocus();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeFeedbackId) {
                    feedbackIdHolder = statisticsSheet.get(this.rowCounter).getFeedbackId();
                    this.holder = String.valueOf(feedbackIdHolder);
                    if(feedbackIdHolder == 0){
                        feedbackExists = false;
                    }
                    else {
                        feedbackExists = true;
                    }
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeReviewedBy) {
                    this.holder = sakaiProxy.getUserDisplayName(statisticsSheet.get(this.rowCounter).getReviewedBy());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeReviewedBy) {
                    this.holder = sakaiProxy.getUserDisplayId(statisticsSheet.get(this.rowCounter).getReviewedBy());
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeReviewedBy) {
                    this.holder = statisticsSheet.get(this.rowCounter).getReviewedBy();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeReviewDate) {
                    this.holder = statisticsSheet.get(this.rowCounter).getReviewDate().toString();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    if(feedbackExists){
                        cell.setCellValue(this.holder);
                        cell.setCellType(cell.CELL_TYPE_STRING);
                    }else{
                        cell.setCellValue("");
                        cell.setCellType(cell.CELL_TYPE_STRING);
                    }
                    feedbackExists = true;
                    cellCount[0]++;
                }
                if (this.includeComments) {
                    this.holder = statisticsSheet.get(this.rowCounter).getComments();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                if (this.includeReviewDocumentRef) {
                    this.holder = statisticsSheet.get(this.rowCounter).getReviewedDocumentRef();
                    HSSFCell cell = row.createCell(cellCount[0]);
                    cell.setCellValue(this.holder);
                    cell.setCellType(cell.CELL_TYPE_STRING);
                    cellCount[0]++;
                }
                rowCount[0]++;
                this.rowCounter++;
            });

            FileOutputStream fos = new FileOutputStream(tempFile);
            wb.write(fos);

            fos.close();
            wb.close();

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }
    private String buildFileNamePrefix() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy");
        String ddoExportName = (dateFormat.format(this.modifiedStartDateExport).toString()) + "---" + (dateFormat.format(this.modifiedEndDateExport).toString()) + "||";;
        return ddoExportName;
    }

    private String buildFileNameSuffix() {
        return "." + (this.exportExcelFormat ? StatisticsPage.ExportFormat.XLS.toString().toLowerCase() : StatisticsPage.ExportFormat.CSV.toString().toLowerCase());
    }
}
