package org.ccci;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
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
import java.util.ArrayList;
import java.util.Arrays;
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

    private final String ldapPropertiesFile = "/apps/apps-config/adldsproperties.properties";

    private LdapEntryDaoImpl ldapEntryDao;

    private Set<String> returnAttributes;

    public RelayResearchDao() throws Exception
    {
        Properties properties = new PropertiesWithFallback(false, ldapPropertiesFile);

        ldapEntryDao = new LdapEntryDaoImpl(new Ldap(properties.getProperty("ldapUrl"),
                properties.getProperty("ldapUsername"), properties.getProperty("ldapPassword")),
                properties.getProperty("ldapBaseDn"));

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

    public boolean isInGoogle(SyncUser syncUser) throws NamingException
    {
        try
        {
            Map<String, String> searchAttributes = Maps.newHashMap();
            searchAttributes.put(ldapAttributes.username, syncUser.getRelayUsername());

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

    public boolean isCruDomain(String domain)
    {
        return getCruDomains().contains(domain.toLowerCase());
    }

    private List<String> getCruDomains()
    {
        String cruDomains = ("cru.org,agapeitalia.eu,agapeitalia.org,aiaretreatcenter.com,aiasportscomplex.com," +
                "anythingcantalk.com,arc.gt,arclight.org,arrowheadconferences.org,arrowheadsprings.org," +
                "athletesinaction.org,beyondtheultimate.org,bridgesinternational.com,brokenphonebooth.com," +
                "campuscrusadeforchrist.com,ccci.org,ce-un.org,crumilitary.org,destinomovement.com,epicmovement.com," +
                "facultycommons.org,familylife.com,gcfccc.org,giftandestate.org,gocampus.org,historyshandful.org," +
                "hopefororphans.org,inspirationalfilms.com,isponline.org,isptrips.org,jesusfactorfiction.com," +
                "jesusfilm.org,jesusfilmmedia.org,jesusfilmmissiontrips.org,jesusforchildren.org,jesusvideo.org," +
                "jfministrypartners.org,keynote.org,magdalenatoday.com,militaryministry.org,milmin.org," +
                "mission865.org,mpdx.org,mylastdaymovie.com,priorityassociates.org,promail.ru," +
                "reachinginternationals.com,schindlercenter.com,sharepoint.ccci.org,studentventure.com," +
                "table71.org,uscm.org,vidaenfamiliahoy.com,vonettebright.org,womenforjesus.org,zcmanagement.com").toLowerCase();

        return new ArrayList<String>(Arrays.asList(cruDomains.split(",")));

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
