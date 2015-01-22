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
public class Feedback implements Serializable {
    private long feedbackId;
    private long submissionId;
    private String reviewedBy;
    private Date reviewDate;
    private String comments;
    private String reviewedDocumentRef;
}
