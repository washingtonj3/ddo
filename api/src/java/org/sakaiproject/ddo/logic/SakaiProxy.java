package org.sakaiproject.ddo.logic;

import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.SubmissionFile;
import org.sakaiproject.user.api.User;

import java.util.List;
import java.util.Set;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * Based on Sakai Proxy by Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public interface SakaiProxy {

	/**
	 * Get current siteid
	 * @return
	 */
	String getCurrentSiteId();

	/**
	 * Get current user id
	 * @return
	 */
	String getCurrentUserId();

	/**
	 * Get current user's display name
	 * @return
	 */
	String getCurrentUserDisplayName();

	/**
	 * Get current user
	 * @return
	 */
	User getCurrentUser();

	/**
	 * Convert internal userid to eid (3373898)
	 * @return
	 */
	String getUserEid(String userId);

	/**
	 * Convert internal userid to display id (jsmith26)
	 * @return
	 */
	String getUserDisplayId(String userId);

	/**
	 * Convert eid to internal userid
	 * @return
	 */
	String getUserIdForEid(String eid);

	/**
	 * Get displayname of a given userid (internal id)
	 * @return
	 */
	String getUserDisplayName(String userId);

	/**
	 * Get firstname of a given userid (internal id)
	 * @return
	 */
	String getUserFirstName(String userId);

	/**
	 * Get lastname of a given userid (internal id)
	 * @return
	 */
	String getUserLastName(String userId);

	/**
	 * Get email address for a given userid (internal id)
	 * @return
	 */
	String getUserEmail(String userId);

	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	boolean isSuperUser();

	/**
	 * Get a set of the student worker ids
	 * @return
	 */
	Set<String> getStudentWorkerIds();

	/**
	 * Returns true if the current user is a student worker
	 * @return
	 */
	boolean isStudentWorker();

	/**
	 * Save a file to CHS
	 *
	 * @param fullResourceId
	 * @param userId
	 * @param fileName
	 * @param mimeType
	 * @param fileData
	 * @return
	 */
	boolean saveFile(String fullResourceId, String userId, String fileName, String mimeType, byte[] fileData);

	/**
	 * Get the icon associated with a resource's file type
	 *
	 * @param resourceId
	 * @return
	 */
	String getResourceIconUrl(String resourceId);

	/**
	 * Get a resource's file size
	 *
	 * @param resourceId
	 * @return
	 */
	String getResourceFileSize(String resourceId);

	/**
	 * Retrieve a resource from ContentHosting with byte[] and mimetype
	 *
	 * @param resourceId	the full resourceId of the file
	 */
	SubmissionFile getResource(String resourceId);

	/**
	 * Removes the specified resource.
	 *
	 * @param resourceId the ID of the resource to remove.
	 * @return <code>true</code> if the resource is successfully removed,
	 * <code>false</code> if the remove operation fails.
	 */
	boolean removeResource(String resourceId);

	/**
	 * Post an event to Sakai
	 * 
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 * 
	 */
	void postEvent(String event,String reference,boolean modify);
		
	/**
	 * Get a configuration parameter as a boolean
	 * 
	 * @param	dflt the default value if the param is not set
	 * @return
	 */
	boolean getConfigParam(String param, boolean dflt);
	
	/**
	 * Get a configuration parameter as a String
	 * 
	 * @param	dflt the default value if the param is not set
	 * @return
	 */
	String getConfigParam(String param, String dflt);

	/**
	 * Creates a new docuement resource path that is used as a resource id
	 *
	 * @param fileName
	 * @return
	 */
	String getDocumentResourcePath(String fileName);

	/**
	 * Sends an email to DDO staff that notifies them a new submission has been added
	 * Sakai property to specify staff email address: ddo.staff.email
	 *
	 * @param s the submission
	 */
	void sendSubmissionNotification(Submission s);

	/**
	 * Sends a notification email to the original submitter that tells them
	 * that their submission has been reviewed and returned.
	 * Sakai property to specify staff email address: ddo.staff.email
	 *
	 * @param s the submission that was reviewed
	 */
	void sendFeedbackNotification(Submission s);

	Set<String> getCurrentInstructorsForCurrentUser();

	Set<String> getCurrentSectionsForCurrentUser();

	String getCourseOfferingTitleForSection(String sectionEid);
}
