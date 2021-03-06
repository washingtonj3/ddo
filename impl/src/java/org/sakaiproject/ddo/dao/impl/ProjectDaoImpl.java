package org.sakaiproject.ddo.dao.impl;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.sakaiproject.component.cover.ServerConfigurationService;

import org.sakaiproject.ddo.model.Feedback;
import org.sakaiproject.ddo.model.NumStatistics;
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
	public List<Submission> getAllArchivedSubmissions() {
		if(log.isDebugEnabled()) {
			log.debug("getAllArchivedSubmissions()");
		}

		try {
			return getJdbcTemplate().query(getStatement("select.allarchivedsubmissions"),
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
					submission.getDocumentRef(),
					submission.getSubmittedBy(),
					submission.getPrimaryLanguageIsEnglish(),
					submission.getPrimaryLanguage(),
					submission.getStatus(),
					submission.getAssignmentTitle(),
					submission.getInstructorRequirements(),
					submission.getDueDate(),
					submission.getCourseTitle(),
					submission.getInstructor(),
					submission.getFeedbackFocus());
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
					s.getStatus(),s.getSubmissionId());
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
			// first check to see if there was already a review saved
			if(!getFeedbackForSubmission(feedback.getSubmissionId()).isEmpty()) {
				return false;
			}
			getJdbcTemplate().update(getStatement("insert.feedback"),
					feedback.getSubmissionId(),
					feedback.getReviewedBy(),
					feedback.getComments(),
					feedback.getReviewedDocumentRef());
			return true;
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean updateFeedback(Feedback feedback) {
		if(log.isDebugEnabled()) {
			log.debug("updateFeedback( " + feedback.toString() + ")");
		}

		try {
			getJdbcTemplate().update(getStatement("update.feedback"),
					feedback.getReviewedBy(),
					new Timestamp(feedback.getReviewDate().getTime()),
					feedback.getReviewedDocumentRef(),
					feedback.getComments(),
					feedback.getFeedbackId());
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
			return getJdbcTemplate().queryForObject(getStatement("count.waitingsubmissions"), Integer.class);
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

	/**
	 * Gets the number of submissions between two dates with different statuses
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank before the function it is the current date
	 * @param statusString Status type of the submission to search for if blank or null gets all submission in the date range
	 *
	 * @return returns the number of submissions matching the parameters or a 0 on error
	 */
	public int getNumberofSubmissionsDao(Date startDate, Date endDate, String statusString) {
		try {
			if(StringUtils.isBlank(statusString))//catches if the string is null or blank
			{
				return getJdbcTemplate().queryForObject(getStatement("stats.numberOfSubmissionsNoStatus"), Integer.class,
						startDate, endDate);
			}
			else{
				return getJdbcTemplate().queryForObject(getStatement("stats.numberOfSubmissions"), Integer.class,
						startDate, endDate, statusString);
			}

		} catch (DataAccessException ex) {
				return 0;
		}
	}

	/**
	 * Gets the number of unique users between the two dates
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank before the function it is the current date
	 *
	 * @return returns the number of unique users matching the parameters or a 0 on error
	 */
	public int getNumberOfUniqueUsersDao(Date startDate, Date endDate) {
		try {
			return getJdbcTemplate().queryForObject(getStatement("stats.numberOfUniqueUsers"), Integer.class,
						startDate, endDate);
		} catch (DataAccessException ex) {
			return 0;
		}
	}

/**
 * Gets the number of repeat users between the two dates
 *
 * @param startDate    Starting date for the date range search: Never null or after endDate
 * @param endDate      End date for the date range if it was blank before the function it is the current date
 *
 * @return returns the number of repeat users between the two dates or a 0 on error
 */
public int getNumberOfRepeatUsersDao(Date startDate, Date endDate) {
		try {
			return getJdbcTemplate().queryForObject(getStatement("stats.numberOfRepeatUsers"), Integer.class,
						startDate, endDate);
		} catch (DataAccessException ex) {
			return 0;
		}
}

	/**
	 * Gets the number of Consultants who reviewed between two dates
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank or null before the function it is set as the current date
	 *
	 * @return returns the number of Consultants between the two parameters or a 0 on error
	 */
	public int getNumberOfConsultantsDao(Date startDate, Date endDate) {
		try {
			return getJdbcTemplate().queryForObject(getStatement("stats.numberOfConsultants"), Integer.class, startDate, endDate);
		} catch (DataAccessException ex) {
			return 0;
		}
	}

	/**
	 * Gets the number of reviews per consultant
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the reviewerId and number of reviewed papers for each reviewer who reviewed within the timeframe in list form
	 */
	public List<NumStatistics> numberOfReviewsPerConsultantDao(Date startDate, Date endDate) {
		try {
			return getJdbcTemplate().query(getStatement("stats.numberOfReviewsPerConsultant"),
					new Object[]{startDate, endDate},
					new NumStatisticsMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * Gets a list containing the Insturctor Name and how many papers their students submitted
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the three instructors with the most user submissions within the timeframe in list form
	 */
	public List<NumStatistics> topThreeInstructorsStatsDao(Date startDate, Date endDate) {
		try {
			return getJdbcTemplate().query(getStatement("stats.topThreeInstructorStats"),
					new Object[]{startDate, endDate},
					new NumStatisticsMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * Gets a list containing the Section Name and how many papers students in this section submitted
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns the three instructors with the most user submissions within the timeframe in list form
	 */
	public List<NumStatistics> topThreeSectionsStatsDao(Date startDate, Date endDate) {
		try {
			return getJdbcTemplate().query(getStatement("stats.topThreeSectionStats"),
					new Object[]{startDate, endDate},
					new NumStatisticsMapper()
			);
		} catch (DataAccessException ex) {
			log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
			return null;
		}
	}

	/**
	 * Gets the average time between submission and review in milliseconds
	 *
	 * @param startDate
	 * @param endDate
	 *
	 * @return returns a int containing the average time between submission and review in milliseconds
	 */
	public int getAvgTurnaroundTimeDao(Date startDate, Date endDate) {
		try {
			Integer turnAroundTime = getJdbcTemplate().queryForObject(getStatement("stats.averageTurnAroundTime"), Integer.class, startDate, endDate);
			return turnAroundTime == null ? 0 : turnAroundTime;
			}
		catch (DataAccessException ex) {
			return 0;
		}
	}

	/**
	 * Gets the average number of submissions in the date range
	 *
	 * @param startDate    Starting date for the date range search: Never null or after endDate
	 * @param endDate      End date for the date range if it was blank before the function it is the current date
	 *
	 * The query results in null when there are no submissions in the date range
	 * Rounds the result to two decimal places
	 *
	 * @return returns the average number of submissions in the parameter range or a 0 on error or null
	 */
	public double getAvgNumberofSubmissionsDao(Date startDate, Date endDate) {
		try {
			Double result = getJdbcTemplate().queryForObject(getStatement("stats.avgNumberOfSubmissionsNoStatus"), Double.class,
					startDate, endDate);
			if(result == null){
				return 0;
			}
			else{
				result = (double) Math.round(result * 100) / 100;
				return result;
			}
		}catch (DataAccessException ex) {
			return 0;
		}
	}
}
