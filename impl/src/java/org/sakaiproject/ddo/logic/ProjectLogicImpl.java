package org.sakaiproject.ddo.logic;

import java.util.List;

import lombok.Setter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import org.sakaiproject.ddo.dao.ProjectDao;
import org.sakaiproject.ddo.logic.SakaiProxy;
import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.Thing;

/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);

	
	/**
	 * {@inheritDoc}
	 */
	public Thing getThing(long id) {
		
		//check cache 
		Element element = cache.get(id);
		if(element != null) {
			if(log.isDebugEnabled()) {
				log.debug("Fetching item from cache for: " + id);
			}
			return (Thing)element.getValue();
		}
		
		//if nothing from cache, get from db and cache it 
		Thing t = dao.getThing(id);
			
		if(t != null) {
			if(log.isDebugEnabled()) {
				log.debug("Adding item to cache for: " + id);
			}
			cache.put(new Element(id,t));
		}

		return t;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Thing> getThings() {
		return dao.getThings();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean addThing(Thing t) {
		return dao.addThing(t);
	}

	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

	public Submission getSubmission(long id) {

		Submission s = dao.getSubmission(id);

		return s;
	}

	public List<Submission> getAllSubmissions() {
		return dao.getAllSubmissions();
	}

	public List<Submission> getAllWaitingSubmissions() {
		return dao.getAllWaitingSubmissions();
	}

	public List<Submission> getAllReviewedSubmissions() {
		return dao.getAllReviewedSubmissions();
	}

	public List<Submission> getSubmissionsForUser(String userId) {
		return dao.getSubmissionsForUser(userId);
	}

	public boolean addSubmission(Submission submission) {
		return dao.addSubmission(submission);
	}

	public boolean updateSubmissionStatus(Submission s) { return dao.updateSubmissionStatus(s); }

	public Feedback getFeedback(long id) {

		Feedback f = dao.getFeedback(id);

		return f;
	}

	public List<Feedback> getFeedbackForSubmission(long submissionId) {
		return dao.getFeedbackForSubmission(submissionId);
	}

	public boolean addFeedback(Feedback feedback) {
		return dao.addFeedback(feedback);
	}

	@Setter
	private ProjectDao dao;
	
	@Setter
	private Cache cache;

}
