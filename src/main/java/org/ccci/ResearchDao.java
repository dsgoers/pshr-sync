package org.ccci;

import com.google.common.collect.Multimap;
import org.ccci.idm.dao.LdapEntryDaoImpl;
import org.ccci.idm.ldap.Ldap;
import org.ccci.idm.ldap.attributes.LdapAttributes;
import org.ccci.idm.ldap.attributes.LdapAttributesActiveDirectory;
import org.ccci.idm.obj.IdentityUser;
import org.cru.migration.domain.PSHRStaff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dsgoers on 2/6/15.
 */
public class ResearchDao
{
    private LdapEntryDaoImpl ldapEntryDao;

    private HashSet<String> returnAttributes;

    LdapAttributes ldapAttributes = new LdapAttributesActiveDirectory();

    public ResearchDao(String ldapUrl, String rootDn, String username, String password) throws Exception
    {
        ldapEntryDao = new LdapEntryDaoImpl(new Ldap(ldapUrl, username, password), rootDn);

        setReturnAttributes();
    }

    public ArrayList<String> getEmployeeIdsWithNoRelayAccount(Set<PSHRStaff> pshrUsers) throws Exception
    {
        ArrayList<String> employeeIdsWithNoRelayAccount = new ArrayList<String>();

        for(PSHRStaff pshrUser: pshrUsers)
        {
            HashMap<String, String> searchAttributes = new HashMap<String, String>();
            //should the attribute only be added if it is not null?
            searchAttributes.put("employeeNumber", pshrUser.getEmployeeId());
            searchAttributes.put("givenName", pshrUser.getFirstName());
            searchAttributes.put("sn", pshrUser.getLastName());

            try
            {
                ldapEntryDao.getLdapEntry(searchAttributes, returnAttributes);
            }
            catch (Exception e)
            {
                if(e.getMessage().equals("Could not find one entry for attribute list."))
                {
                    employeeIdsWithNoRelayAccount.add(pshrUser.getEmployeeId());
                }
            }
        }

        return employeeIdsWithNoRelayAccount;
    }

    public ArrayList<IdentityUser> getUsersWithoutCruDomainEmails(Set<PSHRStaff> pshrUsers)
    {
        ArrayList<IdentityUser> users = new ArrayList<IdentityUser>();

        for(PSHRStaff pshrUser: pshrUsers)
        {
            HashMap<String, String> searchAttributes = new HashMap<String, String>();
            //should the attribute only be added if it is not null?
            searchAttributes.put("employeeNumber", pshrUser.getEmployeeId());
            searchAttributes.put("givenName", pshrUser.getFirstName());
            searchAttributes.put("sn", pshrUser.getLastName());

            try
            {
                Multimap<String, String> userAttributes = ldapEntryDao.getLdapEntry(searchAttributes, returnAttributes);

                String email = userAttributes.get(ldapAttributes.username).iterator().next();
                String domain = email.substring(email.indexOf("@") + 1);

                if(!getCruDomains().contains(domain))
                {
                    users.add(identityUserFromUserAttributes(userAttributes));
                }
            }
            catch (Exception e)
            {
                if(e.getMessage().equals("Could not find one entry for attribute list."))
                {
                    System.out.println("Could not find user: " + pshrUser.getEmployeeId() + " " + pshrUser.getFirstName()
                            + pshrUser.getLastName());
                }
            }

        }
        return users;
    }



    public ArrayList<IdentityUser> getUsersWithoutGoogleMembership(Set<PSHRStaff> pshrUsers)
    {
        ArrayList<IdentityUser> users = new ArrayList<IdentityUser>();

        for(PSHRStaff pshrUser: pshrUsers)
        {
            HashMap<String, String> searchAttributes = new HashMap<String, String>();
            //should the attribute only be added if it is not null?
            searchAttributes.put("employeeNumber", pshrUser.getEmployeeId());
            searchAttributes.put("givenName", pshrUser.getFirstName());
            searchAttributes.put("sn", pshrUser.getLastName());

            try
            {
                Multimap<String, String> userAttributes = ldapEntryDao.getLdapEntry(searchAttributes, returnAttributes);
                Collection<String> memberOfValues = userAttributes.get(ldapAttributes.memberOf);

                for(String value: memberOfValues)
                {
                    if(value.contains("CN=GoogleApps"))
                    {
                        users.add(identityUserFromUserAttributes(userAttributes));
                    }
                }
            }
            catch (Exception e)
            {
                if(e.getMessage().equals("Could not find one entry for attribute list."))
                {
                    System.out.println("Could not find user: " + pshrUser.getEmployeeId() + " " + pshrUser.getFirstName()
                            + pshrUser.getLastName());
                }
            }

        }
        return users;
    }


    private IdentityUser identityUserFromUserAttributes(Multimap<String, String> userAttributes)
    {
        IdentityUser user = new IdentityUser();
        user.getEmployee().setEmployeeId(userAttributes.get(ldapAttributes.employeeNumber).iterator().next());

        user.getPerson().setFirst(userAttributes.get(ldapAttributes.givenname).iterator().next());
        user.getPerson().setLast(userAttributes.get(ldapAttributes.surname).iterator().next());
        user.getAccount().setUsername(userAttributes.get(ldapAttributes.username).iterator().next());
        user.getDesignation().setDesignationId(userAttributes.get(ldapAttributes.designationId).iterator
                ().next());
        user.getEmployee().setMinistry(userAttributes.get(ldapAttributes.ministryCode).iterator().next());
        user.getEmployee().setDepartmentNumber(userAttributes.get(ldapAttributes.departmentNumber).iterator().next());
        user.getEmployee().setStatusCode(userAttributes.get(ldapAttributes.employeeStatus).iterator()
                .next());

        return user;
    }

    private ArrayList<String> getCruDomains()
    {
        String cruDomains = "cru.org,agapeitalia.eu,agapeitalia.org,aiaretreatcenter.com,aiasportscomplex.com," +
                "anythingcantalk.com,arc.gt,arclight.org,arrowheadconferences.org,arrowheadsprings.org," +
                "athletesinaction.org,beyondtheultimate.org,bridgesinternational.com,brokenphonebooth.com," +
                "campuscrusadeforchrist.com,ccci.org,ce-un.org,crumilitary.org,destinomovement.com,epicmovement.com," +
                "facultycommons.org,familylife.com,gcfccc.org,giftandestate.org,gocampus.org,historyshandful.org," +
                "hopefororphans.org,inspirationalfilms.com,isponline.org,isptrips.org,jesusfactorfiction.com," +
                "jesusfilm.org,jesusfilmmedia.org,jesusfilmmissiontrips.org,jesusforchildren.org,jesusvideo.org," +
                "jfministrypartners.org,keynote.org,magdalenatoday.com,militaryministry.org,milmin.org," +
                "mission865.org,mpdx.org,mylastdaymovie.com,priorityassociates.org,promail.ru," +
                "reachinginternationals.com,schindlercenter.com,sharepoint.ccci.org,studentventure.com," +
                "table71.org,uscm.org,vidaenfamiliahoy.com,vonettebright.org,womenforjesus.org,zcmanagement.com";

        return new ArrayList<String>(Arrays.asList(cruDomains.split(",")));

    }

    private void setReturnAttributes()
    {
        returnAttributes.add(ldapAttributes.employeeNumber);
        returnAttributes.add(ldapAttributes.givenname);
        returnAttributes.add(ldapAttributes.surname);
        returnAttributes.add(ldapAttributes.username);
        returnAttributes.add(ldapAttributes.designationId);
        returnAttributes.add(ldapAttributes.ministryCode);
        returnAttributes.add(ldapAttributes.departmentNumber);
        returnAttributes.add(ldapAttributes.employeeStatus);
    }




}
