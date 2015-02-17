package org.ccci;

import com.google.common.collect.Sets;
import org.ccci.idm.obj.IdentityUser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by dsgoers on 2/17/15.
 */
public class IdentityUserCsvWriter
{
    private Set<String> headers;

    public IdentityUserCsvWriter() throws IOException
    {
        headers = Sets.newHashSet();
        headers.add("Employee Number");
        headers.add("First Name");
        headers.add("Last Name");
        headers.add("Username");
        headers.add("Designation");
        headers.add("Ministry");
        headers.add("Department");
        headers.add("Status");
    }

    public void writeIdentityUsersToCsv(String fileName, Set<IdentityUser> users) throws IOException
    {
        FileWriter writer = new FileWriter(fileName);

        for(String header: headers)
        {
            writer.append(header);
            writer.append(',');
        }
        writer.append('\n');

        for(IdentityUser user: users)
        {
            writer.append(user.getEmployee().getEmployeeId());
            writer.append(',');
            writer.append(user.getPerson().getFirst());
            writer.append(',');
            writer.append(user.getPerson().getLast());
            writer.append(',');
            writer.append(user.getAccount().getUsername());
            writer.append(',');
            writer.append(user.getDesignation().getDesignationId());
            writer.append(',');
            writer.append(user.getEmployee().getMinistry());
            writer.append(',');
            writer.append(user.getEmployee().getDepartmentNumber());
            writer.append(',');
            writer.append(user.getEmployee().getStatusCode());
            writer.append('\n');
        }

        writer.flush();
        writer.close();
    }

}
