package org.ccci;

import com.google.common.collect.Sets;
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
        SyncUserCsvWriter writer = new SyncUserCsvWriter();

        Set<PSHRStaff> pshrStaffs = pshrResearchDao.getAllNonTerminatedUsStaff();

        Set<SyncUser> identityUserStaffs = Sets.newHashSet();

        for(PSHRStaff staff: pshrStaffs)
        {
            identityUserStaffs.add(new SyncUser(staff));
        }

        System.out.println("all users in peoplesoft: " + identityUserStaffs.size());
        writer.writePshrUsersToCsv(directory + "all pshr users.csv", identityUserStaffs);

        UserMembershipInfo info = relayResearchDao.getRelayMembershipInfo(pshrStaffs);
        System.out.println("relay: " + info.getMembers().size() + ", non relay: " + info.getUsersWithoutMembership
                ().size() + ", total: " + info.getTotalUsers());
        writer.writeSyncUsersToCsv(directory + "relay users.csv", info.getMembers());
        writer.writePshrUsersToCsv(directory + "non relay users.csv", info.getUsersWithoutMembership());



        info = relayResearchDao.getCruDomainEmailAddressInfo(info.getMembers());
        System.out.println("cru domain: " + info.getMembers().size() + ", non cru domain: " +
                info.getUsersWithoutMembership().size() + ", total: " + info.getTotalUsers());
        writer.writeSyncUsersToCsv(directory + "cru domain users.csv", info.getMembers());
        writer.writeSyncUsersToCsv(directory + "non cru domain users.csv", info
                .getUsersWithoutMembership());


        info = relayResearchDao.getGoogleMembershipInfo(info.getMembers());
        System.out.println("google: " + info.getMembers().size() + ", non google: " +
                info.getUsersWithoutMembership().size() + ", total: " + info.getTotalUsers());
        writer.writeSyncUsersToCsv(directory + "google users.csv", info.getMembers());
        writer.writeSyncUsersToCsv(directory + "non google users.csv", info.getUsersWithoutMembership());


        info = relayResearchDao.getPeopleSoftPrimaryEmailInfo(info.getMembers(), pshrStaffs);
        System.out.println("pshr matching: " + info.getMembers().size() + ", non pshr matching: " +
                info.getUsersWithoutMembership().size() + ", total: " + info.getTotalUsers());
        writer.writeSyncUsersToCsv(directory + "pshr matching users.csv", info.getMembers());
        writer.writeSyncUsersToCsv(directory + "non pshr matching users.csv", info
                .getUsersWithoutMembership());


        info = relayResearchDao.splitNonMatchingUsers(info.getUsersWithoutMembership(), pshrStaffs);
        System.out.println("non matching with Cru domain: " + info.getMembers().size() + ", non matching without Cru " +
                "domain: " + info.getUsersWithoutMembership().size() + ", total: " + info.getTotalUsers());
        writer.writeSyncUsersToCsv(directory + "pshr non matching users with cru domain.csv", info
                .getMembers());
        writer.writeSyncUsersToCsv(directory + "pshr non matching users without cru domain.csv", info
                .getUsersWithoutMembership());



        System.out.println("\nDone.");
    }
}
