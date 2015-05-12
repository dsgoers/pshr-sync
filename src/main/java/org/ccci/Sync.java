package org.ccci;

import org.ccci.idm.dao.pshr.PSHRStaff;
import org.ccci.util.properties.PropertiesWithFallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * Created by dsgoers on 2/10/15.
 */
public class Sync
{
    public static void main(String[] args) throws Exception
    {
        String pshrProperties = "/apps/apps-config/pshrSync.properties";
        Properties properties = new PropertiesWithFallback(false, pshrProperties);

        PSHRResearchDao pshrResearchDao = new PSHRResearchDao();
        RelayResearchDao relayResearchDao = new RelayResearchDao();
        SyncUserCsvWriter writer = new SyncUserCsvWriter();

        Set<PSHRStaff> pshrStaffs = pshrResearchDao.getAllNonTerminatedUsStaff();
        System.out.println("query found " + pshrStaffs.size() + " users.");

        Set<SyncUser> syncUsers = relayResearchDao.getRelayData(pshrStaffs);
        System.out.println("minus duplicates: " + syncUsers.size() + " users.");

        writer.writeCruGoogleInfoToCsv(properties.getProperty("csvLocation") + "/" + "PSHR Relay data " +
                        new SimpleDateFormat("yyyy_MM_dd HH_mm_ss").format(new Date()) + ".csv", syncUsers,
                relayResearchDao);


        System.out.println("\nDone.");
    }
}
