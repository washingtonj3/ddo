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

package org.sakaiproject.ddo.logic;

import java.util.Date;
import java.util.List;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.NumStatistics;
import org.sakaiproject.ddo.model.Submission;

/**
 * Digital Drop Off Project Logic
 * 
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public interface ProjectLogic {

	/**
	 * Get a particular submission by its ID.
	 *
	 * @param id
	 * @return
	 */
	Submission getSubmission(long id);

	/**
	 * Adds a submission and sends a notification by default
	 *
	 * @param submission the submission to be added
	 * @return returns true if the submission was added successfully
	 */
	boolean addSubmission(Submission submission);

	/**
	 * Adds a submission to the database and sends a notification if specified.
	 *
	 * @param submission the submission to be added
	 * @param sendNotification true if an email should be sent notifying that there is a new submission
	 * @return returns true if the submission was added successfully
	 */
	boolean addSubmission(Submission submission, boolean sendNotification);

	/**
	 * Get all submissions for a given user by their ID.
	 *
	 * @param userId
	 * @return
	 */
	List<Submission> getSubmissionsForUser(String userId);

	/**
	 * Gell all submissions for the current authenticated user
	 *
	 * @return
	 */
	List<Submission> getSubmissionsForCurrentUser();

	/**
	 * Get a feedback record by its ID.
	 *
	 * @param id
	 * @return
	 */
	Feedback getFeedback(long id);

	/**
	 * Get all feedback records for a submission by the submission's ID.
	 *
	 * @param submissionId
	 * @return
	 */
	List<Feedback> getFeedbackForSubmission(long submissionId);

	/**
	 * Add a feedback record.
	 *
	 * @param feedback
	 * @return
	 */
	boolean addFeedback(Feedback feedback);

	/**
	 * Get all submissions with the status Submission.STATUS_WAITING
	 *
	 * @return
	 */
	List<Submission> getAllWaitingSubmissions();

	/**
	 * Get all submissions with the status Submission.STATUS_REVIEWED
	 *
	 * @return
	 */
	List<Submission> getAllReviewedSubmissions();

	/**
	 * Get all submissions with the status Submission.STATUS_ARCHIVED
	 *
	 * @return
	 */
	List<Submission> getAllArchivedSubmissions();

	/**
	 * Update a particular submission's status
	 *
	 * @param s
	 * @return
	 */
	boolean updateSubmissionStatus(Submission s);

	/**
	 * Update an existing feedback record
	 *
	 * @param feedback
	 * @return
	 */
	boolean updateFeedback(Feedback feedback);

	/**
	 * Set all submissions with the status Submission.STATUS_REVIEWED
	 * to the status Submission.STATUS_ARCHIVED.
	 *
	 * @return
	 */
	boolean archiveAllReviewedSubmissions();

	/**
	 * Gets the number of submission that are currently waiting to be reviewed
	 *
	 * @return returns the number of submissions waiting to be reviewed or 0 on a caught exception
	 */
	int getNumberOfWaitingSubmissions();

	/**
	 * Gets the number of submissions between two dates with different statuses
	 *
	 * The date parameters are checked for error prior to this function.
	 *
	 * @param startDate
	 * @param endDate
	 * @param statusString
	 *
	 * @return returns the number of submissions matching the parameters or a 0 on error
	 */

	int getNumberofSubmissionsLogic(Date startDate, Date endDate, String statusString);

	/**
	 * Gets the number of unique submitters between two dates with different statuses
	 *
	 * The date parameters are checked for error prior to this function.
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of unique submitters matching the parameters or a 0 on error
	 */
	int getNumberOfUniqueUsersLogic(Date startDate, Date endDate);

	/**
	 * Gets the number of repeat users between the two dates
	 *
	 * The date parameters are checked for error prior to this function.
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of repeat users between the two dates or a 0 on error
	 */
	int getNumberOfRepeatUsersLogic(Date startDate, Date endDate);

	/**
	 * Gets the number of Consultants who reviewed between two dates
	 *
	 * The date parameters are checked for error prior to this function.
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of Consultants between the two parameters or a 0 on error
	 */
	int getNumberOfConsultantsLogic(Date startDate, Date endDate);

	/**
	 * Gets the number of reviews per consultant
	 *
	 * The date parameters are checked for error prior to this function.
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the reviewerId and number of reviewed papers for each reviewer who reviewed within the timeframe in list form
	 */

	List<NumStatistics> numberOfReviewsPerConsultantLogic(Date startDate, Date endDate);

	/**
	 * Gets a list containing the Insturctor Name and how many papers their students submitted
	 *
	 * The date parameters are checked for error prior to this function.
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the reviewerId and number of reviewed papers for each reviewer who reviewed within the timeframe in list form
	 */

	List<NumStatistics> topThreeInstructorsStatsLogic(Date startDate, Date endDate);
}
