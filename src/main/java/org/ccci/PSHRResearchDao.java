package org.ccci;

import org.apache.commons.dbcp.BasicDataSource;
import org.ccci.idm.dao.datasource.BasicDataSourceFactory;
import org.ccci.idm.dao.pshr.PSHRDao;
import org.ccci.idm.dao.pshr.PSHRStaff;
import org.ccci.util.properties.PropertiesWithFallback;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.Set;

/**
 * Created by dsgoers on 2/10/15.
 */
public class PSHRResearchDao
{
    private final static String propertiesFile = "/apps/apps-config/pshr-properties.properties";

    PSHRDao pshrDao;

    public PSHRResearchDao() throws PropertyVetoException
    {
        Properties properties = new PropertiesWithFallback(false, propertiesFile);

        BasicDataSource basicDataSource  = new BasicDataSourceFactory().createInstance();

        basicDataSource.setDriverClassName(properties.getProperty("pshrDriverClass"));
        basicDataSource.setUrl(properties.getProperty("pshrJdbcUrl"));
        basicDataSource.setUsername(properties.getProperty("pshrUsername"));
        basicDataSource.setPassword(properties.getProperty("pshrPassword"));

        pshrDao = new PSHRDao();
        pshrDao.setDataSource(basicDataSource);
    }

    public Set<PSHRStaff> getAllNonTerminatedUsStaff() throws PropertyVetoException
    {
        return pshrDao.getAllNonTerminatedUSStaff();
    }

    public Set<PSHRStaff> getSomeUsStaff() throws PropertyVetoException
    {
        return pshrDao.getSomeUSStaff();
    }
}
