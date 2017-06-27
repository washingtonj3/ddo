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
public class Export implements Serializable {
    private long submissionId;
    private String documentRef;
    private Date submissionDate;
    private String submittedBy;
    private String status;
    private String assignmentTitle;
    private String instructorRequirements;
    private String courseTitle;
    private String instructor;
    private Date dueDate;
    private Boolean primaryLanguageIsEnglish;
    private String primaryLanguage;
    private String feedbackFocus;
    private Long feedbackId;
    private String reviewedBy;
    private Date reviewDate;
    private String comments;
    private String reviewedDocumentRef;
}
