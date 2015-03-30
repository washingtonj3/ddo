package org.sakaiproject.ddo.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import org.sakaiproject.ddo.model.Submission;

/**
 * Created by David P. Bauer on 12/10/14.
 */
public class SubmissionMapper implements RowMapper{
    /* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
    @Override
    public Submission mapRow(ResultSet rs, int rowNum) throws SQLException {

        Submission s = new Submission();

        s.setSubmissionId(rs.getLong("SUBMISSIONID"));
        s.setDocumentRef(rs.getString("DOCUMENTREF"));
        s.setSubmissionDate(new Date(rs.getTimestamp("SUBMISSIONDATE").getTime()));
        s.setSubmittedBy(rs.getString("SUBMITTEDBY"));
        s.setPrimaryLanguageIsEnglish(rs.getBoolean("PRIMARYLANGUAGEISENGLISH"));
        s.setPrimaryLanguage(rs.getString("PRIMARYLANGUAGE"));

        s.setStatus(rs.getString("STATUS"));

        s.setAssignmentTitle(rs.getString("ASSIGNMENTTITLE"));
        s.setInstructorRequirements(rs.getString("INSTRUCTORREQUIREMENTS"));
        s.setDueDate(rs.getDate("DUEDATE"));

        s.setCourseTitle(rs.getString("COURSETITLE"));
        s.setInstructor(rs.getString("INSTRUCTOR"));

        s.setFeedbackFocus(rs.getString("FEEDBACKFOCUS"));

        return s;
    }
}
