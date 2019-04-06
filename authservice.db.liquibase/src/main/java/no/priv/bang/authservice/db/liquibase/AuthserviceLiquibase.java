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
package no.priv.bang.authservice.db.liquibase;

import java.sql.Connection;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class AuthserviceLiquibase {

    public void createInitialSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "authservice-db-changelog/db-changelog-1.0.0.xml");
    }

    public void applyChangelist(Connection connection, ClassLoader classLoader, String changelistClasspathResource) throws LiquibaseException {
        Liquibase liquibase = createLiquibaseInstance(connection, classLoader, changelistClasspathResource);
        liquibase.update("");
    }

    public void updateSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "authservice-db-changelog/db-changelog-1.1.0.xml");
    }

    public boolean forceReleaseLocks(Connection connection) throws LiquibaseException {
        Liquibase liquibase = createLiquibaseInstance(connection, "authservice-db-changelog/db-changelog-1.0.0.xml");
        liquibase.forceReleaseLocks();
        return true;
    }

    private void applyLiquibaseChangelist(Connection connection, String changelistClasspathResource) throws LiquibaseException {
        Liquibase liquibase = createLiquibaseInstance(connection, changelistClasspathResource);
        liquibase.update("");
    }

    private Liquibase createLiquibaseInstance(Connection connection, String changelistClasspathResource) throws LiquibaseException {
        ClassLoader currentClassloader = getClass().getClassLoader();
        return createLiquibaseInstance(connection, currentClassloader, changelistClasspathResource);
    }

    Liquibase createLiquibaseInstance(Connection connection, ClassLoader currentClassloader, String changelistClasspathResource) throws LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connection);
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(currentClassloader);
        return new Liquibase(changelistClasspathResource, classLoaderResourceAccessor, databaseConnection);
    }

}
