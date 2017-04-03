package org.sakaiproject.ddo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by james on 3/31/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumStatistics implements Serializable {
    private String Id;
    private int statisticCount;
}
