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

	@Setter
	private ProjectDao dao;
	
	@Setter
	private Cache cache;

	@Setter
	private SakaiProxy sakaiProxy;

}
