package org.sakaiproject.ddo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class to hold a byte[], the mimetype, and fileName.
 *
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */

@Data
@NoArgsConstructor
public class SubmissionFile {

    // Only accept files that are Microsoft Office and do not have macros enabled.
    public static final String[] AcceptableMimeTypes = new String[]{
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    };

    private byte[] bytes;
    private String mimeType;
    private String fileName;

}