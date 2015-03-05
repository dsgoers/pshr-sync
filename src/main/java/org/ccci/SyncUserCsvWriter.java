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
        int pshrRelayBothCruOwned = 0;
        int aliasCount = 0;

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
                    syncUser.setIsCruDomainRelayUsername("no");
                    cruDomains.addNonMember();
                }
                else
                {
                    cruDomains.addMember();
                    syncUser.setIsCruDomainRelayUsername("yes");
                }
            }

            if(syncUser.isInGoogle())
            {
                googleMembers.addMember();
            }
            else
            {
                googleMembers.addNonMember();
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
                    if(syncUser.isAlias())
                    {
                        aliasCount++;
                        syncUser.setEmailsMatch("no, but PSHR email is an alias");
                    }
                    else
                    {
                        if (relayResearchDao.isCruDomain(pshrEmail) && relayResearchDao.isCruDomain(relayUsername))
                        {
                            pshrRelayBothCruOwned++;
                            syncUser.setEmailsMatch("no, but both are Cru owned");
                        }
                    }
                }
            }
        }


        FileWriter writer = new FileWriter(fileName);

        int nonCruMismatches = users.size() - pshrRelayTotalMatches - pshrRelayBothCruOwned - aliasCount;

        List<String> headers = Lists.newArrayList();
        headers.add("First name");
        headers.add("Last name");
        headers.add("Employee Number (in PSHR: " + users.size() + ")");
        headers.add("PSHR email");
        headers.add("Relay username (in Relay: " + relayMembers.getMembers() + ")");
        headers.add("Cru domain (total 'yes': " + cruDomains.getMembers() + ")");
        headers.add("In Google (total 'yes': " + googleMembers.getMembers() + ")");
        headers.add("Relay - PSHR match (total 'yes': " + pshrRelayTotalMatches + ", total 'no but Cru owned': " +
                pshrRelayBothCruOwned + ", total 'no but PSHR is alias': " + aliasCount + ", total 'no': " +
                nonCruMismatches + ")");

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
            writeValue(writer, user.getIsCruDomainRelayUsername());
            writeValue(writer, user.isInGoogle());
            writeValue(writer, user.getEmailsMatch());

            writer.append('\n');
        }

        writer.flush();
        writer.close();

        System.out.println("Relay: " + relayMembers.toString());
        System.out.println("Cru domains: " + cruDomains.toString());
        System.out.println("Google: " + googleMembers.toString());
        System.out.println("Users with matching PSHR email and Relay username: " + pshrRelayTotalMatches
                + ", not matching but Cru domain: " + pshrRelayBothCruOwned + ", with an alias: " + aliasCount
                + ", not matching and not both Cru owned: " + nonCruMismatches);
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
