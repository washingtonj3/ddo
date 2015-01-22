package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;

import org.apache.wicket.util.lang.Bytes;
import org.sakaiproject.ddo.model.Submission;

import java.util.Date;

/**
 * Created by dbauer1 on 12/10/14.
 */
public class DropOffForm extends BasePage {

    private final CheckBox primaryLanguageIsEnglish;
    private final TextField<String> primaryLanguage;
    private final TextField<String> assignmentTitle;
    private final TextArea<String> instructorRequirements;
    private final TextField<Date> dueDate;
    private final TextField<String> courseTitle;
    private final TextField<String> instructor;
    private final TextArea<String> feedbackFocus;

    private final FileUploadField uploadField;


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
                s.setInstructor(instructor.getModelObject());
                s.setFeedbackFocus(feedbackFocus.getModelObject());

                FileUpload file = uploadField.getFileUpload();

                if (file == null) {
                    error("You must attach a document to be reviewed.");
                    return;
                } else if (file.getSize() == 0) {
                    error("The attached file is empty. Please try submitting a different document.");
                    return;
                } else {

                    String mimeType = file.getContentType();
                    String fileName = file.getClientFileName();

                    //ok so get bytes of file uploaded
                    byte[] documentBytes = file.getBytes();

                    //create resource id
                    String documentRef = sakaiProxy.getDocumentResourcePath(fileName);

                    if (!sakaiProxy.saveFile(documentRef, currentUserId, fileName, mimeType, documentBytes)) {
                        error("Unable to save the document to Isidore. Please try again.");
                        return;
                    } else {
                        s.setDocumentRef(documentRef);
                    }

                    s.setSubmittedBy(currentUserId);
                    s.setStatus(Submission.STATUS_WAITING);

                    if (projectLogic.addSubmission(s)) {
                        setResponsePage(new StudentOverview());
                        feedbackPanel.info("Item added");
                    } else {
                        error("Error adding item");
                    }
                }
            }
        };

        dropOffForm.setMaxSize(Bytes.megabytes(2));
        add(dropOffForm);

        dropOffForm.add(primaryLanguageIsEnglish = new CheckBox("primaryLanguageIsEnglish", Model.of(Boolean.TRUE)));
        dropOffForm.add(primaryLanguage = new TextField<String>("primaryLanguage", new Model<String>()));
        dropOffForm.add(assignmentTitle = new TextField<String>("assignmentTitle", new Model<String>()));
        dropOffForm.add(instructorRequirements = new TextArea<String>("instructorRequirements", new Model<String>()));
        dropOffForm.add(dueDate = new TextField<Date>("dueDate", new Model<Date>()));
        dropOffForm.add(courseTitle = new TextField<String>("courseTitle", new Model<String>()));
        dropOffForm.add(instructor = new TextField<String>("instructor", new Model<String>()));
        dropOffForm.add(feedbackFocus = new TextArea<String>("feedbackFocus", new Model<String>()));

        dropOffForm.add(uploadField = new FileUploadField("uploadField"));

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
    }

}
