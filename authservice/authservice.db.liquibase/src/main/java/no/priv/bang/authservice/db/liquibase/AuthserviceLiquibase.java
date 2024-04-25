/*
 * Copyright 2018-2023 Steinar Bang
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
import java.util.Map;

import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.authservice.definitions.AuthserviceException;

public class AuthserviceLiquibase {

    public void createInitialSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "authservice-db-changelog/db-changelog-1.0.0.xml");
    }

    public void applyChangelist(Connection connection, ClassLoader classLoader, String changelistClasspathResource) throws LiquibaseException {
        try (var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))) {
            Map<String, Object> scopeObjects = Map.of(
                Scope.Attr.database.name(), database,
                Scope.Attr.resourceAccessor.name(), new ClassLoaderResourceAccessor(classLoader));

            Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                        .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                        .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changelistClasspathResource)
                        .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                        .execute());
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthserviceException("Error applying liquibase changelist in authservice", e);
        }
    }

    public void updateSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "authservice-db-changelog/db-changelog-1.1.0.xml");
    }

    private void applyLiquibaseChangelist(Connection connection, String changelistClasspathResource) throws LiquibaseException {
        applyChangelist(connection, getClass().getClassLoader(), changelistClasspathResource);
    }

}
