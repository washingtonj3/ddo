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

package org.sakaiproject.ddo.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import org.sakaiproject.ddo.model.Export;

/**
 * Created by David P. Bauer on 12/10/14.
 */
public class ExportMapper implements RowMapper{
    /* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
    @Override
    public Export mapRow(ResultSet rs, int rowNum) throws SQLException {
        Calendar now = Calendar.getInstance();
        Export e = new Export();

        e.setSubmissionId(rs.getLong("SUBMISSIONID"));
        e.setDocumentRef(rs.getString("DOCUMENTREF"));
        e.setSubmissionDate(new Date(rs.getTimestamp("SUBMISSIONDATE").getTime()));
        e.setSubmittedBy(rs.getString("SUBMITTEDBY"));
        e.setStatus(rs.getString("STATUS"));
        e.setAssignmentTitle(rs.getString("ASSIGNMENTTITLE"));
        e.setInstructorRequirements(rs.getString("INSTRUCTORREQUIREMENTS"));
        e.setCourseTitle(rs.getString("COURSETITLE"));
        e.setInstructor(rs.getString("INSTRUCTOR"));
        e.setDueDate(rs.getDate("DUEDATE"));
        e.setPrimaryLanguageIsEnglish(rs.getBoolean("PRIMARYLANGUAGEISENGLISH"));
        e.setPrimaryLanguage(rs.getString("PRIMARYLANGUAGE"));
        e.setFeedbackFocus(rs.getString("FEEDBACKFOCUS"));
        e.setFeedbackId(rs.getLong("FEEDBACKID"));
        e.setReviewedBy((StringUtils.isBlank(rs.getString("REVIEWEDBY"))) ? "" : rs.getString("REVIEWEDBY"));
        e.setReviewDate(new Date((rs.getTimestamp("REVIEWDATE") == null ? now.getTime() : rs.getTimestamp("REVIEWDATE")).getTime()));
        e.setComments(rs.getString("COMMENTS"));
        e.setReviewedDocumentRef(rs.getString("REVIEWEDDOCUMENTREF"));

        return e;
    }
}
