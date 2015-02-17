package org.ccci;

import org.ccci.idm.dao.pshr.PSHRStaff;

import java.util.Set;

/**
 * Created by dsgoers on 2/10/15.
 */
public class Sync
{
    public static void main(String[] args) throws Exception
    {
        String directory = "/Users/dsgoers/projects/pshr-csv/";

        PSHRResearchDao pshrResearchDao = new PSHRResearchDao();
        RelayResearchDao relayResearchDao = new RelayResearchDao();
        IdentityUserCsvWriter writer = new IdentityUserCsvWriter();

        Set<PSHRStaff> staffs = pshrResearchDao.getSomeUsStaff();


        UserMembershipInfo info = relayResearchDao.getRelayMembershipInfo(staffs);
        writer.writeIdentityUsersToCsv(directory + "relay users", info.getMembers());
        writer.writeIdentityUsersToCsv(directory + "non relay users", info.getUsersWithoutMembership());

        info = relayResearchDao.getCruDomainEmailAddressInfo(staffs);
        writer.writeIdentityUsersToCsv(directory + "cru domain users", info.getMembers());
        writer.writeIdentityUsersToCsv(directory + "non cru domain users", info.getUsersWithoutMembership());

        info = relayResearchDao.getGoogleMembershipInfo(staffs);
        writer.writeIdentityUsersToCsv(directory + "google users", info.getMembers());
        writer.writeIdentityUsersToCsv(directory + "non google users", info.getUsersWithoutMembership());


        System.out.println("Done.");
    }
}
