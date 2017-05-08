package org.sakaiproject.ddo.utils;

/**
 * Created by james on 3/31/17.
 */
public enum StatisticType {

    REVIEWERS ("Number of Reviews Completed by Consultant"),
    TOPINSTRUCTORS ("Top Three Instructors by Number of Student Submissions"),
    TOPSECTIONS ("Top Three Sections by Number of Student Submissions"),
    NUMBEROFSUBMISSION ("Total Number of Submissions for Current Status"),
    NUMBEROFUNIQUEUSERS("Total Number of Unique Users"),
    NUMBEROFREPEATUSERS("Total Number of Repeat Users"),
    AVGNUMBEROFSUBMISSIONS("Average Number of Submissions"),
    NUMBEROFCONSULTANTS("Total Number of Consultants"),
    NUMBEROFREVIEWSPERCONSULTANT("Number of Reviews for Each Consultant"),
    AVGTURNAROUNDTIME("Average Turnaround Time"),
    TOPTHREEINSTURCTORS("Top Three Instructors"),
    TOPTHREESECTIONS("Top Three Sections");

    public String getStatisticName() {
        return statisticName;
    }

    private String statisticName;

    StatisticType(String statisticName) {
        this.statisticName = statisticName;
    }
}
