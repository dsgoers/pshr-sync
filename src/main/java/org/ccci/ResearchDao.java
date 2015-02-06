package org.ccci;

import org.ccci.idm.dao.LdapEntryDaoImpl;
import org.ccci.idm.ldap.Ldap;
import org.ccci.idm.obj.IdentityUser;
import org.cru.migration.domain.PSHRStaff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dsgoers on 2/6/15.
 */
public class ResearchDao
{
    private LdapEntryDaoImpl ldapEntryDao;

    public ResearchDao(String ldapUrl, String rootDn, String username, String password) throws Exception
    {
        ldapEntryDao = new LdapEntryDaoImpl(new Ldap(ldapUrl, username, password), rootDn);
    }

    public ArrayList<IdentityUser> getUsersWithNoRelayAccount(Set<PSHRStaff> pshrUsers) throws Exception
    {
        for(PSHRStaff pshrUser: pshrUsers)
        {
            HashMap<String, String> searchAttributes = new HashMap<String, String>();
            searchAttributes.put("employeeNumber", pshrUser.getEmployeeId());
            searchAttributes.put("givenName", pshrUser.getFirstName());
            searchAttributes.put("sn", pshrUser.getLastName());

            HashSet<String> returnAttributes = new HashSet<String>();
            returnAttributes.add("employeeNumber");
            returnAttributes.add("employeeNumber");
            returnAttributes.add("employeeNumber");

            try
            {

            }


        }


    }




}
