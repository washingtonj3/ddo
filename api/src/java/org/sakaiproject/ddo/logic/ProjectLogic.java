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
	boolean addSubmission(Submission submission);
	List<Submission> getSubmissionsForUser(String userId);
	Feedback getFeedback(long id);
	List<Feedback> getFeedbackForSubmission(long submissionId);
	boolean addFeedback(Feedback feedback);
	List<Submission> getAllWaitingSubmissions();
	List<Submission> getAllReviewedSubmissions();
	boolean updateSubmissionStatus(Submission s);
}
