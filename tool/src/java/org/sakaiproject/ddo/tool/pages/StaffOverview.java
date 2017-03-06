/*
 *  Copyright (c) 2016, University of Dayton
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *              http://opensource.org/licenses/ecl2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.sakaiproject.ddo.tool.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.SubmissionFile;
import org.sakaiproject.ddo.tool.providers.SubmissionDataProvider;
import org.sakaiproject.ddo.utils.SubmissionListType;

/**
 * @author David P. Bauer (dbauer1@udayton.edu)
 */
public class StaffOverview extends BasePage {

    SubmissionDataProvider waitingProvider;
    SubmissionDataProvider reviewedProvider;

    public StaffOverview() {
        disableLink(staffOverviewLink);

        // Add a link to refresh the tables on the page
        Link<Void> refreshPage = new Link<Void>("refreshPage") {
            public void onClick() {
                setResponsePage(new StaffOverview());
            }
        };
        add(refreshPage);

        //get list of items from db, wrapped in a dataprovider
        waitingProvider = new SubmissionDataProvider(SubmissionListType.ALL_AWAITING);

        //present the data in a table
        final DataView<Submission> dataView = new DataView<Submission>("awaitingReview", waitingProvider) {

            @Override
            public void populateItem(final Item item) {

                DateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm a");

                final Submission submission = (Submission) item.getModelObject();
                String submissionStatus = submission.getStatus();
                item.add(new Label("submittedBy", sakaiProxy.getUserDisplayName(submission.getSubmittedBy())));
                item.add(new Label("username", sakaiProxy.getUserDisplayId(submission.getSubmittedBy())));
                item.add(new Label("submissiondate", df.format(submission.getSubmissionDate())));
                item.add(new Label("status", submissionStatus));
                Link<Void> startReviewing;
                Label startReviewingLabel;
                if (submissionStatus != null && Submission.STATUS_WAITING.equals(submissionStatus)) {
                    final long submissionId = submission.getSubmissionId();
                    startReviewing = new Link<Void>("startReviewing") {
                        @Override
                        public void onClick() {
                            setResponsePage(new FeedbackFormPage(submissionId));
                        }
                    };
                    startReviewingLabel = new Label("startReviewingLabel", new ResourceModel("link.start_reviewing"));
                } else {
                    final long submissionId = submission.getSubmissionId();
                    startReviewing = new Link<Void>("startReviewing") {
                        @Override
                        public void onClick() {
                            getSession().warn(getString("warn.under_review"));
                            setResponsePage(new FeedbackFormPage(submissionId));
                        }
                    };
                    startReviewingLabel = new Label("startReviewingLabel", Submission.STATUS_UNDER);
                }
                startReviewing.add(startReviewingLabel);
                item.add(startReviewing);
                Link<Void> streamDownloadLink = new Link<Void>("document") {

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

                item.add(streamDownloadLink);
                SubmissionFile sf = sakaiProxy.getResource(submission.getDocumentRef());
                streamDownloadLink.add(new Label("fileName", sf==null?"Cannot find file":sf.getFileName()));
                item.add(new ContextImage("submissionIcon", new Model<String>(sakaiProxy.getResourceIconUrl(submission.getDocumentRef()))));
                item.add(new Label("fileSize", sakaiProxy.getResourceFileSize(submission.getDocumentRef())));
            }
        };
        dataView.setItemReuseStrategy(new DefaultItemReuseStrategy());
        dataView.setItemsPerPage(10);
        add(dataView);
        add(new Label("waitingHeader", MessageFormat.format(getString("staff.overview.waiting.header"), waitingProvider.size())));

        //add a pager to our table, only visible if we have more than 5 items
        add(new PagingNavigator("navigator", dataView) {

            @Override
            public boolean isVisible() {
                if(waitingProvider.size() > 10) {
                    return true;
                }
                return false;
            }

            @Override
            public void onBeforeRender() {
                super.onBeforeRender();

                //clear the feedback panel messages
                clearFeedback(feedbackPanel);
            }
        });

        // Archive All link
        Link<Void> archiveAllLink = new Link<Void>("archive-all-link") {
            @Override
            public void onClick() {
                if(projectLogic.archiveAllReviewedSubmissions()){
                    getSession().info(getString("success.all_archived_submission"));
                    setResponsePage(new StaffOverview());
                } else {
                    getSession().error(getString("error.all_archived_submission"));
                    setResponsePage(new StaffOverview());
                }
            }
        };
        archiveAllLink.setVisible(sakaiProxy.isDDOAdmin());
        add(archiveAllLink);

        //get list of items from db, wrapped in a dataprovider
        reviewedProvider = new SubmissionDataProvider(SubmissionListType.ALL_REVIEWED);

        //present the reviewed data in a table
        final DataView<Submission> dataViewReviewed = new DataView<Submission>("reviewed", reviewedProvider) {

            @Override
            public void populateItem(final Item item) {

                DateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm a");

                final Submission submission = (Submission) item.getModelObject();
                final String submissionStatus = submission.getStatus();
                item.add(new Label("submittedBy", sakaiProxy.getUserDisplayName(submission.getSubmittedBy())));
                item.add(new Label("username", sakaiProxy.getUserDisplayId(submission.getSubmittedBy())));
                item.add(new Label("submissiondate", df.format(submission.getSubmissionDate())));
                item.add(new Label("status", submissionStatus));
                Link<Void> feedback;
                Label feedbackLabel;
                Link<Void> editFeedback;
                Label editFeedbackLabel;
                final List<Feedback> feedbackList = projectLogic.getFeedbackForSubmission(submission.getSubmissionId());
                if (feedbackList != null && !feedbackList.isEmpty()) {
                    Feedback f = feedbackList.get(0);
                    final long feedbackId = f.getFeedbackId();
                    item.add(new Label("reviewedBy", sakaiProxy.getUserDisplayName(f.getReviewedBy())));
                    item.add(new Label("reviewDate", df.format(f.getReviewDate())));
                    feedback = new Link<Void>("feedback") {
                        @Override
                        public void onClick() {
                                setResponsePage(new FeedbackPage(feedbackId,"staff"));
                            }
                    };
                    editFeedback = new Link<Void>("editFeedback") {
                        @Override
                        public void onClick() {
                            setResponsePage(new EditFeedback(feedbackId));
                        }
                    };
                    feedbackLabel = new Label("feedbackLabel","View");
                    editFeedbackLabel = new Label ("editFeedbackLabel", "Edit");
                } else {
                    item.add(new Label("reviewedBy",""));
                    item.add(new Label("reviewDate",""));
                    feedback = new Link<Void>("feedback"){
                        @Override
                        public void onClick() {}
                    };
                    editFeedback = new Link<Void>("editFeedback"){
                        @Override
                        public void onClick() {}
                    };
                    feedbackLabel = new Label("feedbackLabel","");
                    editFeedbackLabel = new Label ("editFeedbackLabel", "");
                }
                feedback.add(feedbackLabel);
                item.add(feedback);
                editFeedback.add(editFeedbackLabel);
                item.add(editFeedback);
                Link<Void> streamDownloadLink = new Link<Void>("document") {

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

                item.add(streamDownloadLink);
                SubmissionFile sf = sakaiProxy.getResource(submission.getDocumentRef());
                streamDownloadLink.add(new Label("fileName", sf==null?"Cannot find file.":sf.getFileName()));
                item.add(new ContextImage("submissionIcon", new Model<String>(sakaiProxy.getResourceIconUrl(submission.getDocumentRef()))));
                item.add(new Label("fileSize", sakaiProxy.getResourceFileSize(submission.getDocumentRef())));
                // Archive link
                Link<Void> archiveLink = new Link<Void>("archive-link") {
                    @Override
                    public void onClick() {
                        submission.setStatus(Submission.STATUS_ARCHIVED);
                        if(projectLogic.updateSubmissionStatus(submission)){
                            getSession().info(getString("success.archived_submission"));
                            setResponsePage(new StaffOverview());
                        } else {
                            error(getString("error.archived_submission"));
                        }
                    }
                };
                item.add(archiveLink);
                archiveLink.setVisible(sakaiProxy.isDDOAdmin());
            }
        };
        dataViewReviewed.setItemReuseStrategy(new DefaultItemReuseStrategy());
        dataViewReviewed.setItemsPerPage(7);
        add(dataViewReviewed);

        add(new Label("completedHeader", MessageFormat.format(getString("staff.overview.completed.header"), reviewedProvider.size())));

        //add a pager to our table, only visible if we have more than 5 items
        add(new PagingNavigator("reviewedNavigator", dataViewReviewed) {

            @Override
            public boolean isVisible() {
                if(reviewedProvider.size() > 7) {
                    return true;
                }
                return false;
            }

            @Override
            public void onBeforeRender() {
                super.onBeforeRender();

                //clear the feedback panel messages
                clearFeedback(feedbackPanel);
            }
        });
    }

}
