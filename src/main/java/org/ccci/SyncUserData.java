package org.ccci;

/**
 * Created by dsgoers on 3/20/15.
 */
public class SyncUserData
{
    private String employeeId;
    private String firstName;
    private String lastName;
    private String pshrEmail;
    private String relayUsername;
    private boolean inGoogle;

    private boolean emailsMatch;

    public enum Status {approved, nonapproved, nonCru, none}
    private Status pshrDomain;
    private Status relayDomain;

    public enum Scenario {i, ii, iii, iv, v, vi, vii, viii, ix}
    private Scenario scenario;


    public SyncUserData(SyncUser syncUser)
    {
        employeeId = syncUser.getEmployeeId();
        firstName = syncUser.getFirstName();
        lastName = syncUser.getLastName();
        pshrEmail = syncUser.getPshrEmail();
        relayUsername = syncUser.getRelayUsername();
        inGoogle = syncUser.isInGoogle();
    }

    public Status getPshrDomain()
    {
        return pshrDomain;
    }

    public void setPshrDomain(Status pshrDomain)
    {
        this.pshrDomain = pshrDomain;
    }

    public Status getRelayDomain()
    {
        return relayDomain;
    }

    public void setRelayDomain(Status relayDomain)
    {
        this.relayDomain = relayDomain;
    }

    public void setEmailsMatch(boolean emailsMatch)
    {
        this.emailsMatch = emailsMatch;
    }

    public void setScenario(Scenario scenario)
    {
        this.scenario = scenario;
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

    public boolean emailsMatch()
    {
        return emailsMatch;
    }

    public boolean isInGoogle()
    {
        return inGoogle;
    }

    public Scenario getScenario()
    {
        return scenario;
    }
}
