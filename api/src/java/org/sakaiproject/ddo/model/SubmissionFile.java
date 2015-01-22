package org.sakaiproject.ddo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper class to hold a byte[], the mimetype, and fileName.
 *
 * @author David P. Bauer (dbauer1@udayton.edu)
 *
 */

@Data
@NoArgsConstructor
public class SubmissionFile {

    private byte[] bytes;
    private String mimeType;
    private String fileName;

}