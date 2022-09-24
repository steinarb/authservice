/*
 * Copyright 2018-2022 Steinar Bang
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
package no.priv.bang.authservice.db.liquibase;

import java.sql.Connection;

import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.authservice.definitions.AuthserviceException;

public class AuthserviceLiquibase {

    public void createInitialSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "authservice-db-changelog/db-changelog-1.0.0.xml");
    }

    public void applyChangelist(Connection connection, ClassLoader classLoader, String changelistClasspathResource) throws LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connection);
        try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(classLoader)) {
            try(var liquibase = new Liquibase(changelistClasspathResource, classLoaderResourceAccessor, databaseConnection)) {
                liquibase.update("");
            }
        } catch (Exception e) {
            throw new AuthserviceException("Error applying liquibase changelist in authservice", e);
        }
    }

    public void updateSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "authservice-db-changelog/db-changelog-1.1.0.xml");
    }

    public boolean forceReleaseLocks(Connection connection, LogService logservice) {
        Logger logger = logservice.getLogger(getClass());
        try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
            DatabaseConnection databaseConnection = new JdbcConnection(connection);
            try(var liquibase = new Liquibase("authservice-db-changelog/db-changelog-1.0.0.xml", classLoaderResourceAccessor, databaseConnection)) {
                liquibase.forceReleaseLocks();
                logger.info("Liquibase lock successfully forced, continuing without modifying the schema");
                return true;
            }
        } catch (Exception e) {
            logger.warn("Authservice failed to force lock", e);
            return false;
        }
    }

    private void applyLiquibaseChangelist(Connection connection, String changelistClasspathResource) {
        DatabaseConnection databaseConnection = new JdbcConnection(connection);
        try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
            try(var liquibase = new Liquibase(changelistClasspathResource, classLoaderResourceAccessor, databaseConnection)) {
                liquibase.update("");
            }
        } catch (Exception e) {
            throw new AuthserviceException("Error applying liquibase changelist in authservice", e);
        }
    }

}
