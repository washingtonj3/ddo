package org.sakaiproject.ddo.dao;

import java.util.Date;
import java.util.List;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.NumStatistics;
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
	 * Get all archived submissions
	 */
	List<Submission> getAllArchivedSubmissions();

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

	/**
	 * Update a given feedback record.
	 *
	 * @param feedback
	 * @return
	 */
	boolean updateFeedback(Feedback feedback);

	/**
	 * Gets the number of submissions between two dates with different statuses
	 *
	 * @param startDate
	 * @param endDate
	 * @param statusString
	 *
	 * @return returns the number of submissions matching the parameters or a 0 on error
	 */
	int getNumberofSubmissionsDao(Date startDate, Date endDate, String statusString);

	/**
	 * Gets the number of unique Submitters between two dates
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of unique Submitters between the two dates or a 0 on error
	 */
	int getNumberOfUniqueUsersDao(Date startDate, Date endDate);

	/**
	 * Gets the number of repeat users between the two dates
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of repeat users between the two dates or a 0 on error
	 */
	int getNumberOfRepeatUsersDao(Date startDate, Date endDate);

	/**
	 * Gets the number of Consultants who reviewed between two dates
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of Consultants between the two parameters or a 0 on error
	 */
	int getNumberOfConsultantsDao(Date startDate, Date endDate);

	/**
	 * Gets the number of reviews per consultant
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the reviewerId and number of reviewed papers for each reviewer who reviewed within the timeframe in list form
	 */
	List<NumStatistics> numberOfReviewsPerConsultantDao(Date startDate, Date endDate);

	/**
	 * Gets a list containing the Insturctor Name and how many papers their students submitted
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the three instructors with the most user submissions within the timeframe in list form
	 */
	List<NumStatistics> topThreeInstructorsStatsDao(Date startDate, Date endDate);

	/**
	 * Gets a list containing the Section Name and how many papers students in this section submitted
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the three sections with the most user submissions within the timeframe in list form
	 */
	List<NumStatistics> topThreeSectionsStatsDao(Date startDate, Date endDate);

	/**
	 * Gets the average time between submission and review in milliseconds
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns a int containing the average time between submission and review in milliseconds
	 */
	int getAvgTurnaroundTimeDao(Date startDate, Date endDate);

	/**
	 * Gets the average number of submissions in the date range
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank before the function it is the current date
	 *
	 * @return returns the average number of submissions in the parameter range or a 0 on error or null
	 */
	double getAvgNumberofSubmissionsDao(Date startDate, Date endDate);

}
