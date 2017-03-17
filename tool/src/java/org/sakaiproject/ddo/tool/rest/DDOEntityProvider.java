package org.sakaiproject.ddo.tool.rest;

import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.ddo.utils.DDOConstants;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.*;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;

/**
 * Created by David P. Bauer [dbauer1@udayton.edu] on 3/17/17.
 *
 */
public class DDOEntityProvider extends AbstractEntityProvider implements AutoRegisterEntityProvider,
        Outputable, Describeable, ActionsExecutable {

    public final static String ENTITY_PREFIX = "ddo-statistics";

    @Setter
    private AuthzGroupService authzGroupService;

    @Setter
    private SecurityService securityService;

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

    @EntityCustomAction(action = "numberOfSubmissions", viewKey = EntityView.VIEW_LIST)
    public String getNumberOfSubmissions(EntityView view) {
        final String startDateString = view.getPathSegment(2);
        final String endDateString = view.getPathSegment(3);

        if (StringUtils.isBlank(startDateString) || StringUtils.isBlank(endDateString)) {
            // TODO: Check for valid date format.
            throw new IllegalArgumentException(
                    String.format(
                            "Start and End Date must be set in order to get the number of submissions for range, via the URL /%s/numberOfSubmissions/{startDate}/{endDate}",
                            ENTITY_PREFIX));
        }

        final String userId = developerHelperService.getCurrentUserId();

        if (StringUtils.isBlank(userId)) {
            throw new SecurityException("Only logged in users can access DDO statistics.");
        }

        if (securityService.isSuperUser() || this.isUserDDOAdmin(userId)) {

            // TODO: This is where you would get the statistic.
            return null;

        } else {
            throw new SecurityException("DDO Statistics are only available to DDO Admins.");
        }
    }


    private boolean isUserDDOAdmin(final String userId) {
        try {
            final AuthzGroup ddoRealm = this.authzGroupService.getAuthzGroup(DDOConstants.DDO_REALM);
            return ddoRealm != null && StringUtils.equals(ddoRealm.getMaintainRole(), this.authzGroupService.getUserRole(userId, DDOConstants.DDO_REALM));
        } catch (GroupNotDefinedException e) {
            throw new RuntimeException("Unable to check if user is DDO Admin. Verify realm is defined correctly.");
        }
    }
}
