package org.ccci;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.naming.NamingException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

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

            if(pshrEmail == null)
            {
                userData.setPshrDomain(SyncUserData.Status.none);
            }
            else
            {
                userData.setPshrDomain(relayResearchDao.isCruDomain(pshrEmail));
            }

            if(relayUsername == null)
            {
                userData.setRelayDomain(SyncUserData.Status.none);
            }
            else
            {
                userData.setRelayDomain(relayResearchDao.isCruDomain(relayUsername));
            }

            userDatas.add(userData);
        }


        FileWriter writer = new FileWriter(fileName);

        List<String> headers = Lists.newArrayList();
        headers.add("First name");
        headers.add("Last name");
        headers.add("Employee Number)");
        headers.add("PSHR email");
        headers.add("PSHR domain");
        headers.add("Relay username");
        headers.add("Relay domain");
        headers.add("In Google");
        headers.add("Relay - PSHR match");

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

            writer.append('\n');
        }

        writer.flush();
        writer.close();
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
