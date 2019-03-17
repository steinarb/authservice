/*
 * Copyright 2018 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.authservice.db.derby.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.db.liquibase.AuthserviceLiquibase;
import no.priv.bang.authservice.definitions.AuthserviceDatabaseService;
import no.priv.bang.authservice.definitions.AuthserviceException;

@Component(immediate=true)
public class DerbyTestDatabase implements AuthserviceDatabaseService {

    private LogService logservice;
    private DataSourceFactory dataSourceFactory;
    private DataSource datasource;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Activate
    public void activate() {
        try {
            datasource = createDatasource();
            try(Connection connection = datasource.getConnection()) {
                AuthserviceLiquibase liquibase = new AuthserviceLiquibase();
                liquibase.createInitialSchema(connection);
                liquibase.applyChangelist(connection, getClass().getClassLoader(), "db-changelog/db-changelog.xml");
                liquibase.updateSchema(connection);
            }
        } catch (Exception e) {
            String message = "Failed to activate authservice Derby test database component";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public DataSource getDatasource() {
        return datasource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    private DataSource createDatasource() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        return dataSourceFactory.createDataSource(properties);
    }

}
