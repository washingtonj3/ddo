package org.sakaiproject.ddo.dao;

import java.util.List;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.Thing;

/**
 * DAO interface for our project
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public interface ProjectDao {

	/**
	 * Gets a single Thing from the db
	 * 
	 * @return an item or null if no result
	 */
	public Thing getThing(long id);
	
	/**
	 * Get all Things
	 * @return a list of items, an empty list if no items
	 */
	public List<Thing> getThings();
		
	/**
	 * Add a new Thing record to the database. Only the name property is actually used.
	 * @param t	Thing
	 * @return	true if success, false if not
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
