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

import org.apache.wicket.markup.html.WebMarkupContainer;
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
import java.util.*;

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
public class StudentOverview extends BasePage {

    private SubmissionDataProvider provider;

    public StudentOverview() {
        disableLink(studentOverviewLink);

        //link to drop off form
        //the i18n label for this is directly in the HTML
        Link<Void> toDropOffFormLink = new Link<Void>("toDropOffFormLink") {
            private static final long serialVersionUID = 1L;

            public void onClick() {
                setResponsePage(new DropOffForm());
            }

            @Override
            public boolean isEnabled() {
                return !sakaiProxy.isDDOClosed();
            }

            @Override
            public boolean isVisible() {
                return !sakaiProxy.isDDOClosed();
            }
        };
        add(toDropOffFormLink);

        Label closedMessage = new Label("dropOffClosedMessage", sakaiProxy.getDDOClosedMessage());
        closedMessage.setVisible(sakaiProxy.isDDOClosed());
        add(closedMessage);

        //get list of items from db, wrapped in a dataprovider
        provider = new SubmissionDataProvider(SubmissionListType.FOR_CURRENT_USER);

        //present the data in a table
        final DataView<Submission> dataView = new DataView<Submission>("simple", provider) {

            @Override
            public void populateItem(final Item item) {

                DateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm a");

                final Submission submission = (Submission) item.getModelObject();

                Link<Void> feedback;
                Label feedbackLabel;
                final List<Feedback> feedbackList = projectLogic.getFeedbackForSubmission(submission.getSubmissionId());
                if (feedbackList != null && !feedbackList.isEmpty()) {
                    final long feedbackId = feedbackList.get(0).getFeedbackId();
                    feedback = new Link<Void>("feedback") {
                        @Override
                        public void onClick() {
                            setResponsePage(new FeedbackPage(feedbackId,"student"));
                        }
                    };
                    feedbackLabel = new Label("feedbackLabel",new ResourceModel("link.view_feedback"));
                } else {
                    feedback = new Link<Void>("feedback"){
                        @Override
                        public void onClick() {}
                    };
                    feedbackLabel = new Label("feedbackLabel","");
                }
                feedback.add(feedbackLabel);
                item.add(feedback);

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
                item.add(new Label("submissiondate", df.format(submission.getSubmissionDate())));
                item.add(new Label("status", submission.getStatus()));
            }
        };
        dataView.setItemReuseStrategy(new DefaultItemReuseStrategy());
        dataView.setItemsPerPage(15);
        add(dataView);

        //add a pager to our table, only visible if we have more than 5 items
        add(new PagingNavigator("navigator", dataView) {

            @Override
            public boolean isVisible() {
                if(provider.size() > 15) {
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

        add(new Label("studentSubmissionsHeader",
                MessageFormat.format(getString("student.overview.submissions.header"),
                        provider.size()))
        .setEscapeModelStrings(false));

        WebMarkupContainer submissionQueueInfo = new WebMarkupContainer("submissionQueueInfo");

        int numberOfWaitingSubmissions = projectLogic.getNumberOfWaitingSubmissions();

        submissionQueueInfo.add(new Label("numberWaitingReview",
                MessageFormat.format(getString("activity.number.waiting"),
                        numberOfWaitingSubmissions))
        .setEscapeModelStrings(false));

        submissionQueueInfo.add(new Label("expectedWaitTime",
                MessageFormat.format(getString("activity.wait.time"),
                        getExpectedWaitTime(numberOfWaitingSubmissions),
                        getExpectedReturnDay(numberOfWaitingSubmissions)))
        .setEscapeModelStrings(false));

        add(submissionQueueInfo);
    }

    private String getExpectedWaitTime(int numberOfWaitingSubmissions) {
        if(numberOfWaitingSubmissions < 9) {
            return "48 hours";
        } else if (numberOfWaitingSubmissions < 18) {
            return "72 hours";
        } else {
            return "96 hours";
        }
    }

    private String getExpectedReturnDay(int numberOfWaitingSubmissions) {
        Calendar today = Calendar.getInstance();
        int currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        int expectedReturnDay;

        if(numberOfWaitingSubmissions < 9) {
            expectedReturnDay = currentDayOfWeek + 2;
        } else if (numberOfWaitingSubmissions < 18) {
            expectedReturnDay = currentDayOfWeek + 3;
        } else {
            expectedReturnDay = currentDayOfWeek + 4;
        }
        if(expectedReturnDay > 7)
            expectedReturnDay = expectedReturnDay - 7;

        String dayOfWeek = "";

        switch(expectedReturnDay){
            case 1:
                dayOfWeek="Sunday";
                break;
            case 2:
                dayOfWeek="Monday";
                break;
            case 3:
                dayOfWeek="Tuesday";
                break;
            case 4:
                dayOfWeek="Wednesday";
                break;
            case 5:
                dayOfWeek="Thursday";
                break;
            case 6:
                dayOfWeek="Friday";
                break;
            case 7:
                dayOfWeek="Saturday";
                break;
        }

        return dayOfWeek;
    }
}
