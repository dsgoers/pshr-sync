package org.ccci;

import java.util.Set;

/**
 * Created by dsgoers on 2/17/15.
 */
public class UserMembershipInfo
{
    private Set<SyncUser> usersWithoutMembership;
    private Set<SyncUser> members;

    public UserMembershipInfo(Set<SyncUser> usersWithoutMembership, Set<SyncUser> members)
    {
        this.usersWithoutMembership = usersWithoutMembership;
        this.members = members;
    }

    public int getTotalUsers()
    {
        return usersWithoutMembership.size() + members.size();
    }

    public Set<SyncUser> getUsersWithoutMembership()
    {
        return usersWithoutMembership;
    }

    public void setUsersWithoutMembership(Set<SyncUser> usersWithoutMembership)
    {
        this.usersWithoutMembership = usersWithoutMembership;
    }

    public Set<SyncUser> getMembers()
    {
        return members;
    }

    public void setMembers(Set<SyncUser> members)
    {
        this.members = members;
    }
}
