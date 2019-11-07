/*
 * Copyright 2018-2019 Steinar Bang
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
package no.priv.bang.authservice.db.liquibase.test;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.db.liquibase.AuthserviceLiquibase;
import no.priv.bang.authservice.definitions.AuthserviceException;

@Component(immediate=true, property = "name=authservicedb")
public class TestLiquibaseRunner implements PreHook {

    private LogService logservice;
    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Activate
    public void activate() {
        // Called after all injections have been satisfied and before the PreHook service is exposed
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        try {
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

}
