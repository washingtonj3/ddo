package org.sakaiproject.ddo.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by dbauer1 on 12/9/14.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission implements Serializable {
    //Submission statuses
    public static final String STATUS_WAITING = "Awaiting Review";
    public static final String STATUS_UNDER = "Under Review";
    public static final String STATUS_REVIEWED = "Reviewed";

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
}
