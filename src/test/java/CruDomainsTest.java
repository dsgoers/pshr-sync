import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by dsgoers on 3/16/15.
 */
public class CruDomainsTest
{
    private final String keepEmailDomainsFileLocation = "/apps/apps-config/keepEmailDomains.properties";
    private final String dropEmailDomainsFileLocation = "/apps/apps-config/dropEmailDomains.properties";

    private File keepEmailDomainsFile;
    private File dropEmailDomainsFile;

    @Before
    public void before()
    {
        keepEmailDomainsFile = new File(keepEmailDomainsFileLocation);
        dropEmailDomainsFile = new File(dropEmailDomainsFileLocation);
    }

    @Test
    public void domainRepeatTest() throws IOException
    {
        List<String> keepCruDomains = Files.readLines(keepEmailDomainsFile, Charsets.UTF_8);
        List<String> dropCruDomains = Files.readLines(dropEmailDomainsFile, Charsets.UTF_8);

        for(String keepDomain: keepCruDomains)
        {
            for (String dropDomain : dropCruDomains)
            {
                Assert.assertNotEquals(keepDomain.toLowerCase(), dropDomain.toLowerCase());
        }
        }
    }

}
