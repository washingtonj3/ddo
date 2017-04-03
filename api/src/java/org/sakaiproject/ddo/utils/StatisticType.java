package org.sakaiproject.ddo.utils;

/**
 * Created by james on 3/31/17.
 */
public enum StatisticType {

    REVIEWERS ("Number of Reviews Completed by Consultant"),
    TOPINSTRUCTORS ("Top Three Instructors by Number of Student Submissions"),
    TOPSECTIONS ("");

    public String getStatisticName() {
        return statisticName;
    }

    private String statisticName;

    StatisticType(String statisticName) {
        this.statisticName = statisticName;
    }
}
