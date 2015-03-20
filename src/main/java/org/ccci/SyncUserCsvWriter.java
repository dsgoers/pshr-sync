package org.ccci;

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
            userData.setScenario(Scenario.i);
        }
        else if(!emailsMatch && relayDomain == Status.approved && pshrDomain == Status.approved)
        {
            userData.setScenario(Scenario.ii);
        }
        else if(!emailsMatch && relayDomain == Status.approved && pshrDomain != Status.approved)
        {
            userData.setScenario(Scenario.iii);
        }
        else if(!emailsMatch && relayDomain == Status.nonapproved && pshrDomain == Status.approved)
        {
            userData.setScenario(Scenario.iv);
        }
        else if(!emailsMatch && (relayDomain == Status.nonCru || relayDomain == Status.none) && pshrDomain == Status
                .approved)
        {
            userData.setScenario(Scenario.v);
        }
        else if(!emailsMatch && relayDomain == Status.nonapproved && pshrDomain != Status.approved)
        {
            userData.setScenario(Scenario.vi);
        }
        else if(!emailsMatch && (relayDomain == Status.nonCru || relayDomain == Status.none) && pshrDomain != Status.approved)
        {
            userData.setScenario(Scenario.vii);
        }
        else if(emailsMatch && relayDomain == Status.nonapproved && pshrDomain != Status.approved)
        {
            userData.setScenario(Scenario.viii);
        }
        else if(emailsMatch && (relayDomain == Status.nonCru || relayDomain == Status.none) && pshrDomain != Status.approved)
        {
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
