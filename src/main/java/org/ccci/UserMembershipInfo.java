package org.ccci;


/**
 * Created by dsgoers on 2/17/15.
 */
public class UserMembershipInfo
{
    private int members;
    private int nonMembers;

    public int getTotalUsers()
    {
        return members + nonMembers;
    }

    public int getNonMembers()
    {
        return nonMembers;
    }

    public void addNonMember()
    {
        nonMembers++;
    }

    public int getMembers()
    {
        return members;
    }

    public void addMember()
    {
        members++;
    }

    public String toString()
    {
        return "Members: " + members + ", non members: " + nonMembers + ", total: " + getTotalUsers();
    }
}
