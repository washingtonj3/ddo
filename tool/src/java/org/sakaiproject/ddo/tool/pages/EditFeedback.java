package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.SubmissionFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dbauer1 on 8/24/15.
 */
public class EditFeedback extends BasePage {

    DateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm a");
    DateFormat dueFormat = new SimpleDateFormat(("MMM d, yyyy"));

    protected Submission s = new Submission();
    protected Feedback f = new Feedback();

    private final FileUploadField uploadField;
    private final TextArea<String> comments;
    private final CheckBox replaceReview;

    public EditFeedback(long feedbackId) {
        disableLink(staffOverviewLink);
        this.f = projectLogic.getFeedback(feedbackId);
        this.s = projectLogic.getSubmission(f.getSubmissionId());

        //s.setStatus(Submission.STATUS_UNDER);
        //projectLogic.updateSubmissionStatus(s);

        add(new Label("name", sakaiProxy.getUserDisplayName(s.getSubmittedBy())));
        add(new Label("primaryLanguageIsEnglish",s.getPrimaryLanguageIsEnglish() ? "Yes" : "No"));
        add(new Label("primaryLanguage",s.getPrimaryLanguage()));
        add(new Label("submissionDate",df.format(s.getSubmissionDate())));
        add(new Label("email", sakaiProxy.getUserEmail(s.getSubmittedBy())));
        add(new Label("username", sakaiProxy.getUserDisplayId(s.getSubmittedBy())));

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
        SubmissionFile sf = sakaiProxy.getResource(s.getDocumentRef());
        streamDownloadLink.add(new Label("fileName", sf==null?"Cannot find file.":sf.getFileName()));
        add(new ContextImage("submissionIcon", new Model<String>(sakaiProxy.getResourceIconUrl(s.getDocumentRef()))));
        add(new Label("fileSize", sakaiProxy.getResourceFileSize(s.getDocumentRef())));

        add(new Label("assignmentTitle", s.getAssignmentTitle()));
        add(new Label("course", s.getCourseTitle()));

        String instructorDisplayName = sakaiProxy.getUserDisplayName(s.getInstructor());
        String instructorEmail = sakaiProxy.getUserEmail(s.getInstructor());
        if("".equals(instructorDisplayName)) {
            add(new Label("instructor", s.getInstructor()));
        } else {
            add(new Label("instructor", instructorDisplayName + " (" + instructorEmail + ")"));
        }

        add(new Label("dueDate", s.getDueDate()!=null ? dueFormat.format(s.getDueDate()) : getString("error.no_due_date")));
        add(new MultiLineLabel("feedbackFocus", s.getFeedbackFocus()));
        add(new MultiLineLabel("instructorRequirements", s.getInstructorRequirements()));

        final Form<?> feedbackForm = new Form<Void>("feedbackForm") {
            /**
             * @see org.apache.wicket.markup.html.form.Form#onSubmit()
             */
            @Override
            protected void onSubmit() {
                String currentUserId = sakaiProxy.getCurrentUserId();

                f.setComments(comments.getModelObject());

                FileUpload file = uploadField.getFileUpload();

                String oldFileReference = "";

                // Check if the reviewer would like to replace or add a new reviewed file
                if (replaceReview.getModelObject().booleanValue()) {
                    // Remove previously submitted file if there was one.
                    if (f.getReviewedDocumentRef() != null && !f.getReviewedDocumentRef().isEmpty()) {
                        oldFileReference = f.getReviewedDocumentRef();
                    }
                    if (file == null) {
                        f.setReviewedDocumentRef("");
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
                            f.setReviewedDocumentRef(documentRef);
                        }
                    }
                }

                if (f.getComments() == null && file == null){
                    error(getString("error.no_feedback"));
                    return;
                }

                f.setReviewDate(new Date());
                f.setReviewedBy(currentUserId);

                if(projectLogic.updateFeedback(f) && projectLogic.updateSubmissionStatus(s)){
                    // Only delete old file on a successful save
                    if(!oldFileReference.isEmpty()) {
                        sakaiProxy.removeResource(oldFileReference);
                    }
                    getSession().info(getString("success.save_feedback"));
                    setResponsePage(new StaffOverview());
                } else {
                    error(getString("error.save_feedback"));
                }

            }
        };

        feedbackForm.setMaxSize(Bytes.megabytes(15));
        add(feedbackForm);

        feedbackForm.add(comments = new TextArea<String>("comments", Model.of(f.getComments())));

        feedbackForm.add(uploadField = new FileUploadField("uploadField"));

        feedbackForm.add(replaceReview = new CheckBox("replaceReview", Model.of(Boolean.FALSE)));

        if(f.getReviewedDocumentRef() != null && !f.getReviewedDocumentRef().isEmpty()) {
            Link<Void> feedbackDownloadLink = new Link<Void>("feedbackDoc") {

                @Override
                public void onClick() {

                    AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

                        @Override
                        public void write(OutputStream output) throws IOException {
                            output.write(sakaiProxy.getResource(f.getReviewedDocumentRef()).getBytes());
                        }
                    };

                    ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, sakaiProxy.getResource(f.getReviewedDocumentRef()).getFileName());
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
                }
            };

            feedbackForm.add(feedbackDownloadLink);
            SubmissionFile ff = sakaiProxy.getResource(f.getReviewedDocumentRef());
            feedbackDownloadLink.add(new Label("fFileName", ff==null?"Cannot find file":ff.getFileName()));
            feedbackForm.add(new ContextImage("feedbackIcon", new Model<String>(sakaiProxy.getResourceIconUrl(f.getReviewedDocumentRef()))));
            String fileSize = "(" + sakaiProxy.getResourceFileSize(f.getReviewedDocumentRef()) + ")";
            feedbackForm.add(new Label("reviewFileSize", fileSize));
            feedbackForm.add(new Label("noDoc", getString("error.no_document")).setVisible(false));
        } else {
            Link<Void> feedbackDownloadLink = new Link<Void>("feedbackDoc") {
                @Override
                public void onClick() {}
            };
            feedbackForm.add(feedbackDownloadLink);
            feedbackDownloadLink.add(new Label("fFileName",""));
            ContextImage icon = new ContextImage("feedbackIcon", "");
            icon.setVisible(false);
            feedbackForm.add(icon);
            feedbackForm.add(new Label("reviewFileSize", ""));
            feedbackForm.add(new Label("noDoc", getString("error.no_document")).setVisible(true));
        }

        feedbackForm.add(new Label("max", new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return feedbackForm.getMaxSize().toString();
            }
        }));

        Link<Void> cancel = new Link<Void>("cancelLink") {
            public void onClick() {
                //s.setStatus(Submission.STATUS_WAITING);
                //projectLogic.updateSubmissionStatus(s);
                setResponsePage(new StaffOverview());
            }
        };
        feedbackForm.add(cancel);

        SubmitLink submit = new SubmitLink("submit");
        feedbackForm.add(submit);


    }
}
