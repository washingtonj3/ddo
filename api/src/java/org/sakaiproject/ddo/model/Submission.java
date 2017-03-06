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

package org.sakaiproject.ddo.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author David P. Bauer (dbauer1@udayton.edu)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission implements Serializable {
    //Submission statuses
    public static final String STATUS_WAITING = "Awaiting Review";
    public static final String STATUS_UNDER = "Under Review";
    public static final String STATUS_REVIEWED = "Reviewed";
    public static final String STATUS_ARCHIVED = "Archived";

    private long submissionId;
    private String documentRef;
    private Date submissionDate;
    private String submittedBy;
    private Boolean primaryLanguageIsEnglish;
    private String primaryLanguage;

    private String status;

    private String assignmentTitle;
    private String instructorRequirements;
    private Date dueDate;

    private String courseTitle;
    private String instructor;

    private String feedbackFocus;

    public static final Comparator<Submission> submissionComparator = (s1, s2) -> {
        long t1 = s1.getSubmissionDate().getTime();
        long t2 = s2.getSubmissionDate().getTime();
        // This is opposite of standard time comparator in order to have the
        // most recent submissions at the top of the list.
        if (t1 > t2) {
            return -1;
        } else if (t1 < t2) {
            return 1;
        } else {
            return 0;
        }
    };
}
