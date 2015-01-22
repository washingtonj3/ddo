package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by dbauer1 on 12/17/14.
 */
public class FeedbackFormPage extends BasePage {

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    protected Submission s = new Submission();

    private final FileUploadField uploadField;
    private final TextArea<String> comments;

    public FeedbackFormPage(long submissionId) {
        disableLink(staffOverviewLink);
        this.s = projectLogic.getSubmission(submissionId);

        s.setStatus(Submission.STATUS_UNDER);
        projectLogic.updateSubmissionStatus(s);

        add(new Label("name", sakaiProxy.getUserDisplayName(s.getSubmittedBy())));
        add(new Label("primaryLanguageIsEnglish",s.getPrimaryLanguageIsEnglish()));
        add(new Label("primaryLanguage",s.getPrimaryLanguage()));
        add(new Label("submissionDate",df.format(s.getSubmissionDate())));

        Link<Void> streamDownloadLink = new Link<Void>("document") {

            @Override
            public void onClick() {

                AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

                    @Override
                    public void write(OutputStream output) throws IOException {
                        output.write(sakaiProxy.getResource(s.getDocumentRef()).getBytes());
                    }
                };

                ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, sakaiProxy.getResource(s.getDocumentRef()).getFileName());
                getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
            }
        };

        add(streamDownloadLink);
        streamDownloadLink.add(new Label("fileName", sakaiProxy.getResource(s.getDocumentRef()).getFileName()));

        add(new Label("assignmentTitle", s.getAssignmentTitle()));
        add(new Label("course", s.getCourseTitle()));
        add(new Label("instructor", s.getInstructor()));
        add(new Label("dueDate", s.getDueDate()!=null ? df.format(s.getDueDate()) : "No due date added"));
        add(new MultiLineLabel("feedbackFocus", s.getFeedbackFocus()));
        add(new MultiLineLabel("instructorRequirements", s.getInstructorRequirements()));

        final Form<?> feedbackForm = new Form<Void>("feedbackForm") {
            /**
             * @see org.apache.wicket.markup.html.form.Form#onSubmit()
             */
            @Override
            protected void onSubmit() {
                String currentUserId = sakaiProxy.getCurrentUserId();

                Feedback f = new Feedback();

                f.setComments(comments.getModelObject());

                FileUpload file = uploadField.getFileUpload();

                if (file == null) {
                    f.setReviewedDocumentRef("");
                } else if (file.getSize() == 0) {
                    error("Problem: file was empty");
                    return;
                } else {

                    String mimeType = file.getContentType();
                    String fileName = file.getClientFileName();

                    //ok so get bytes of file uploaded
                    byte[] documentBytes = file.getBytes();

                    //create resource id
                    String documentRef = sakaiProxy.getDocumentResourcePath(fileName);

                    if (!sakaiProxy.saveFile(documentRef, currentUserId, fileName, mimeType, documentBytes)) {
                        error("Couldn't add file to CHS.");
                        return;
                    } else {
                        f.setReviewedDocumentRef(documentRef);
                    }

                }

                f.setSubmissionId(s.getSubmissionId());
                f.setReviewedBy(currentUserId);
                s.setStatus(Submission.STATUS_REVIEWED);

                if(projectLogic.addFeedback(f) && projectLogic.updateSubmissionStatus(s)){
                    info("Item added");
                    setResponsePage(new StaffOverview());
                } else {
                    error("Error adding item");
                }
            }
        };

        feedbackForm.setMaxSize(Bytes.megabytes(2));
        add(feedbackForm);
        feedbackForm.add(comments = new TextArea<String>("comments", new Model<String>()));
        feedbackForm.add(uploadField = new FileUploadField("uploadField"));

        feedbackForm.add(new Label("max", new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return feedbackForm.getMaxSize().toString();
            }
        }));

        Link<Void> cancel = new Link<Void>("cancelLink") {
            public void onClick() {
                s.setStatus(Submission.STATUS_WAITING);
                projectLogic.updateSubmissionStatus(s);
                setResponsePage(new StaffOverview());
            }
        };
        feedbackForm.add(cancel);
    }
}


