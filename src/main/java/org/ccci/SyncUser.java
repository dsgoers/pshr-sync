package org.ccci;

import com.google.common.collect.Multimap;
import org.ccci.idm.dao.pshr.PSHRStaff;
import org.ccci.idm.ldap.attributes.LdapAttributes;
import org.ccci.idm.ldap.attributes.LdapAttributesActiveDirectory;
import org.ccci.idm.obj.IdentityUser;

import java.util.NoSuchElementException;

/**
 * Created by dsgoers on 2/27/15.
 */
public class SyncUser
{
    private String employeeId;
    private String firstName;
    private String lastName;
    private String pshrEmail;
    private String relayUsername;
    private String designation;
    private String ministry;
    private String department;
    private String status;

    private String cruDomainRelayUsername = "";
    private boolean inGoogle;
    private String emailsMatch = "no";

    private final LdapAttributes ldapAttributes = new LdapAttributesActiveDirectory();

    public SyncUser(IdentityUser identityUser)
    {
        employeeId = identityUser.getEmployee().getEmployeeId();
        firstName = identityUser.getPerson().getFirst();
        lastName = identityUser.getPerson().getLast();
        relayUsername = identityUser.getAccount().getUsername();
        designation = identityUser.getDesignation().getDesignationId();
        ministry = identityUser.getEmployee().getMinistry();
        department = identityUser.getEmployee().getDepartmentNumber();
        status = identityUser.getEmployee().getEmployeeStatus();
    }

    public SyncUser(PSHRStaff pshrStaff)
    {
        employeeId = pshrStaff.getEmployeeId();
        firstName = pshrStaff.getFirstName();
        lastName = pshrStaff.getLastName();
        pshrEmail = pshrStaff.getEmail();
    }

    public SyncUser(Multimap<String, String> userAttributes)
    {
        employeeId = userAttributes.get(ldapAttributes.employeeNumber).iterator().next();
        firstName = userAttributes.get(ldapAttributes.givenname).iterator().next();
        lastName = userAttributes.get(ldapAttributes.surname).iterator().next();
        relayUsername = userAttributes.get(ldapAttributes.username).iterator().next();
        designation = userAttributes.get(ldapAttributes.designationId).iterator().next();
        try
        {
            ministry = userAttributes.get(ldapAttributes.ministryCode).iterator().next();
        }
        catch (NoSuchElementException e)
        {

        }
        department = userAttributes.get(ldapAttributes.departmentNumber).iterator().next();
        status = userAttributes.get(ldapAttributes.employeeStatus).iterator().next();
    }

    public String getStatus()
    {
        return status;
    }

    public String getEmployeeId()
    {
        return employeeId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getPshrEmail()
    {
        return pshrEmail;
    }

    public String getRelayUsername()
    {
        return relayUsername;
    }

    public String getDesignation()
    {
        return designation;
    }

    public String getMinistry()
    {
        return ministry;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setPshrEmail(String pshrEmail)
    {
        this.pshrEmail = pshrEmail;
    }

    public boolean isInGoogle()
    {
        return inGoogle;
    }

    public void setInGoogle(boolean inGoogle)
    {
        this.inGoogle = inGoogle;
    }

    public String isCruDomainRelayUsername()
    {
        return cruDomainRelayUsername;
    }

    public void setCruDomainRelayUsername(String isCruDomainRelayUsername)
    {
        this.cruDomainRelayUsername = isCruDomainRelayUsername;
    }

    public String getEmailsMatch()
    {
        return emailsMatch;
    }

    public void setEmailsMatch(String emailsMatch)
    {
        this.emailsMatch = emailsMatch;
    }
}
