package org.ccci;

import com.google.common.collect.Lists;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by dsgoers on 2/17/15.
 */
public class SyncUserCsvWriter
{
    public void writeSyncUsersToCsv(String fileName, Set<SyncUser> users) throws IOException
    {
        FileWriter writer = new FileWriter(fileName);

        List<String> headers = Lists.newArrayList();
        headers.add("Employee Number");
        headers.add("First Name");
        headers.add("Last Name");
        headers.add("Relay username");
        headers.add("PSHR email");
        headers.add("Designation");
        headers.add("Ministry");
        headers.add("Department");
        headers.add("Status");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');

        for(SyncUser user: users)
        {
            writeValue(writer, user.getEmployeeId());
            writeValue(writer, user.getFirstName());
            writeValue(writer, user.getLastName());
            writeValue(writer, user.getRelayUsername());
            writeValue(writer, user.getPshrEmail());
            writeValue(writer, user.getDesignation());
            writeValue(writer, user.getMinistry());
            writeValue(writer, user.getDepartment());
            writeValue(writer, user.getStatus());
            writer.append('\n');
        }

        writer.flush();
        writer.close();
    }

    public void writePshrUsersToCsv(String fileName, Set<SyncUser> users) throws IOException
    {
        FileWriter writer = new FileWriter(fileName);

        List<String> headers = Lists.newArrayList();
        headers.add("Employee Number");
        headers.add("PSHR email");
        headers.add("First Name");
        headers.add("Last Name");

        for(String header: headers)
        {
            writeValue(writer, header);
        }
        writer.append('\n');

        for(SyncUser user: users)
        {
            writeValue(writer, user.getEmployeeId());
            writeValue(writer, user.getPshrEmail());
            writeValue(writer, user.getFirstName());
            writeValue(writer, user.getLastName());
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
