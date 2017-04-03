package org.sakaiproject.ddo.tool.rest;

import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.ddo.logic.ProjectLogic;
import org.sakaiproject.ddo.logic.SakaiProxy;
import org.sakaiproject.ddo.model.NumStatistics;
import org.sakaiproject.ddo.model.NumberStat;
import org.sakaiproject.ddo.utils.DDOConstants;
import org.sakaiproject.ddo.utils.StatisticType;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.*;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * Created by David P. Bauer [dbauer1@udayton.edu] on 3/17/17.
 *
 */
public class DDOEntityProvider extends AbstractEntityProvider implements AutoRegisterEntityProvider,
        Outputable, Describeable, ActionsExecutable {

    public final static String ENTITY_PREFIX = "ddo-statistics";

    @Override
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable#
     * getHandledOutputFormats()
     */
    @Override
    public String[] getHandledOutputFormats() {
        return new String[] { Formats.JSON };
    }

    private boolean isUserDDOAdmin(final String userId) {
        try {
            final AuthzGroup ddoRealm = this.authzGroupService.getAuthzGroup(DDOConstants.DDO_REALM);
            return ddoRealm != null && StringUtils.equals(ddoRealm.getMaintainRole(), this.authzGroupService.getUserRole(userId, DDOConstants.DDO_REALM));
        } catch (GroupNotDefinedException e) {
            throw new RuntimeException("Unable to check if user is DDO Admin. Verify realm is defined correctly.");
        }
    }

    private boolean checkUserStatus(String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new SecurityException("Only logged in users can access DDO statistics.");
        }
        if (this.securityService.isSuperUser() || this.isUserDDOAdmin(userId)) {
            return true;
        } else {
            throw new SecurityException("DDO Statistics are only available to DDO Admins.");
        }
    }

    private Date getValidStartDateFromParams(Map<String, Object> params){
        final String startDateString = (String) params.get(DDOConstants.PARAM_START_DATE);
        if (StringUtils.isBlank(startDateString)){
            throw new IllegalArgumentException(
                    String.format(
                            "Start Date parameter ("+ DDOConstants.PARAM_START_DATE +
                                    ") must be set in order to get the number of submissions for range, via the URL /%s/numberOfSubmissions",
                                    ENTITY_PREFIX));
        }else{
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            try {
                TemporalAccessor startDateAccessor = timeFormatter.parse(startDateString);
                Date startDateConverted = Date.from(Instant.from(startDateAccessor));
                return startDateConverted;
            } catch (DateTimeParseException e){
                throw new IllegalArgumentException(
                        String.format(
                                "Start Date parameter ("+ DDOConstants.PARAM_START_DATE +
                                        ") is incorrectly formatted"));
            }
        }
    }

    private Date getValidEndDateFromParams(Map<String, Object> params){
        final String endDateString = (String) params.get(DDOConstants.PARAM_END_DATE);
        if (StringUtils.isBlank(endDateString)) {
            return new Date();
        }else{
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            try {
                TemporalAccessor endDateAccessor = timeFormatter.parse(endDateString);
                Date endDateConverted = Date.from(Instant.from(endDateAccessor));
                return endDateConverted;
            } catch (DateTimeParseException e){
                throw new IllegalArgumentException(
                        String.format(
                                "End Date parameter ("+ DDOConstants.PARAM_END_DATE +
                                        ") is incorrectly formatted"));
            }
        }
    }

    private void dateChronologicalChecker(Date startDateString, Date endDateString){//Makes sure the dates are in the right order
        if (startDateString.after(endDateString)){
            throw new IllegalArgumentException(
                    String.format(
                            "Start Date parameter ("+ DDOConstants.PARAM_START_DATE +
                                    ") must be set chronologiacally before the End Date parameter (" + DDOConstants.PARAM_END_DATE +
                                    ") in order to get the number of submissions for range, via the URL /%s/numberOfSubmissions",
                            ENTITY_PREFIX));
        }
    }

    @EntityCustomAction(action = "numberOfSubmissions", viewKey = EntityView.VIEW_LIST)
    public String getNumberOfSubmissions(EntityView view, Map<String, Object> params) {
        final String statusString = (String) params.get(DDOConstants.PARAM_STATUS);
        final String userId = sakaiProxy.getCurrentUserId();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);

        final int numberOfSubmissions = this.projectLogic.getNumberofSubmissionsLogic(startDateConverted, endDateConverted, statusString);
        final String numberOfSubmissionsString = String.valueOf(numberOfSubmissions);
        return numberOfSubmissionsString;
    }

    @EntityCustomAction(action = "numberOfUniqueUsers", viewKey = EntityView.VIEW_LIST)
    public String getNumberOfUniqueUsers(EntityView view, Map<String, Object> params) {
        final String userId = sakaiProxy.getCurrentUserId();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);

        final int numberOfUniqueUsers = this.projectLogic.getNumberOfUniqueUsersLogic(startDateConverted, endDateConverted);
        final String numberOfUniqueUsersString = String.valueOf(numberOfUniqueUsers);
        return numberOfUniqueUsersString;
    }

    @EntityCustomAction(action = "numberOfRepeatUsers", viewKey = EntityView.VIEW_LIST)
    public String getNumberOfRepeatUsers(EntityView view, Map<String, Object> params) {
        final String userId = sakaiProxy.getCurrentUserId();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);

        final int numberOfRepeatUsers = this.projectLogic.getNumberOfRepeatUsersLogic(startDateConverted, endDateConverted);
        final String numberOfRepeatUsersString = String.valueOf(numberOfRepeatUsers);
        return numberOfRepeatUsersString;
    }

    @EntityCustomAction(action = "numberOfConsultants", viewKey = EntityView.VIEW_LIST)
    public String getNumberOfConsultants(EntityView view, Map<String, Object> params) {
        final String userId = sakaiProxy.getCurrentUserId();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);
        final int numberOfConsultants = this.projectLogic.getNumberOfConsultantsLogic(startDateConverted, endDateConverted);
        final String numberOfConsultantsString = String.valueOf(numberOfConsultants);
        return numberOfConsultantsString;
    }

    @EntityCustomAction(action = "numberOfReviewsPerConsultant", viewKey = EntityView.VIEW_LIST)
    public NumberStat getNumberOfReviewsPerConsultant(EntityView view, Map<String, Object> params) {
        final String userId = sakaiProxy.getCurrentUserId();
        NumberStat statEntity = new NumberStat();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);

        final List<NumStatistics> numberOfReviewsPerConsultant = this.projectLogic.numberOfReviewsPerConsultantLogic(startDateConverted, endDateConverted);
        statEntity.setStatsType(StatisticType.REVIEWERS);
        statEntity.setStartDate(startDateConverted);
        statEntity.setEndDate(endDateConverted);
        statEntity.setNumStatisticsList(numberOfReviewsPerConsultant);
        return statEntity;
    }
    
    @EntityCustomAction(action = "TopThreeInstructors", viewKey = EntityView.VIEW_LIST)
    public NumberStat getTopThreeInstructors(EntityView view, Map<String, Object> params) {
        final String userId = sakaiProxy.getCurrentUserId();
        NumberStat statEntity = new NumberStat();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);

        final List<NumStatistics> topThreeInstructorsStatsList = this.projectLogic.topThreeInstructorsStatsLogic(startDateConverted, endDateConverted);
        statEntity.setStatsType(StatisticType.TOPINSTRUCTORS);
        statEntity.setStartDate(startDateConverted);
        statEntity.setEndDate(endDateConverted);
        statEntity.setNumStatisticsList(topThreeInstructorsStatsList);
        return statEntity;
    }

    @EntityCustomAction(action = "TopThreeSections", viewKey = EntityView.VIEW_LIST)
    public NumberStat getTopThreeSections(EntityView view, Map<String, Object> params) {
        final String userId = sakaiProxy.getCurrentUserId();
        NumberStat statEntity = new NumberStat();

        checkUserStatus(userId);
        Date startDateConverted = getValidStartDateFromParams(params);
        Date endDateConverted = getValidEndDateFromParams(params);
        dateChronologicalChecker(startDateConverted, endDateConverted);

        final List<NumStatistics> topThreeSectionsStatsList = this.projectLogic.topThreeSectionsStatsLogic(startDateConverted, endDateConverted);
        statEntity.setStatsType(StatisticType.TOPSECTIONS);
        statEntity.setStartDate(startDateConverted);
        statEntity.setEndDate(endDateConverted);
        statEntity.setNumStatisticsList(topThreeSectionsStatsList);
        return statEntity;
    }

    @Setter
    private AuthzGroupService authzGroupService;

    @Setter
    private SecurityService securityService;

    @Setter
    private ProjectLogic projectLogic;

    @Setter
    private SakaiProxy sakaiProxy;
}
