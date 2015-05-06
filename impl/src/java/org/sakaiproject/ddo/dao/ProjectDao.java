package org.sakaiproject.ddo.dao;

import java.util.List;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;

/**
 * DAO interface for our project based on work by Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public interface ProjectDao {

	/**
	 * Get a single submission
	 *
	 * @param id
	 * @return
	 */
	Submission getSubmission(long id);

	/**
	 * Get all submissions in the database
	 *
	 * @return
	 */
	List<Submission> getAllSubmissions();

	/**
	 * Add a single submission
	 *
	 * @param submission
	 * @return
	 */
	boolean addSubmission(Submission submission);

	/**
	 * Get all submissions for a given user
	 *
	 * @param userId
	 * @return
	 */
	List<Submission> getSubmissionsForUser(String userId);

	/**
	 * Get single feedback
	 *
	 * @param id
	 * @return
	 */
	Feedback getFeedback(long id);

	/**
	 * Get all feedback items for a particular submission
	 * @param submissionId
	 * @return
	 */
	List<Feedback> getFeedbackForSubmission(long submissionId);

	/**
	 * Add a feedback item
	 *
	 * @param feedback
	 * @return
	 */
	boolean addFeedback(Feedback feedback);

	/**
	 * Get all submissions that have status waiting or in progress
	 *
	 * @return
	 */
	List<Submission> getAllWaitingSubmissions();

	/**
	 * Get all submissions that have already been reviewed
	 *
	 * @return
	 */
	List<Submission> getAllReviewedSubmissions();

	/**
	 * Update the status for a submission
	 *
	 * @param s
	 * @return
	 */
	boolean updateSubmissionStatus(Submission s);

	/**
	 * Get the number of submissions that are currently waiting to be reviewed
	 *
	 * @return number of submissions waiting to be reviewed or 0 if there was an exception caught
	 */
	int getNumberOfWaitingSubmissions();

}
