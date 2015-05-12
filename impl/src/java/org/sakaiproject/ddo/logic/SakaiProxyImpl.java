package org.sakaiproject.ddo.logic;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ContentTypeImageService;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.ddo.model.Submission;
import org.sakaiproject.ddo.model.SubmissionFile;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation of SakaiProxy
 * 
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public class SakaiProxyImpl implements SakaiProxy {

	private static final Logger log = Logger.getLogger(SakaiProxyImpl.class);
    
	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentSiteId(){
		return toolManager.getCurrentPlacement().getContext();
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	public User getCurrentUser() {
		return userDirectoryService.getCurrentUser();
	}

	/**
	* {@inheritDoc}
	*/
	public String getUserEid(String userId){
		String eid = null;
		try {
			eid = userDirectoryService.getUser(userId).getEid();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get eid for id: " + userId + " : " + e.getClass() + " : " + e.getMessage());
		}
		return eid;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserDisplayId(String userId) {
		String displayId = null;
		try {
			displayId = userDirectoryService.getUser(userId).getDisplayId();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get displayId for id: " + userId + " : " + e.getClass() + " : " + e.getMessage());
		}
		return displayId;
	}

	/**
    * {@inheritDoc}
    */
	public String getUserIdForEid(String eid) {
		String userUuid = null;
		try {
			userUuid = userDirectoryService.getUserByEid(eid).getId();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get id for eid: " + eid + " : " + e.getClass() + " : " + e.getMessage());
		}
		return userUuid;
	}

	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentUserDisplayName() {
	   return userDirectoryService.getCurrentUser().getDisplayName();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserDisplayName(String userId) {
		String userDisplayName = "";
		try {
			userDisplayName = userDirectoryService.getUser(userId).getDisplayName();
		} catch (Exception e) {
			log.debug("Could not get user " + userId + e);
		}
		return userDisplayName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserFirstName(String userId) {
		String email = null;
		try {
			email = userDirectoryService.getUser(userId).getFirstName();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get first name for id: " + userId + " : " + e.getClass() + " : " + e.getMessage());
		}
		return email;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserLastName(String userId) {
		String email = null;
		try {
			email = userDirectoryService.getUser(userId).getLastName();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get last name for id: " + userId + " : " + e.getClass() + " : " + e.getMessage());
		}
		return email;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserEmail(String userId) {
		String email = null;
		try {
			email = userDirectoryService.getUser(userId).getEmail();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get email for id: " + userId + " : " + e.getClass() + " : " + e.getMessage());
		}
		return email;
	}

	/**
 	* {@inheritDoc}
 	*/
	public boolean isSuperUser() {
		return securityService.isSuperUser();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<String> getStudentWorkerIds() {
		try {
			Set<Member> membersSet = authzGroupService.getAuthzGroup("/ddo").getMembers();
			Set<String> studentWorkerIds = new HashSet<String>();
			for(Member m : membersSet) {
				studentWorkerIds.add(m.getUserId());
			}
			return studentWorkerIds;
		} catch (Exception e) {
			log.warn("Cannot get student workers");
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isStudentWorker() {
		String currentUserId = getCurrentUserId();
		for(String id : getStudentWorkerIds()) {
			if (currentUserId.equals(id))
				return true;
		}
		return false;
	}

	/**
 	* {@inheritDoc}
 	*/
	public void postEvent(String event,String reference,boolean modify) {
		eventTrackingService.post(eventTrackingService.newEvent(event,reference,modify));
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public boolean getConfigParam(String param, boolean dflt) {
		return serverConfigurationService.getBoolean(param, dflt);
	}
	
	/**
 	* {@inheritDoc}
 	*/
	public String getConfigParam(String param, String dflt) {
		return serverConfigurationService.getString(param, dflt);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean saveFile(String fullResourceId, String userId, String fileName, String mimeType, byte[] fileData) {
		ContentResourceEdit resource = null;
		boolean result = true;

		try {
			enableSecurityAdvisor();

			try {

				resource = contentHostingService.addResource(fullResourceId);
				resource.setContentType(mimeType);
				resource.setContent(fileData);
				ResourcePropertiesEdit props = resource.getPropertiesEdit();
				props.addProperty(ResourceProperties.PROP_CONTENT_TYPE, mimeType);
				props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, fileName);
				props.addProperty(ResourceProperties.PROP_DESCRIPTION, fileName);
				props.addProperty(ResourceProperties.PROP_CREATOR, userId);
				contentHostingService.commitResource(resource, NotificationService.NOTI_NONE);
				result = true;
			}
			catch (IdUsedException e){
				contentHostingService.cancelResource(resource);
				log.error("SakaiProxy.saveFile(): id= " + fullResourceId + " is in use : " + e.getClass() + " : " + e.getMessage());
				result = false;
			}
			catch (Exception e){
				contentHostingService.cancelResource(resource);
				log.error("SakaiProxy.saveFile(): failed: " + e.getClass() + " : " + e.getMessage());
				result = false;
			}

		} catch (Exception e) {
			log.error("SakaiProxy.saveFile():" + e.getClass() + ":" + e.getMessage());
			result = false;
		} finally {
			disableSecurityAdvisor();
		}
		return result;

	}

	/**
	 * {@inheritDoc}
	 */
	public String getResourceIconUrl(String resourceId) {
		if(StringUtils.isBlank(resourceId)) {
			return "Unknown";
		}

		try {
			enableSecurityAdvisor();
			ContentResource resource = contentHostingService.getResource(resourceId);
			String imageUrl = "/library/image/" + contentTypeImageService.getContentTypeImage(resource.getProperties().getProperty(ResourceProperties.PROP_CONTENT_TYPE));
			return imageUrl;
		}
		catch(Exception e) {
			log.error("Unable to get content type image. " + e);
			return "/library/image/sakai/generic.gif";
		}
		finally	{
			disableSecurityAdvisor();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public String getResourceFileSize(String resourceId) {
		if(StringUtils.isBlank(resourceId)) {
			return "Unknown";
		}

		try {
			enableSecurityAdvisor();
			ContentResource resource = contentHostingService.getResource(resourceId);
			String fileSize = resource.getProperties().getPropertyFormatted(ResourceProperties.PROP_CONTENT_LENGTH);
			return fileSize;
		}
		catch(Exception e) {
			log.error("Unable to get resource file size. " + e);
			return "Unknown";
		}
		finally	{
			disableSecurityAdvisor();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public SubmissionFile getResource(String resourceId) {

		SubmissionFile file = new SubmissionFile();

		if(StringUtils.isBlank(resourceId)) {
			return null;
		}

		try {
			enableSecurityAdvisor();
			try {
				ContentResource resource = contentHostingService.getResource(resourceId);
				if(resource == null){
					return null;
				}
				file.setBytes(resource.getContent());
				file.setMimeType(resource.getContentType());
				file.setFileName(resource.getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME));
				return file;
			}
			catch(Exception e){
				log.error("SakaiProxy.getResource() failed for resourceId: " + resourceId + " : " + e.getClass() + " : " + e.getMessage());
			}
		} catch (Exception e) {
			log.error("SakaiProxy.getResource():" + e.getClass() + ":" + e.getMessage());
		}
		finally	{
			disableSecurityAdvisor();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeResource(String resourceId) {

		boolean result = false;

		try {
			enableSecurityAdvisor();

			contentHostingService.removeResource(resourceId);

			result = true;
		} catch (Exception e) {
			log.error("SakaiProxy.removeResource() failed for resourceId "
					+ resourceId + ": " + e.getMessage());
			return false;
		} finally {
			disableSecurityAdvisor();
		}

		return result;
	}

	// PRIVATE METHODS FOR SAKAIPROXY

	/**
	 * Setup a security advisor for this transaction
	 */
	private void enableSecurityAdvisor() {
		securityService.pushAdvisor(new SecurityAdvisor(){
			public SecurityAdvice isAllowed(String userId, String function, String reference){
				return SecurityAdvice.ALLOWED;
			}
		});
	}

	/**
	 * Remove security advisor from the stack
	 */
	private void disableSecurityAdvisor(){
		securityService.popAdvisor();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDocumentResourcePath(String fileName) {
		String slash = Entity.SEPARATOR;

		StringBuilder path = new StringBuilder();
		path.append(slash);
		path.append("private");
		path.append(slash);
		path.append("ddo");
		path.append(slash);
		path.append(getCurrentUserId());
		path.append(slash);
		path.append(String.valueOf((Calendar.getInstance().get(Calendar.YEAR))));
		path.append(slash);
		path.append(generateUuid());
		path.append(slash);
		path.append(fileName);

		return path.toString();
	}

	/**
	 * Generate a UUID
	 * @return
	 */
	public static String generateUuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendSubmissionNotification(Submission s) {
		String submitterId = s.getSubmittedBy();

		DateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm a");

		String toStr = serverConfigurationService.getString("ddo.staff.email.address");
		String headerToStr = serverConfigurationService.getString("ddo.staff.email.display");
		String fromStr = serverConfigurationService.getString("ddo.notification.email.display");
		String subject = "[DDO] New Isidore Digital Drop-Off Submission Waiting";

		List<String> additionalHeaders = new ArrayList<String>();
		additionalHeaders.add("Content-type: text/html; charset=UTF-8");

		StringBuilder body = new StringBuilder();
		body.append("A new file has been submitted through the Isidore Digital Drop-Off tool.");
		body.append("<br />");
		body.append("<br />");
		body.append("Please visit the Isidore Digital Drop-Off tool to review the submitted file.");
		body.append("<br />");
		body.append("<br />");
		body.append("<strong><u>Submission Details</u></strong>");
		body.append("<br />");
		body.append("<br />");
		body.append("<strong>Student:</strong> ");
		body.append(getUserDisplayName(submitterId));
		body.append("<br />");
		body.append("<strong>File:</strong> ");
		body.append(getResource(s.getDocumentRef()).getFileName());
		body.append("<br />");
		body.append("<strong>Date:</strong> ");
		body.append(df.format(new Date()));
		body.append("<br />");
		body.append("<strong>Course:</strong> ");
		body.append(s.getCourseTitle());
		body.append("<br />");
		body.append("<strong>Assignment Title:</strong> ");
		body.append(s.getAssignmentTitle());
		body.append("<br />");
		body.append("<strong>Instructor Name:</strong> ");
		body.append(s.getInstructor());

		emailService.send(fromStr, toStr, subject, body.toString(), headerToStr, null, additionalHeaders);
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendFeedbackNotification(Submission s) {
		String submitterId = s.getSubmittedBy();

		String toStr = getUserEmail(submitterId);
		String headerToStr = getUserDisplayName(submitterId) + " <" + getUserEmail(submitterId) + ">";
		String fromStr = serverConfigurationService.getString("ddo.staff.email.display");
		String replyToStr = serverConfigurationService.getString("ddo.staff.email.address");
		String subject = "[DDO] Your Write Place Digital Drop-Off submission has been reviewed";

		List<String> additionalHeaders = new ArrayList<String>();
		additionalHeaders.add("Content-type: text/html; charset=UTF-8");

		StringBuilder body = new StringBuilder();
		body.append("Hello ");
		body.append(getUserFirstName(s.getSubmittedBy()));
		body.append(".<br />");
		body.append("<br />");
		body.append("Thank you for using the Digital Drop-Off service available in Isidore.");
		body.append("<br />");
		body.append("<br />");
		body.append("Your recent submission has been reviewed by a writing consultant.");
		body.append("<br />");
		body.append("<br />");
		body.append("Please log in to Isidore, click on the Digital Drop-Off tool located in the left-hand menubar, and click on the ‘View Feedback’ link next to your file to view your feedback.");
		body.append("<br />");
		body.append("<br />");
		body.append("Please send any questions to writeplace@udayton.edu.");

		emailService.send(fromStr, toStr, subject, body.toString(), headerToStr, replyToStr, additionalHeaders);
	}

	public Set<User> getCurrentInstructorsForCurrentUser() {
		User user = getCurrentUser();
		Set<User> instructors = new HashSet<User>();
		Set<EnrollmentSet> enrolledSets = courseManagementService.findCurrentlyEnrolledEnrollmentSetsUDayton(user.getEid());
		for (EnrollmentSet es : enrolledSets) {
			for (String i : es.getOfficialInstructors()) {
				try {
					instructors.add(userDirectoryService.getUserByEid(i));
				} catch (Exception e) {
					log.error("Couldn't get instructor : " + e.getMessage());
				}
			}
		}
		return instructors;
	}


	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

	@Getter @Setter
	private ContentHostingService contentHostingService;
	
	@Getter @Setter
	private ToolManager toolManager;
	
	@Getter @Setter
	private SessionManager sessionManager;
	
	@Getter @Setter
	private UserDirectoryService userDirectoryService;
	
	@Getter @Setter
	private SecurityService securityService;
	
	@Getter @Setter
	private EventTrackingService eventTrackingService;
	
	@Getter @Setter
	private ServerConfigurationService serverConfigurationService;
	
	@Getter @Setter
	private SiteService siteService;

	@Getter @Setter
	private AuthzGroupService authzGroupService;

	@Getter @Setter
	private ContentTypeImageService contentTypeImageService;

	@Getter @Setter
	private EmailService emailService;

	@Getter @Setter
	private CourseManagementService courseManagementService;
}
