package org.sakaiproject.ddo.logic;

import java.util.List;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.Thing;

/**
 * An example logic interface
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public interface ProjectLogic {

	/**
	 * Get a Thing
	 * @return
	 */
	public Thing getThing(long id);
	
	/**
	 * Get all Things
	 * @return
	 */
	public List<Thing> getThings();
	
	/**
	 * Add a new Thing
	 * @param t	Thing
	 * @return boolean if success, false if not
	 */
	public boolean addThing(Thing t);

	public Submission getSubmission(long id);
	public List<Submission> getAllSubmissions();
	public boolean addSubmission(Submission submission);
	public List<Submission> getSubmissionsForUser(String userId);
	public Feedback getFeedback(long id);
	public List<Feedback> getFeedbackForSubmission(long submissionId);
	public boolean addFeedback(Feedback feedback);
	public List<Submission> getAllWaitingSubmissions();
	public List<Submission> getAllReviewedSubmissions();
	public boolean updateSubmissionStatus(Submission s);
}
