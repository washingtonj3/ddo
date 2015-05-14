package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;

import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Bytes;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.user.api.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by David P. Bauer on 12/10/14.
 */
public class DropOffForm extends BasePage {

    private final CheckBox primaryLanguageIsEnglish;
    private final TextField<String> primaryLanguage;
    private final TextField<String> assignmentTitle;
    private final TextArea<String> instructorRequirements;
    private final DateTextField dueDate;
    private final DropDownChoice<String> courseTitle;
    private final DropDownChoice<String> instructors;
    private final TextArea<String> feedbackFocus;

    private final FileUploadField uploadField;

    private final Date date = new Date();

    public DropOffForm() {
        disableLink(studentOverviewLink);

        // create the form
        final Form<?> dropOffForm = new Form<Void>("dropOffForm") {
            /**
             * @see org.apache.wicket.markup.html.form.Form#onSubmit()
             */
            @Override
            protected void onSubmit() {
                String currentUserId = sakaiProxy.getCurrentUserId();

                Submission s = new Submission();

                s.setPrimaryLanguageIsEnglish(primaryLanguageIsEnglish.getModelObject());
                s.setPrimaryLanguage(primaryLanguage.getModelObject());
                s.setAssignmentTitle(assignmentTitle.getModelObject());
                s.setInstructorRequirements(instructorRequirements.getModelObject());
                s.setDueDate(dueDate.getModelObject());
                s.setCourseTitle(courseTitle.getModelObject());
                s.setInstructor(instructors.getModelObject());
                s.setFeedbackFocus(feedbackFocus.getModelObject());

                FileUpload file = uploadField.getFileUpload();

                if (file == null) {
                    error(getString("error.null_file"));
                    return;
                } else if (file.getSize() == 0) {
                    error(getString("error.empty_file"));
                    return;
                } else {

                    String mimeType = file.getContentType();
                    String fileName = file.getClientFileName();

                    //ok so get bytes of file uploaded
                    byte[] documentBytes = file.getBytes();

                    //create resource id
                    String documentRef = sakaiProxy.getDocumentResourcePath(fileName);

                    if (!sakaiProxy.saveFile(documentRef, currentUserId, fileName, mimeType, documentBytes)) {
                        error(getString("error.save"));
                        return;
                    } else {
                        s.setDocumentRef(documentRef);
                    }

                    s.setSubmittedBy(currentUserId);
                    s.setStatus(Submission.STATUS_WAITING);

                    if (projectLogic.addSubmission(s)) {
                        getSession().info(getString("success.save_submission"));
                        setResponsePage(new StudentOverview());
                    } else {
                        error(getString("error.save_submission"));
                    }
                }
            }
        };

        final String userid = sakaiProxy.getCurrentUserId();

        dropOffForm.setMaxSize(Bytes.megabytes(15));
        add(dropOffForm);

        dropOffForm.add(primaryLanguageIsEnglish = new CheckBox("primaryLanguageIsEnglish", Model.of(Boolean.TRUE)));
        dropOffForm.add(primaryLanguage = new TextField<String>("primaryLanguage", new Model<String>()));
        dropOffForm.add(assignmentTitle = new TextField<String>("assignmentTitle", new Model<String>()));
        dropOffForm.add(instructorRequirements = new TextArea<String>("instructorRequirements", new Model<String>()));

        Set<String> sectionsSet = sakaiProxy.getCurrentSectionsForCurrentUser();
        List<String> sectionsList = new ArrayList<String>();

        if(!sectionsSet.isEmpty()) {
            sectionsList = new ArrayList<String>(sectionsSet);
        }
        DropDownChoice<String> sDD = new DropDownChoice<String>("courseTitle", sectionsList,
                new IChoiceRenderer<String>() {
                    @Override
                    public Object getDisplayValue(String section) {
                       return sakaiProxy.getCourseOfferingTitle(section);
                    }

                    @Override
                    public String getIdValue(String section, int i) {
                        return section;
                    }
                });

        dropOffForm.add(courseTitle = sDD);

        Set<String> instructorSet = sakaiProxy.getCurrentInstructorsForCurrentUser();
        List<String> instructorList = new ArrayList<String>();

        if(!instructorSet.isEmpty()) {
            instructorList = new ArrayList<String>(instructorSet);
        }

        DropDownChoice<String> iDD = new DropDownChoice<String>("instructors", instructorList,
                new IChoiceRenderer<String>() {
                    @Override
                    public Object getDisplayValue(String userId) {
                        return sakaiProxy.getUserDisplayName(userId);
                    }

                    @Override
                    public String getIdValue(String userId, int i) {
                        return userId;
                    }
                });

        dropOffForm.add(instructors = iDD);

        dropOffForm.add(feedbackFocus = new TextArea<String>("feedbackFocus", new Model<String>()));

        dueDate = new DateTextField("dueDate", new PropertyModel<Date>(this, "date"));
        DatePicker datePicker = new DatePicker();
        datePicker.setShowOnFieldClick(true);
        datePicker.setAutoHide(true);
        dueDate.add(datePicker);
        dropOffForm.add(dueDate);

        assignmentTitle.setRequired(true);
        instructors.setRequired(true);
        courseTitle.setRequired(true);
        dueDate.setRequired(true);

        dropOffForm.add(uploadField = new FileUploadField("uploadField"));
        uploadField.setRequired(true);

        dropOffForm.add(new Label("max", new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return dropOffForm.getMaxSize().toString();
            }
        }));

        Link<Void> cancel = new Link<Void>("cancelLink") {
            public void onClick() {
                setResponsePage(new StudentOverview());
            }
        };
        dropOffForm.add(cancel);

        Link<Void> cancel2 = new Link<Void>("cancelLink2") {
            public void onClick() {
                setResponsePage(new StudentOverview());
            }
        };
        add(cancel2);

        SubmitLink submit = new SubmitLink("submitLink");
        dropOffForm.add(submit);

        dropOffForm.add(new Label("displayName", sakaiProxy.getCurrentUserDisplayName()));
        dropOffForm.add(new Label("email", sakaiProxy.getUserEmail(userid)));
        dropOffForm.add(new Label("username", sakaiProxy.getUserDisplayId(userid)));
    }

}
