package org.sakaiproject.ddo.dao.impl;

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.log4j.Logger;

import org.sakaiproject.component.cover.ServerConfigurationService;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.Submission;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.sakaiproject.ddo.dao.ProjectDao;


/**
 * Implementation of ProjectDao based on work by Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */
public class ProjectDaoImpl extends JdbcDaoSupport implements ProjectDao {

	private static final Logger log = Logger.getLogger(ProjectDaoImpl.class);
	
	private PropertiesConfiguration statements;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Submission getSubmission(long id) {
		if(log.isDebugEnabled()) {
			log.debug("getSubmission(" + id + ")");
		}

		try {
			return (Submission) getJdbcTemplate().queryForObject(getStatement("select.submission"),
					new Object[]{Long.valueOf(id)},
					new SubmissionMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Submission> getAllSubmissions() {
		if(log.isDebugEnabled()) {
			log.debug("getAllSubmissions()");
		}

		try {
			return getJdbcTemplate().query(getStatement("select.allsubmissions"),
					new SubmissionMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Submission> getAllWaitingSubmissions() {
		if(log.isDebugEnabled()) {
			log.debug("getAllWaitingSubmissions()");
		}

		try {
			return getJdbcTemplate().query(getStatement("select.allwaitingsubmissions"),
					new SubmissionMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Submission> getAllReviewedSubmissions() {
		if(log.isDebugEnabled()) {
			log.debug("getAllReviewedSubmissions()");
		}

		try {
			return getJdbcTemplate().query(getStatement("select.allreviewedsubmissions"),
					new SubmissionMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Submission> getSubmissionsForUser(String userId) {
		if(log.isDebugEnabled()) {
			log.debug("getSubmissionsForUser()");
		}

		try {
			return getJdbcTemplate().query(getStatement("select.usersubmissions"),
					new Object[]{userId},
					new SubmissionMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addSubmission(Submission submission) {
		if(log.isDebugEnabled()) {
			log.debug("addSubmission( " + submission.toString() + ")");
		}

		try {
			getJdbcTemplate().update(getStatement("insert.submission"),
					new Object[]{submission.getDocumentRef(),
							submission.getSubmittedBy(),
							submission.getPrimaryLanguageIsEnglish(),
							submission.getPrimaryLanguage(),
							submission.getStatus(),
							submission.getAssignmentTitle(),
							submission.getInstructorRequirements(),
							submission.getDueDate(),
							submission.getCourseTitle(),
							submission.getInstructor(),
							submission.getFeedbackFocus()
					}

			);
			return true;
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean updateSubmissionStatus(Submission s) {
		if(log.isDebugEnabled()) {
			log.debug("updateSubmissionStatus( " + s.toString() + ")");
		}

		try {
			getJdbcTemplate().update(getStatement("update.submissionStatus"),
					new Object[]{s.getStatus(),s.getSubmissionId()}
			);
			return true;
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Feedback getFeedback(long id) {
		if(log.isDebugEnabled()) {
			log.debug("getFeedback(" + id + ")");
		}

		try {
			return (Feedback) getJdbcTemplate().queryForObject(getStatement("select.feedback"),
					new Object[]{Long.valueOf(id)},
					new FeedbackMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Feedback> getFeedbackForSubmission(long submissionId) {
		if(log.isDebugEnabled()) {
			log.debug("getFeedbackForSubmission()");
		}

		try {
			return getJdbcTemplate().query(getStatement("select.feedbackforsubmission"),
					new Object[]{Long.valueOf(submissionId)},
					new FeedbackMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addFeedback(Feedback feedback) {
		if(log.isDebugEnabled()) {
			log.debug("addFeedback( " + feedback.toString() + ")");
		}

		try {
			getJdbcTemplate().update(getStatement("insert.feedback"),
					new Object[]{feedback.getSubmissionId(),
							feedback.getReviewedBy(),
							feedback.getComments(),
							feedback.getReviewedDocumentRef()
					}

			);
			return true;
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfWaitingSubmissions() {
		if(log.isDebugEnabled()) {
			log.debug("getNumberOfWaitingSubmissions()");
		}

		try {
			return getJdbcTemplate().queryForInt(getStatement("count.waitingsubmissions"));
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return 0;
		}
	}

	/**
	 * init
	 */
	public void init() {
		log.info("init()");
		
		//setup the vendor
		String vendor = ServerConfigurationService.getInstance().getString("vendor@org.sakaiproject.db.api.SqlService", null);
		
		//initialise the statements
		initStatements(vendor);
		
		//setup tables if we have auto.ddl enabled.
		boolean autoddl = ServerConfigurationService.getInstance().getBoolean("auto.ddl", true);
		if(autoddl) {
			initTables();
		}
	}
	
	/**
	 * Sets up our tables
	 */
	private void initTables() {
		try {
			getJdbcTemplate().execute(getStatement("create.submissiontable"));
			getJdbcTemplate().execute(getStatement("create.feedbacktable"));
		} catch (DataAccessException ex) {
			log.info("Error creating tables: " + ex.getClass() + ":" + ex.getMessage());
			return;
		}
	}
	
	/**
	 * Loads our SQL statements from the appropriate properties file
	 
	 * @param vendor	DB vendor string. Must be one of mysql, oracle, hsqldb
	 */
	private void initStatements(String vendor) {
		
		URL url = getClass().getClassLoader().getResource(vendor + ".properties"); 
		
		try {
			statements = new PropertiesConfiguration(); //must use blank constructor so it doesn't parse just yet (as it will split)
			statements.setReloadingStrategy(new InvariantReloadingStrategy());	//don't watch for reloads
			statements.setThrowExceptionOnMissing(true);	//throw exception if no prop
			statements.setDelimiterParsingDisabled(true); //don't split properties
			statements.load(url); //now load our file
		} catch (ConfigurationException e) {
			log.error(e.getClass() + ": " + e.getMessage());
			return;
		}
	}
	
	/**
	 * Get an SQL statement for the appropriate vendor from the bundle
	
	 * @param key
	 * @return statement or null if none found. 
	 */
	private String getStatement(String key) {
		try {
			return statements.getString(key);
		} catch (NoSuchElementException e) {
			log.error("Statement: '" + key + "' could not be found in: " + statements.getFileName());
			return null;
		}
	}
}
