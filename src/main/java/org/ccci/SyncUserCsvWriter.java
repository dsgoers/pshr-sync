package org.ccci;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.naming.NamingException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.ccci.SyncUserData.Status;
import org.ccci.SyncUserData.Scenario;


/**
 * Created by dsgoers on 2/17/15.
 */
public class SyncUserCsvWriter
{
    int i;
    int ii;
    int iii;
    int iv;
    int v;
    int vi;
    int vii;
    int viii;
    int ix;

    public void writeCruGoogleInfoToCsv(String fileName, Set<SyncUser> users, RelayResearchDao relayResearchDao) throws IOException, NamingException
    {
        Set<SyncUserData> userDatas = Sets.newHashSet();

        for(SyncUser syncUser: users)
        {
            SyncUserData userData = new SyncUserData(syncUser);

            String relayUsername = userData.getRelayUsername();
            String pshrEmail = userData.getPshrEmail();

            if(relayUsername != null && pshrEmail != null && relayUsername.equalsIgnoreCase(pshrEmail))
            {
                userData.setEmailsMatch(true);
            }

            if(pshrEmail == null || pshrEmail.trim().equals(""))
            {
                userData.setPshrDomain(Status.none);
            }
            else
            {
                userData.setPshrDomain(relayResearchDao.isCruDomain(pshrEmail));
            }

            if(relayUsername == null || relayUsername.trim().equals(""))
            {
                userData.setRelayDomain(Status.none);
            }
            else
            {
                userData.setRelayDomain(relayResearchDao.isCruDomain(relayUsername));
            }

            setScenario(userData);

            userDatas.add(userData);
        }

        int inGoogle = 0;
        int notInGoogle = 0;
        int matches = 0;
        int nonMatches = 0;
        int notInRelay = 0;

        int pshrApproved = 0;
        int pshrNonApproved = 0;
        int pshrNonCru = 0;
        int noPshrEmail = 0;

        int relayApproved = 0;
        int relayNonApproved = 0;
        int relayNonCru = 0;
        int noRelayEmail = 0;

        for(SyncUserData user: userDatas)
        {
            if(user.isInGoogle())
                inGoogle++;
            else
                notInGoogle++;

            if(user.getRelayUsername() != null && user.getPshrEmail() != null
                    && user.getRelayUsername().equalsIgnoreCase(user.getPshrEmail()))
            {
                matches++;
            }
            else
                nonMatches++;

            if(Strings.isNullOrEmpty(user.getRelayUsername()))
                notInRelay++;

            if(user.getPshrDomain().equals(Status.approved))
                pshrApproved++;
            if(user.getPshrDomain().equals(Status.nonapproved))
                pshrNonApproved++;
            if(user.getPshrDomain().equals(Status.nonCru))
                pshrNonCru++;
            if(user.getPshrDomain().equals(Status.none))
                noPshrEmail++;

            if(user.getRelayDomain().equals(Status.approved))
                relayApproved++;
            if(user.getRelayDomain().equals(Status.nonapproved))
                relayNonApproved++;
            if(user.getRelayDomain().equals(Status.nonCru))
                relayNonCru++;
            if(user.getRelayDomain().equals(Status.none))
                noRelayEmail++;

        }


        FileWriter writer = new FileWriter(fileName);

        List<String> headers = Lists.newArrayList();
        headers.add("First name");
        headers.add("Last name");
        headers.add("Employee Number");
        headers.add("PSHR email");
        headers.add("PSHR domain");
        headers.add("Relay username");
        headers.add("Relay domain");
        headers.add("In Google");
        headers.add("Relay - PSHR match");
        headers.add("Scenario");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');

        writeValue(writer, null);
        writeValue(writer, null);
        writeValue(writer, "Total in PSHR: " + userDatas.size());
        writeValue(writer, "No PSHR email: " + noPshrEmail);
        writeValue(writer, "Approved: " + pshrApproved + ".  Non-approved: " + pshrNonApproved + ".  Non-Cru: " + pshrNonCru +
                ".  None: " + noPshrEmail);
        writeValue(writer, "Not in Relay: " + notInRelay);
        writeValue(writer, "Approved: " + relayApproved + ".  Non-approved: " + relayNonApproved + ".  Non-Cru: " +
                relayNonCru + ".  None: " + noRelayEmail);
        writeValue(writer, "In Google: " + inGoogle + "  Not in Google: " + notInGoogle);
        writeValue(writer, "Matches: " + matches + ".  Non-matches: " + nonMatches);
        writeValue(writer, "i: " + i + ".  ii: " + ii + ".  iii: " + iii + ".  iv: " + iv + ".  v: " + v +  ".  vi: " +
                vi + ".  vii: " + vii + ".  viii: " + viii + ".  ix: " + ix);
        writer.append('\n');

        for(SyncUserData user: userDatas)
        {
            writeValue(writer, user.getFirstName());
            writeValue(writer, user.getLastName());
            writeValue(writer, user.getEmployeeId());
            writeValue(writer, user.getPshrEmail());
            writeValue(writer, user.getPshrDomain().name());
            writeValue(writer, user.getRelayUsername());
            writeValue(writer, user.getRelayDomain().name());
            writeValue(writer, user.isInGoogle());
            writeValue(writer, user.emailsMatch());
            writeValue(writer, user.getScenario().name());

            writer.append('\n');
        }

        writer.flush();
        writer.close();
    }

    private void setScenario(SyncUserData userData)
    {
        boolean emailsMatch = userData.emailsMatch();
        Status pshrDomain = userData.getPshrDomain();
        Status relayDomain = userData.getRelayDomain();

        if(emailsMatch && relayDomain == Status.approved && pshrDomain == Status.approved)
        {
            i++;
            userData.setScenario(Scenario.i);
        }
        else if(!emailsMatch && relayDomain == Status.approved && pshrDomain == Status.approved)
        {
            ii++;
            userData.setScenario(Scenario.ii);
        }
        else if(!emailsMatch && relayDomain == Status.approved && pshrDomain != Status.approved)
        {
            iii++;
            userData.setScenario(Scenario.iii);
        }
        else if(!emailsMatch && relayDomain == Status.nonapproved && pshrDomain == Status.approved)
        {
            iv++;
            userData.setScenario(Scenario.iv);
        }
        else if(!emailsMatch && (relayDomain == Status.nonCru || relayDomain == Status.none) && pshrDomain == Status
                .approved)
        {
            v++;
            userData.setScenario(Scenario.v);
        }
        else if(!emailsMatch && relayDomain == Status.nonapproved && pshrDomain != Status.approved)
        {
            vi++;
            userData.setScenario(Scenario.vi);
        }
        else if(!emailsMatch && (relayDomain == Status.nonCru || relayDomain == Status.none) && pshrDomain != Status.approved)
        {
            vii++;
            userData.setScenario(Scenario.vii);
        }
        else if(emailsMatch && relayDomain == Status.nonapproved && pshrDomain != Status.approved)
        {
            viii++;
            userData.setScenario(Scenario.viii);
        }
        else if(emailsMatch && (relayDomain == Status.nonCru || relayDomain == Status.none) && pshrDomain != Status.approved)
        {
            ix++;
            userData.setScenario(Scenario.ix);
        }
    }


    private void writeValue(FileWriter writer, String value) throws IOException
    {
        if(value != null)
        {
            value = value.contains(",") ? "\"" + value + "\"" : value;

            value = value.trim();
        }
        else
        {
            value = "";
        }

        writer.append(value);
        writer.append(',');
    }

    private void writeValue(FileWriter writer, boolean condition) throws IOException
    {
        writer.append(condition ? "yes" : "no");
        writer.append(',');
    }

}
