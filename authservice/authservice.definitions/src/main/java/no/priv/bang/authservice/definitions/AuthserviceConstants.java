/*
 * Copyright 2019 Steinar Bang
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
package no.priv.bang.authservice.definitions;

public class AuthserviceConstants {
    public static final String AUTHSERVICE_JDBC_URL = "authservice.db.jdbc.url";
    public static final String AUTHSERVICE_JDBC_USER = "authservice.db.jdbc.user";
    public static final String AUTHSERVICE_JDBC_PASSWORD = "authservice.db.jdbc.password"; // NOSONAR hard to write code to handle passwords without saying the word "password"

    private AuthserviceConstants() {}
}
