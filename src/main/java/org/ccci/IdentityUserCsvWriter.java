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
    public void writeIdentityUsersMinistryInfoToCsv(String fileName, Set<IdentityUser> users) throws IOException
    {
        FileWriter writer = new FileWriter(fileName);

        Set<String> headers = Sets.newHashSet();
        headers.add("Employee Number");
        headers.add("First Name");
        headers.add("Last Name");
        headers.add("Username");
        headers.add("Designation");
        headers.add("Ministry");
        headers.add("Department");
        headers.add("Status");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');

        for(IdentityUser user: users)
        {
            writeValue(writer, user.getEmployee().getEmployeeId());
            writeValue(writer, user.getPerson().getFirst());
            writeValue(writer, user.getPerson().getLast());
            writeValue(writer, user.getAccount().getUsername());
            writeValue(writer, user.getDesignation().getDesignationId());
            writeValue(writer, user.getEmployee().getMinistry());
            writeValue(writer, user.getEmployee().getDepartmentNumber());
            writeValue(writer, user.getEmployee().getStatusCode());
            writer.append('\n');
        }

        writer.flush();
        writer.close();
    }

    public void writeIdentityUsersToCsv(String fileName, Set<IdentityUser> users) throws IOException
    {
        FileWriter writer = new FileWriter(fileName);

        Set<String> headers = Sets.newHashSet();
        headers.add("Employee Number");
        headers.add("First Name");
        headers.add("Last Name");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');

        for(IdentityUser user: users)
        {
            writeValue(writer, user.getEmployee().getEmployeeId());
            writeValue(writer, user.getPerson().getFirst());
            writeValue(writer, user.getPerson().getLast());
            writer.append('\n');
        }

        writer.flush();
        writer.close();
    }

    private void writeValue(FileWriter writer, String value) throws IOException
    {
        writer.append(value);
        writer.append(',');
    }

}
