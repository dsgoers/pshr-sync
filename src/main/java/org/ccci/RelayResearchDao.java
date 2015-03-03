package org.ccci;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.ccci.idm.dao.LdapEntryDaoImpl;
import org.ccci.idm.dao.exception.EntryLookupException;
import org.ccci.idm.dao.exception.EntryLookupMoreThanOneResultException;
import org.ccci.idm.dao.exception.EntryLookupNoResultsException;
import org.ccci.idm.dao.pshr.PSHRStaff;
import org.ccci.idm.ldap.Ldap;
import org.ccci.idm.ldap.attributes.LdapAttributes;
import org.ccci.idm.ldap.attributes.LdapAttributesActiveDirectory;
import org.ccci.util.properties.PropertiesWithFallback;

import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

/**
 * Created by dsgoers on 2/6/15.
 */
public class RelayResearchDao
{
    private final LdapAttributes ldapAttributes = new LdapAttributesActiveDirectory();

    private final String ldapPropertiesFileLocation = "/apps/apps-config/adldsproperties.properties";
    private final String emailDomainsFileLocation = "/apps/apps-config/emailDomains.properties";

    private File emailDomainsFile;

    private LdapEntryDaoImpl ldapEntryDao;

    private Set<String> returnAttributes;

    public RelayResearchDao() throws Exception
    {
        Properties properties = new PropertiesWithFallback(false, ldapPropertiesFileLocation);

        ldapEntryDao = new LdapEntryDaoImpl(new Ldap(properties.getProperty("ldapUrl"),
                properties.getProperty("ldapUsername"), properties.getProperty("ldapPassword")),
                properties.getProperty("ldapBaseDn"));

        emailDomainsFile = new File(emailDomainsFileLocation);

        returnAttributes = getReturnAttributes();
    }

    public Set<SyncUser> getRelayData(Set<PSHRStaff> pshrUsers) throws Exception
    {
        Set<SyncUser> syncUsers = Sets.newHashSet();

        for(PSHRStaff pshrUser: pshrUsers)
        {
            try
            {
                Map<String, String> searchAttributes = Maps.newHashMap();
                searchAttributes.put(ldapAttributes.employeeNumber, pshrUser.getEmployeeId());

                Multimap<String, String> userAttributes = ldapEntryDao.getLdapEntry(searchAttributes,
                        returnAttributes);

                SyncUser syncUser = new SyncUser(userAttributes);
                syncUser.setPshrEmail(pshrUser.getEmail());
                syncUsers.add(syncUser);
            }
            catch (EntryLookupNoResultsException e)
            {
                syncUsers.add(new SyncUser(pshrUser));
            }
            catch(EntryLookupMoreThanOneResultException e)
            {
                System.out.println("User has more than one relay account: " + pshrUser.toString());
            }
            catch(NoSuchElementException e)
            {
                e.printStackTrace();
            }
        }

        return syncUsers;
    }

    public boolean isInGoogle(String relayUsername) throws NamingException
    {
        if(relayUsername == null)
        {
            return false;
        }

        try
        {
            Map<String, String> searchAttributes = Maps.newHashMap();
            searchAttributes.put(ldapAttributes.username, relayUsername);

            Multimap<String, String> userAttributes = ldapEntryDao.getLdapEntry(searchAttributes,
                    returnAttributes);
            Collection<String> memberOfValues = userAttributes.get(ldapAttributes.memberOf);

            for(String value: memberOfValues)
            {
                if(value.contains("CN=GoogleApps"))
                {
                    return true;
                }
            }
            return false;
        }
        catch (EntryLookupException e)
        {
            //System.out.println(e.getMessage());
        }
        catch(NoSuchElementException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCruDomain(String email) throws IOException
    {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        List<String> cruDomains = getCruDomains();

        for(String cruDomain: cruDomains)
        {
            if(cruDomain.toLowerCase().equals(domain))
            {
                return true;
            }
        }

        return false;
    }

    private List<String> getCruDomains() throws IOException
    {
        return Files.readLines(emailDomainsFile, Charsets.UTF_8);
    }

    private Set<String> getReturnAttributes()
    {
        Set<String> returnAttributes = Sets.newHashSet();

        returnAttributes.add(ldapAttributes.employeeNumber);
        returnAttributes.add(ldapAttributes.givenname);
        returnAttributes.add(ldapAttributes.surname);
        returnAttributes.add(ldapAttributes.username);
        returnAttributes.add(ldapAttributes.designationId);
        returnAttributes.add(ldapAttributes.ministryCode);
        returnAttributes.add(ldapAttributes.departmentNumber);
        returnAttributes.add(ldapAttributes.employeeStatus);
        returnAttributes.add(ldapAttributes.memberOf);

        return returnAttributes;
    }


}
