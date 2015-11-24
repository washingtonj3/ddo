package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.basic.Label;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.SubmissionFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by dbauer1 on 12/15/14.
 */
public class FeedbackPage extends BasePage {

    Link<Void> backLink;

    public FeedbackPage(long feedbackId, String fromPage) {

        final Feedback feedback = projectLogic.getFeedback(feedbackId);
        final Submission submission = projectLogic.getSubmission(feedback.getSubmissionId());

        Link<Void> submissionDownloadLink = new Link<Void>("submission") {

            @Override
            public void onClick() {

                AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

                    @Override
                    public void write(OutputStream output) throws IOException {
                        output.write(sakaiProxy.getResource(submission.getDocumentRef()).getBytes());
                    }
                };

                ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, sakaiProxy.getResource(submission.getDocumentRef()).getFileName());
                getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
            }
        };

        add(submissionDownloadLink);
        SubmissionFile sf = sakaiProxy.getResource(submission.getDocumentRef());
        submissionDownloadLink.add(new Label("sFileName", sf==null?"Cannot find file.":sf.getFileName()));
        add(new ContextImage("sSubmissionIcon", new Model<String>(sakaiProxy.getResourceIconUrl(submission.getDocumentRef()))));
        add(new Label("sFileSize", sakaiProxy.getResourceFileSize(submission.getDocumentRef())));

        if(feedback.getComments() != null && !feedback.getComments().isEmpty()) {
            add(new Label("comments", feedback.getComments()).setEscapeModelStrings(false));
        } else {
            add(new Label("comments", getString("error.no_comments")));
        }

        if(feedback.getReviewedDocumentRef() != null && !feedback.getReviewedDocumentRef().isEmpty()) {
            Link<Void> feedbackDownloadLink = new Link<Void>("feedbackDoc") {

                @Override
                public void onClick() {

                    AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

                        @Override
                        public void write(OutputStream output) throws IOException {
                            output.write(sakaiProxy.getResource(feedback.getReviewedDocumentRef()).getBytes());
                        }
                    };

                    ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, sakaiProxy.getResource(feedback.getReviewedDocumentRef()).getFileName());
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
                }
            };

            add(feedbackDownloadLink);
            SubmissionFile ff = sakaiProxy.getResource(feedback.getReviewedDocumentRef());
            feedbackDownloadLink.add(new Label("fFileName", ff==null?"Cannot find file.":ff.getFileName()));
            add(new ContextImage("submissionIcon", new Model<String>(sakaiProxy.getResourceIconUrl(feedback.getReviewedDocumentRef()))));
            String fileSize = "(" + sakaiProxy.getResourceFileSize(feedback.getReviewedDocumentRef()) + ")";
            add(new Label("fileSize", fileSize));
            add(new Label("noDoc", getString("error.no_document")).setVisible(false));
        } else {
            Link<Void> feedbackDownloadLink = new Link<Void>("feedbackDoc") {
                @Override
                public void onClick() {}
            };
            add(feedbackDownloadLink);
            feedbackDownloadLink.add(new Label("fFileName",""));
            ContextImage icon = new ContextImage("submissionIcon", "");
            icon.setVisible(false);
            add(icon);
            add(new Label("fileSize", ""));
            add(new Label("noDoc", getString("error.no_document")).setVisible(true));
        }

        if("staff".equals(fromPage)) {
            disableLink(staffOverviewLink);
            backLink = new Link<Void>("backLink") {
                public void onClick() {
                    setResponsePage(new StaffOverview());
                }
            };
            add(backLink);
        } else {
            disableLink(studentOverviewLink);
            backLink = new Link<Void>("backLink") {
                public void onClick() {
                    setResponsePage(new StudentOverview());
                }
            };
            add(backLink);
        }
    }
}
