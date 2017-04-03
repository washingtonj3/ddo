package org.sakaiproject.ddo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sakaiproject.ddo.utils.StatisticType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by james on 3/31/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberStat implements Serializable{
    private StatisticType statsType;
    private Date startDate;
    private Date endDate;
    private List<NumStatistics> numStatisticsList;
}
