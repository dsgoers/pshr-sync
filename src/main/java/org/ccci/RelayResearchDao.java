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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.ccci.SyncUserData.Status;

/**
 * Created by dsgoers on 2/6/15.
 */
public class RelayResearchDao
{
    private final LdapAttributes ldapAttributes = new LdapAttributesActiveDirectory();

    private final String ldapPropertiesFileLocation = "/apps/apps-config/adldsproperties.properties";
    private final String keepEmailDomainsFileLocation = "/apps/apps-config/keepEmailDomains.properties";
    private final String dropEmailDomainsFileLocation = "/apps/apps-config/dropEmailDomains.properties";

    private File keepEmailDomainsFile;
    private File dropEmailDomainsFile;

    private LdapEntryDaoImpl ldapEntryDao;

    private Set<String> returnAttributes;

    public RelayResearchDao() throws Exception
    {
        Properties properties = new PropertiesWithFallback(false, ldapPropertiesFileLocation);

        ldapEntryDao = new LdapEntryDaoImpl(new Ldap(properties.getProperty("ldapUrl"),
                properties.getProperty("ldapUsername"), properties.getProperty("ldapPassword")),
                properties.getProperty("ldapBaseDn"));

        keepEmailDomainsFile = new File(keepEmailDomainsFileLocation);
        dropEmailDomainsFile = new File(dropEmailDomainsFileLocation);

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

                SyncUser syncUser = new SyncUser(userAttributes, pshrUser.getEmail());
                syncUsers.add(syncUser);
            }
            catch (EntryLookupNoResultsException e)
            {
                syncUsers.add(new SyncUser(pshrUser));
            }
            catch(EntryLookupMoreThanOneResultException e)
            {
                Map<String, String> moreSearchAttributes = Maps.newHashMap();
                moreSearchAttributes.put(ldapAttributes.employeeNumber, pshrUser.getEmployeeId());
                moreSearchAttributes.put(ldapAttributes.givenname, pshrUser.getFirstName());
                moreSearchAttributes.put(ldapAttributes.surname, pshrUser.getLastName());

                try
                {
                    Multimap<String, String> userAttributes = ldapEntryDao.getLdapEntry(moreSearchAttributes,
                            returnAttributes);

                    SyncUser syncUser = new SyncUser(userAttributes, pshrUser.getEmail());
                    syncUsers.add(syncUser);
                }
                catch (EntryLookupException exception)
                {
                    System.out.println("Could not find exactly one entry: " + pshrUser.toString());
                }
            }
            catch(NoSuchElementException e)
            {
                e.printStackTrace();
            }
        }

        return syncUsers;
    }

    public Status isCruDomain(String email) throws IOException
    {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        List<String> domains = Files.readLines(keepEmailDomainsFile, Charsets.UTF_8);

        for(String cruDomain: domains)
        {
            if(cruDomain.toLowerCase().equals(domain))
            {
                return Status.approved;
            }
        }

        domains = Files.readLines(dropEmailDomainsFile, Charsets.UTF_8);

        for(String cruDomain: domains)
        {
            if(cruDomain.toLowerCase().equals(domain))
            {
                return Status.nonapproved;
            }
        }

        return Status.nonCru;
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
        returnAttributes.add(ldapAttributes.proxyAddresses);

        return returnAttributes;
    }


}
