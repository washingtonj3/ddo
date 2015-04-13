package org.sakaiproject.ddo.logic;

import java.util.List;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;

/**
 * Digital Drop Off Project Logic
 * 
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public interface ProjectLogic {

	Submission getSubmission(long id);
	List<Submission> getAllSubmissions();

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

	List<Submission> getSubmissionsForUser(String userId);
	Feedback getFeedback(long id);
	List<Feedback> getFeedbackForSubmission(long submissionId);
	boolean addFeedback(Feedback feedback);
	List<Submission> getAllWaitingSubmissions();
	List<Submission> getAllReviewedSubmissions();
	boolean updateSubmissionStatus(Submission s);
}
