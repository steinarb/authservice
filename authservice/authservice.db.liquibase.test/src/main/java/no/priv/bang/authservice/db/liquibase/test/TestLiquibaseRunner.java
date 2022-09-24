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
package no.priv.bang.authservice.db.liquibase.test;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import no.priv.bang.authservice.db.liquibase.AuthserviceLiquibase;
import no.priv.bang.authservice.definitions.AuthserviceException;

@Component(immediate=true, property = "name=authservicedb")
public class TestLiquibaseRunner implements PreHook {

    @Activate
    public void activate() {
        // Called after all injections have been satisfied and before the PreHook service is exposed
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        AuthserviceLiquibase liquibase = new AuthserviceLiquibase();

        try (Connection connection = datasource.getConnection()) {
            liquibase.createInitialSchema(connection);
        } catch (Exception e) {
            throw new AuthserviceException("Failed to create schema for authservice Derby test database component", e);
        }

        try (Connection connection = datasource.getConnection()) {
            liquibase.applyChangelist(connection, getClass().getClassLoader(), "db-changelog/db-changelog.xml");
        } catch (Exception e) {
            throw new AuthserviceException("Failed to insert mock data in authservice Derby test database component", e);
        }

        try (Connection connection = datasource.getConnection()) {
            liquibase.updateSchema(connection);
        } catch (Exception e) {
            throw new AuthserviceException("Failed to update schema of authservice Derby test database component", e);
        }
    }

}
