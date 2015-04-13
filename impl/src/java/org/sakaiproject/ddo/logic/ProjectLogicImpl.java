package org.sakaiproject.ddo.logic;

import java.util.List;

import lombok.Setter;

import net.sf.ehcache.Cache;

import org.apache.log4j.Logger;

import org.sakaiproject.ddo.dao.ProjectDao;
import org.sakaiproject.ddo.logic.SakaiProxy;
import org.sakaiproject.ddo.model.Feedback;
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

		Submission s = dao.getSubmission(id);

		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Submission> getAllSubmissions() {
		return dao.getAllSubmissions();
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
	public List<Submission> getSubmissionsForUser(String userId) {
		return dao.getSubmissionsForUser(userId);
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

		Feedback f = dao.getFeedback(id);

		return f;
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

	@Setter
	private ProjectDao dao;
	
	@Setter
	private Cache cache;

	@Setter
	private SakaiProxy sakaiProxy;

}
