package org.sakaiproject.ddo.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import org.sakaiproject.ddo.model.Feedback;

/**
 * Created by David P. Bauer on 12/15/14.
 */
public class FeedbackMapper implements RowMapper{
    /* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
    @Override
    public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {

        Feedback f = new Feedback();

        f.setFeedbackId(rs.getLong("FEEDBACKID"));
        f.setSubmissionId(rs.getLong("SUBMISSIONID"));
        f.setReviewedBy(rs.getString("REVIEWEDBY"));
        f.setReviewDate(new Date(rs.getTimestamp("REVIEWDATE").getTime()));
        f.setComments(rs.getString("COMMENTS"));
        f.setReviewedDocumentRef(rs.getString("REVIEWEDDOCUMENTREF"));

        return f;
    }
}
