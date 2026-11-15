package no.priv.bang.authservice.web.security.resources;
/*
 * Copyright 2019-2026 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.web.security.ShiroTestBase;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class LoggedInUserResourceTest extends ShiroTestBase {

    @Test
    void testFindLoggedInUser() {
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with().userid(1).username(username).email("jane@gmail.com").firstname("Jane").lastname("Doe").build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));

        loginUser(username, "1ad");

        var loggedInUser = LoggedInUserResource.findLoggedInUser(useradmin);
        assertTrue(loggedInUser.isPresent());
    }

    @Test
    void testFindLoggedInUserWhenUserIsNotLoggedIn() {
        var useradmin = mock(UserManagementService.class);

        createSubjectWithNullPrincipalAndBindItToThread();

        var loggedInUser = LoggedInUserResource.findLoggedInUser(useradmin);
        assertFalse(loggedInUser.isPresent());
    }

    @Test
    void testFindLoggedInUserWhenShiroNotInitialized() {
        var useradmin = mock(UserManagementService.class);

        createNullWebSubjectAndBindItToThread();

        assertThrows(AuthserviceException.class, () -> {
            LoggedInUserResource.findLoggedInUser(useradmin);
        });
    }

}
