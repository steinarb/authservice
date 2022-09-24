/*
 * Copyright 2019-2022 Steinar Bang
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
package no.priv.bang.authservice.db.liquibase.production;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import liquibase.exception.LockException;
import no.priv.bang.authservice.db.liquibase.AuthserviceLiquibase;
import no.priv.bang.authservice.definitions.AuthserviceException;

@Component(immediate=true, property = "name=authservicedb")
public class ProductionLiquibaseRunner implements PreHook {

    private LogService logservice;
    private Logger logger;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
        this.logger = logservice.getLogger(getClass());
    }

    @Activate
    public void activate() {
        // Called after all injections have been satisfied and before the PreHook service is exposed
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        AuthserviceLiquibase liquibase = new AuthserviceLiquibase();
        applySchemaChangelistsAndTryForcingLiquibaseLockIfFailingToUnlock(datasource, liquibase);

        try(var connection = datasource.getConnection()) {
            liquibase.applyChangelist(connection, getClass().getClassLoader(), "db-changelog/db-changelog.xml");
        } catch (Exception e) {
            throw new AuthserviceException("Failed to insert initial data in authservice postgresql database", e);
        }

        try(var connection = datasource.getConnection()) {
            liquibase.updateSchema(connection);
        } catch (Exception e) {
            throw new AuthserviceException("Failed to update schma in authservice postgresql database", e);
        }
    }

    void applySchemaChangelistsAndTryForcingLiquibaseLockIfFailingToUnlock(DataSource datasource, AuthserviceLiquibase liquibase) {
        try(var connection = datasource.getConnection()) {
            liquibase.createInitialSchema(connection);
        } catch (LockException e) {
            logger.warn("Authservice PostgreSQL component failed to aquire liquibase lock, trying to force liquibase lock", e);

            try(var connection = datasource.getConnection()) {
                liquibase.forceReleaseLocks(connection, logservice);
            } catch (SQLException e1) {
                throw new AuthserviceException("Error when closing JDBC connection after breaking authservice database liquibase lock", e1);
            }

            try(var connection = datasource.getConnection()) {
                liquibase.createInitialSchema(connection);
            } catch (Exception e2) {
                throw new AuthserviceException("Failed to create schema in authservice postgresql database after forced lock", e2);
            }
        } catch (Exception e) {
            throw new AuthserviceException("Failed to create schema in authservice postgresql database", e);
        }
    }

}
