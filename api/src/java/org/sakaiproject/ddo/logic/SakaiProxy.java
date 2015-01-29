package org.sakaiproject.ddo.logic;

import org.sakaiproject.ddo.model.SubmissionFile;
import org.sakaiproject.user.api.User;

import java.util.Set;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public interface SakaiProxy {

	/**
	 * Get current siteid
	 * @return
	 */
	public String getCurrentSiteId();

	/**
	 * Get current user id
	 * @return
	 */
	public String getCurrentUserId();

	public String getCurrentUserDisplayName();

	/**
	 * Get current user
	 * @return
	 */
	public User getCurrentUser();

	/**
	 * Convert internal userid to eid (3373898)
	 * @return
	 */
	public String getUserEid(String userId);

	/**
	 * Convert internal userid to eid (jsmith26)
	 * @return
	 */
	public String getUserDisplayId(String userId);

	/**
	 * Convert eid to internal userid
	 * @return
	 */
	public String getUserIdForEid(String eid);

	/**
	 * Get displayname of a given userid (internal id)
	 * @return
	 */
	public String getUserDisplayName(String userId);

	/**
	 * Get firstname of a given userid (internal id)
	 * @return
	 */
	public String getUserFirstName(String userId);

	/**
	 * Get lastname of a given userid (internal id)
	 * @return
	 */
	public String getUserLastName(String userId);

	/**
	 * Get email address for a given userid (internal id)
	 * @return
	 */
	public String getUserEmail(String userId);

	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	public boolean isSuperUser();

	public Set<String> getStudentWorkerIds();

	public boolean isStudentWorker();

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
	public boolean saveFile(String fullResourceId, String userId, String fileName, String mimeType, byte[] fileData);

	public String getResourceIconUrl(String resourceId);

	public String getResourceFileSize(String resourceId);

	/**
	 * Retrieve a resource from ContentHosting with byte[] and mimetype
	 *
	 * @param resourceId	the full resourceId of the file
	 */
	public SubmissionFile getResource(String resourceId);

	/**
	 * Removes the specified resource.
	 *
	 * @param resourceId the ID of the resource to remove.
	 * @return <code>true</code> if the resource is successfully removed,
	 * <code>false</code> if the remove operation fails.
	 */
	public boolean removeResource(String resourceId);

	/**
	 * Post an event to Sakai
	 * 
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 * 
	 */
	public void postEvent(String event,String reference,boolean modify);
		
	/**
	 * Get a configuration parameter as a boolean
	 * 
	 * @param	dflt the default value if the param is not set
	 * @return
	 */
	public boolean getConfigParam(String param, boolean dflt);
	
	/**
	 * Get a configuration parameter as a String
	 * 
	 * @param	dflt the default value if the param is not set
	 * @return
	 */
	public String getConfigParam(String param, String dflt);

	public String getDocumentResourcePath(String fileName);
}
