/*
 * Copyright 2025 Steinar Bang
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

/***
 * Interface defining OSGi service providing configurable shiro values.
 *
 * The purpose of this interface is to provide a way for
 * adjusting shiro config values running i apache karaf.
 * without needing to change shiro.ini files and recompile
 * the apps containing and using the shiro.ini files
 */
public interface AuthserviceShiroConfigService {

    /**
     * Used to set the global session timeout of shiro's session manager.
     *
     * @return the time in milliseconds that any session may remain idle before expiring
     */
    long getGlobalSessionTimeout();

}
