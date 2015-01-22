package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.basic.Label;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;

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
        submissionDownloadLink.add(new Label("sFileName", sakaiProxy.getResource(submission.getDocumentRef()).getFileName()));

        if(feedback.getComments() != null && !feedback.getComments().isEmpty()) {
            add(new Label("comments", feedback.getComments()));
        } else {
            add(new Label("comments", "No comments were given."));
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
            feedbackDownloadLink.add(new Label("fFileName", sakaiProxy.getResource(feedback.getReviewedDocumentRef()).getFileName()));
        } else {
            Link<Void> feedbackDownloadLink = new Link<Void>("feedbackDoc") {
                @Override
                public void onClick() {}
            };
            add(feedbackDownloadLink);
            feedbackDownloadLink.add(new Label("fFileName",""));
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
