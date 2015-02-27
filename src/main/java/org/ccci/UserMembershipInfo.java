package org.ccci;

import org.ccci.idm.obj.IdentityUser;

import java.util.Set;

/**
 * Created by dsgoers on 2/17/15.
 */
public class UserMembershipInfo
{
    private Set<IdentityUser> usersWithoutMembership;
    private Set<IdentityUser> members;

    public UserMembershipInfo(Set<IdentityUser> usersWithoutMembership, Set<IdentityUser> members)
    {
        this.usersWithoutMembership = usersWithoutMembership;
        this.members = members;
    }

    public int getTotalUsers()
    {
        return usersWithoutMembership.size() + members.size();
    }

    public Set<IdentityUser> getUsersWithoutMembership()
    {
        return usersWithoutMembership;
    }

    public void setUsersWithoutMembership(Set<IdentityUser> usersWithoutMembership)
    {
        this.usersWithoutMembership = usersWithoutMembership;
    }

    public Set<IdentityUser> getMembers()
    {
        return members;
    }

    public void setMembers(Set<IdentityUser> members)
    {
        this.members = members;
    }
}
