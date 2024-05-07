/*
 * Copyright 2020-2024 Steinar Bang
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
package no.priv.bang.authservice.web.users.api.resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;

public class Testdata {

    public static List<User> createUsers() {
        var admin = User.with()
            .userid(1)
            .username("admin")
            .email("admin@gmail.com")
            .firstname("Admin")
            .lastname("Istrator")
            .build();
        var on = User.with()
            .userid(2)
            .username("on")
            .email("olanordmann2345@gmail.com")
            .firstname("Ola")
            .lastname("Nordmann")
            .build();
        var kn = User.with()
            .userid(3)
            .username("kn")
            .email("karinordmann3456@gmail.com")
            .firstname("Kari")
            .lastname("Nordmann")
            .build();
        var jad = User.with()
            .userid(4)
            .username("jad")
            .email("janedoe7896@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        var jod = User.with()
            .userid(5)
            .username("jod")
            .email("johndoe6789@gmail.com")
            .firstname("John")
            .lastname("Doe")
            .build();
        return Arrays.asList(admin, on, kn, jad, jod);
    }

    public static Map<String, List<Role>> createUserroles() {
        var users = createUsers();
        var admin = users.get(0);
        var on = users.get(1);
        var kn = users.get(2);
        var jad = users.get(3);
        var jod = users.get(4);
        var roles = Testdata.createRoles();
        var adminrole = roles.get(0);
        var caseworker = roles.get(1);
        var userroles = new HashMap<String, List<Role>>();
        userroles.put(admin.username(), Arrays.asList(adminrole, caseworker));
        userroles.put(on.username(), Arrays.asList(adminrole, caseworker));
        userroles.put(kn.username(), Arrays.asList(adminrole, caseworker));
        userroles.put(jad.username(), Arrays.asList(caseworker));
        userroles.put(jod.username(), Arrays.asList(caseworker));
        return userroles;
    }

    public static List<Role> createRoles() {
        var admin = Role.with()
            .id(1)
            .rolename("admin")
            .description("Administrate stuff")
            .build();
        var caseworker = Role.with()
            .id(2)
            .rolename("caseworker")
            .description("Respond to cases")
            .build();
        var visitor = Role.with()
            .id(3)
            .rolename("visitor")
            .description("Just browsing")
            .build();
        return Arrays.asList(admin, caseworker, visitor);
    }

    public static Map<String, List<Permission>> createRolesPermissions() {
        var roles = createRoles();
        var admin = roles.get(0);
        var caseworker = roles.get(1);
        var permissions = Testdata.createPermissions();
        var user_admin_api_read = permissions.get(0);
        var user_admin_api_write = permissions.get(1);
        var caseworker_read = permissions.get(2);
        var caseworker_write = permissions.get(3);
        var user_read = permissions.get(4);
        var rolespermissions = new HashMap<String, List<Permission>>();
        rolespermissions.put(admin.rolename(), Arrays.asList(user_admin_api_read, user_admin_api_write, caseworker_read, caseworker_write, user_read));
        rolespermissions.put(caseworker.rolename(), Arrays.asList(caseworker_read, caseworker_write, user_read));
        return rolespermissions;
    }

    public static List<Permission> createPermissions() {
        var user_admin_api_read = Permission.with()
            .id(1)
            .permissionname("user_admin_api_read")
            .description("User admin read access")
            .build();
        var user_admin_api_write = Permission.with()
            .id(2)
            .permissionname("user_admin_api_write")
            .description("User admin write access")
            .build();
        var caseworker_read = Permission.with()
            .id(3)
            .permissionname("caseworker_read")
            .description("Caseworker read access")
            .build();
        var caseworker_write = Permission.with()
            .id(4)
            .permissionname("caseworker_write")
            .description("Caseworker write access")
            .build();
        var user_read = Permission.with()
            .id(5)
            .permissionname("user_read")
            .description("User read access")
            .build();
        return Arrays.asList(user_admin_api_read, user_admin_api_write, caseworker_read, caseworker_write, user_read);
    }

    private Testdata() {
        // Private to avoid instantiation
    }

}
