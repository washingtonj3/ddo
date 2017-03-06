/*
 *  Copyright (c) 2016, University of Dayton
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *              http://opensource.org/licenses/ecl2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.sakaiproject.ddo.logic;

import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.SubmissionFile;
import org.sakaiproject.user.api.User;

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
	 *
	 * @param userId
	 * @return User's sort name
     */
	String getUserSortName(String userId);

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
	 * Get a set of the ddo admin ids
	 * @return
	 */
	Set<String> getDDOAdminIds();

	/**
	 * Returns true if the current user is a student worker
	 * @return
	 */
	boolean isStudentWorker();

	/**
	 * Returns true if the current user is a DDO Admin
	 * @return
	 */
	boolean isDDOAdmin();

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

	/**
	 * Retrieve a set of instructors' IDs for the current
	 * authenticated user.
	 *
	 * @return
	 */
	Set<String> getCurrentInstructorsForCurrentUser();

	/**
	 * Retrieve the current sections that the the current
	 * authenticated user is registered for.
	 *
	 * @return
	 */
	Set<String> getCurrentSectionsForCurrentUser();

	/**
	 * Get the human-readable course offering title for a given Section Eid.
	 *
	 * @param sectionEid
	 * @return
	 */
	String getCourseOfferingTitleForSection(String sectionEid);

	/**
	 * Send an email notification to an instructor to inform them that one of their
	 * students used the DDO service.
	 *
	 * @param instructorEmail
	 * @param currentUserId
	 * @param submission
	 */
	void sendNotificationToInstructor(String instructorEmail, String currentUserId, Submission submission);

	/**
	 * Send an email receipt to the user that used the DDO service.
	 *
	 * @param currentUserId
	 * @param submission
	 */
	void sendReceipt(String currentUserId, Submission submission);

	/**
	 * Adds a user to the ddo realm.
	 *
	 * @param userId User's id
	 * @param roleId The role that the user will have ddostaff or ddoadmin
     * @return
     */
	boolean addUserToDDO(String userId, String roleId);

	/**
	 * Removes a user from the ddo realm.
	 * @param userId
	 * @return
     */
	boolean removeUserFromDDO(String userId);

	/**
	 * Checks the DDO realm's properties for whether or not DDO is closed
	 * @return A boolean value that is true if closed or false if open
	 */
	boolean isDDOClosed();

	/**
	 * Gets the "closed" message from the DDO realm properties.
	 * @return A string containing the closed message or null if there is none.
	 */
	String getDDOClosedMessage();

	/**
	 * Sets specified property in the DDO realm to a specified value.
	 *
	 * @param name The name of the property being set in the ddo realm
	 * @param value The value of the property
	 */
	void setDDORealmProperty(String name, Object value);
}
