package org.sakaiproject.ddo.logic;

import java.util.Date;
import java.util.List;

import lombok.Setter;

import net.sf.ehcache.Cache;

import org.apache.log4j.Logger;

import org.sakaiproject.ddo.dao.ProjectDao;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.NumStatistics;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.Export;
/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);

	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

	/**
	 * {@inheritDoc}
	 */
	public Submission getSubmission(long id) {

		return dao.getSubmission(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Submission> getAllWaitingSubmissions() {
		return dao.getAllWaitingSubmissions();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Submission> getAllReviewedSubmissions() {
		return dao.getAllReviewedSubmissions();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Submission> getAllArchivedSubmissions() {
		return dao.getAllArchivedSubmissions();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Submission> getSubmissionsForUser(String userId) {
		return dao.getSubmissionsForUser(userId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Submission> getSubmissionsForCurrentUser() {
		return getSubmissionsForUser(sakaiProxy.getCurrentUserId());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addSubmission(Submission submission) {
		return addSubmission(submission, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addSubmission(Submission submission, boolean sendNotification) {
		if (dao.addSubmission(submission)) {
			if(sendNotification){
				sakaiProxy.sendSubmissionNotification(submission);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean updateSubmissionStatus(Submission s) { return dao.updateSubmissionStatus(s); }

	/**
	 * {@inheritDoc}
	 */
	public Feedback getFeedback(long id) {

		return dao.getFeedback(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Feedback> getFeedbackForSubmission(long submissionId) {
		return dao.getFeedbackForSubmission(submissionId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addFeedback(Feedback feedback) {
		return addFeedback(feedback, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addFeedback(Feedback feedback, boolean sendNotification) {
		if (dao.addFeedback(feedback)) {
			if(sendNotification){
				Submission submission = getSubmission(feedback.getSubmissionId());
				sakaiProxy.sendFeedbackNotification(submission);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean updateFeedback(Feedback feedback) {
		return dao.updateFeedback(feedback);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean archiveAllReviewedSubmissions() {
		List<Submission> allReviewedSubmissions = getAllReviewedSubmissions();
		for(Submission submission : allReviewedSubmissions) {
			submission.setStatus(Submission.STATUS_ARCHIVED);
			if(!updateSubmissionStatus(submission)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfWaitingSubmissions() {
		return dao.getNumberOfWaitingSubmissions();
	}

	/**
	 * Gets the number of submissions between two dates with different statuses
	 *
	 * @param startDate
	 * @param endDate
	 * @param statusString
	 *
	 * @return returns the number of submissions matching the parameters or a 0 on error
	 */
	public int getNumberofSubmissionsLogic(Date startDate, Date endDate, String statusString){
		return dao.getNumberofSubmissionsDao(startDate, endDate, statusString);
	}

	/**
	 * Gets the number of unique submitters between two dates with different statuses
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of unique submitters matching the parameters or a 0 on error
	 */
	public int getNumberOfUniqueUsersLogic(Date startDate, Date endDate){
		return dao.getNumberOfUniqueUsersDao(startDate, endDate);
	}

	/**
	 * Gets the number of repeat users between the two dates
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of repeat users between the two dates or a 0 on error
	 */
	public int getNumberOfRepeatUsersLogic(Date startDate, Date endDate){
		return dao.getNumberOfRepeatUsersDao(startDate, endDate);
	}

	/**
	 * Gets the number of Consultants who reviewed between two dates
	 *
	 * * @param startDate
	 * @param endDate
	 *
	 * @return returns the number of Consultants between the two parameters or a 0 on error
	 */
	public int getNumberOfConsultantsLogic(Date startDate, Date endDate){
		return dao.getNumberOfConsultantsDao(startDate, endDate);
	}


	/**
	 * Gets the number of reviews per consultant
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the reviewerId and number of reviewed papers for each reviewer who reviewed within the timeframe in list form
	 */
	public List<NumStatistics> numberOfReviewsPerConsultantLogic(Date startDate, Date endDate) {
		return dao.numberOfReviewsPerConsultantDao(startDate, endDate);
	}

	/**
	 * Gets a list containing the Insturctor Name and how many papers their students submitted
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the three instructors with the most user submissions within the timeframe in list form
	 */
	public List<NumStatistics> topThreeInstructorsStatsLogic(Date startDate, Date endDate) {
		return dao.topThreeInstructorsStatsDao(startDate, endDate);
	}

	/**
	 * Gets a list containing the Section Name and how many papers students in this section submitted
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the three sections with the most user submissions within the timeframe in list form
	 */
	public List<NumStatistics> topThreeSectionsStatsLogic(Date startDate, Date endDate) {
		return dao.topThreeSectionsStatsDao(startDate, endDate);
	}


	/**
	 * Gets the average time between submission and review in milliseconds
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns a int containing the average time between submission and review in milliseconds
	 */
	public int getAvgTurnaroundTimeLogic(Date startDate, Date endDate){
		return dao.getAvgTurnaroundTimeDao(startDate, endDate);
	}

	/**
	 * Gets the average number of submissions in the date range
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank before the function it is the current date
	 *
	 * @return returns the average number of submissions in the parameter range or a 0 on error or null
	 */
	public double getAvgNumberofSubmissionsLogic(Date startDate, Date endDate){
		return dao.getAvgNumberofSubmissionsDao(startDate, endDate);
	}


	/**
	 * Converts milleseconds to a date String
	 *
	 * @param rawMilliseconds    Starting date for the date range search: Never null or after endDate
	 *
	 * @return returns the datestring
	 */

	public String millisecondsToTime(double rawMilliseconds){//Converts Milliseconds to the HH:MM:SS as a string
		String dateString = "";
		double seconds =  Math.floor(rawMilliseconds / 1000);
		double minutes =  Math.floor(seconds / 60);
		double hours =  Math.floor(minutes / 60);
		seconds = seconds - (minutes * 60);
		minutes = minutes - (hours * 60);

		if(hours > 0){
			dateString = String.valueOf((int)hours) + ":";
		}
		if(minutes < 10){
			dateString = dateString + "0" + String.valueOf((int)minutes) + ":";
		}
		else{
			dateString = dateString + String.valueOf((int)minutes) + ":";
		}
		if(seconds < 10){
			dateString = dateString + "0" + String.valueOf((int)seconds);
		}
		else{
			dateString = dateString + String.valueOf((int)seconds);
		}
		return dateString;
	}

	/**
	 * Gets all the submissions, and relevant submission and review data from the database
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank before the function it is the current date
	 *
	 * @return returns the list of submissions
	 */
	public List<Export> statsGetAllSubmissionsLogic (Date startDate, Date endDate){
		return dao.statsGetAllSubmissionsDao(startDate, endDate);
	}

	@Setter
	private ProjectDao dao;
	
	@Setter
	private Cache cache;

	@Setter
	private SakaiProxy sakaiProxy;

}
