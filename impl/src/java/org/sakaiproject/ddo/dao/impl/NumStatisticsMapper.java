package org.sakaiproject.ddo.dao.impl;

import org.sakaiproject.ddo.model.NumStatistics;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by james on 3/31/17.
 */
public class NumStatisticsMapper implements RowMapper {
    @Override
    public NumStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {

        NumStatistics numStatistics = new NumStatistics();

        numStatistics.setId(rs.getString("Id"));
        numStatistics.setStatisticCount(rs.getInt("countStat"));

        return numStatistics;
    }
}
