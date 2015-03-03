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
        int pshrRelayTotalMatches = 0;

        for(SyncUser syncUser: users)
        {
            String relayUsername = syncUser.getRelayUsername();
            String pshrEmail = syncUser.getPshrEmail();

            if(relayUsername == null)
            {
                relayMembers.addNonMember();
            }
            else
            {
                relayMembers.addMember();

                if(!relayResearchDao.isCruDomain(relayUsername))
                {
                    syncUser.setCruDomainRelayUsername("no");
                    cruDomains.addNonMember();
                }
                else
                {
                    cruDomains.addMember();
                    syncUser.setCruDomainRelayUsername("yes");
                }
            }

            if(!relayResearchDao.isInGoogle(relayUsername))
            {
                googleMembers.addNonMember();
            }
            else
            {
                googleMembers.addMember();
                syncUser.setInGoogle(true);
            }

            if(pshrEmail != null && relayUsername != null)
            {
                if (pshrEmail.equalsIgnoreCase(relayUsername))
                {
                    pshrRelayTotalMatches++;
                    syncUser.setEmailsMatch("yes");
                }
                else
                {
                    if (relayResearchDao.isCruDomain(pshrEmail) && relayResearchDao.isCruDomain(relayUsername))
                    {
                        syncUser.setEmailsMatch("no, but both are Cru owned");
                    }
                }
            }
        }


        FileWriter writer = new FileWriter(fileName);

        List<String> headers = Lists.newArrayList();
        headers.add("First name");
        headers.add("Last name");
        headers.add("Employee Number (in PSHR: " + users.size() + ")");
        headers.add("PSHR email");
        headers.add("Relay username (in Relay: " + relayMembers.getMembers() + ")");
        headers.add("Cru domain (total 'yes': " + cruDomains.getMembers() + ")");
        headers.add("In Google (total 'yes': " + googleMembers.getMembers() + ")");
        headers.add("Relay - PSHR match (total 'yes': " + pshrRelayTotalMatches + ")");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');

        for(SyncUser user: users)
        {
            writeValue(writer, user.getFirstName());
            writeValue(writer, user.getLastName());
            writeValue(writer, user.getEmployeeId());
            writeValue(writer, user.getPshrEmail());
            writeValue(writer, user.getRelayUsername());
            writeValue(writer, user.isCruDomainRelayUsername());
            writeValue(writer, user.isInGoogle());
            writeValue(writer, user.getEmailsMatch());

            writer.append('\n');
        }

        writer.flush();
        writer.close();

        System.out.println("Relay: " + relayMembers.toString());
        System.out.println("Cru domains: " + cruDomains.toString());
        System.out.println("Google: " + googleMembers.toString());
        System.out.println("Users with matching PSHR email and Relay username: " + pshrRelayTotalMatches);
    }

    private void writeValue(FileWriter writer, String value) throws IOException
    {
        if(value != null)
        {
            if(value.contains(","))
            {
                value = "\"" + value + "\"";
            }
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
        String value;
        if(condition)
        {
            value = "yes";
        }
        else
        {
            value = "no";
        }

        writer.append(value);
        writer.append(',');
    }

}
