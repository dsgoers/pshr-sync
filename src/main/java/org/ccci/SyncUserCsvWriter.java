package org.ccci;

import com.google.common.collect.Lists;

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
        UserMembershipInfo relayMembers = new UserMembershipInfo();
        UserMembershipInfo cruDomains = new UserMembershipInfo();
        UserMembershipInfo googleMembers = new UserMembershipInfo();
        int nonMatches = 0;

        FileWriter writer = new FileWriter(fileName);

        List<String> headers = Lists.newArrayList();
        headers.add("First name");
        headers.add("Last name");
        headers.add("Employee Number");
        headers.add("PSHR email");
        headers.add("Relay username");
        headers.add("Cru domain");
        headers.add("In Google");
        headers.add("Relay - PSHR match");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');


        for(SyncUser user: users)
        {
            boolean relayPshrMatches = false;

            writeValue(writer, user.getFirstName());
            writeValue(writer, user.getLastName());
            writeValue(writer, user.getEmployeeId());
            writeValue(writer, user.getPshrEmail());

            String relayUsername = user.getRelayUsername();
            if(relayUsername == null)
            {
                relayMembers.addNonMember();

                writeValue(writer, "");
                writeValue(writer, "");
                writeValue(writer, "");
            }
            else
            {
                if (relayUsername.equalsIgnoreCase(user.getPshrEmail()))
                {
                    relayPshrMatches = true;
                }

                relayMembers.addMember();
                writeValue(writer, user.getRelayUsername());

                boolean isCruDomain = relayResearchDao.isCruDomain(relayUsername.substring(relayUsername.indexOf("@") +
                        1));
                writeValue(writer, Boolean.toString(isCruDomain));

                boolean isInGoogle = relayResearchDao.isInGoogle(user);
                writeValue(writer, Boolean.toString(isInGoogle));

                if (!isCruDomain)
                {
                    cruDomains.addNonMember();
                }
                else
                {
                    cruDomains.addMember();

                    if (isInGoogle)
                    {
                        googleMembers.addMember();
                    }
                    else
                    {
                        googleMembers.addNonMember();
                    }
                }
            }

            if(!relayPshrMatches)
            {
                nonMatches++;
            }

            writeValue(writer, Boolean.toString(relayPshrMatches));
            writer.append('\n');
        }

        writer.flush();
        writer.close();

        System.out.println("Relay: " + relayMembers.toString());
        System.out.println("Cru domains: " + cruDomains.toString());
        System.out.println("Google: " + googleMembers.toString());
        System.out.println("Users without matching PSHR email and Relay username: " +
                nonMatches);
    }

    private void writeValue(FileWriter writer, String value) throws IOException
    {
        if(value.contains(","))
        {
            value = "\"" + value + "\"";
        }

        writer.append(value.trim().toLowerCase());
        writer.append(',');
    }

}
