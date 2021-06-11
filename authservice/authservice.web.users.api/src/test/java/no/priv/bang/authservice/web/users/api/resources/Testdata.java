/*
 * Copyright 2020-2021 Steinar Bang
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
        User admin = User.with()
            .userid(1)
            .username("admin")
            .email("admin@gmail.com")
            .firstname("Admin")
            .lastname("Istrator")
            .build();
        User on = User.with()
            .userid(2)
            .username("on")
            .email("olanordmann2345@gmail.com")
            .firstname("Ola")
            .lastname("Nordmann")
            .build();
        User kn = User.with()
            .userid(3)
            .username("kn")
            .email("karinordmann3456@gmail.com")
            .firstname("Kari")
            .lastname("Nordmann")
            .build();
        User jad = User.with()
            .userid(4)
            .username("jad")
            .email("janedoe7896@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        User jod = User.with()
            .userid(5)
            .username("jod")
            .email("johndoe6789@gmail.com")
            .firstname("John")
            .lastname("Doe")
            .build();
        return Arrays.asList(admin, on, kn, jad, jod);
    }

    public static Map<String, List<Role>> createUserroles() {
        List<User> users = createUsers();
        User admin = users.get(0);
        User on = users.get(1);
        User kn = users.get(2);
        User jad = users.get(3);
        User jod = users.get(4);
        List<Role> roles = Testdata.createRoles();
        Role adminrole = roles.get(0);
        Role caseworker = roles.get(1);
        Map<String, List<Role>> userroles = new HashMap<>();
        userroles.put(admin.getUsername(), Arrays.asList(adminrole, caseworker));
        userroles.put(on.getUsername(), Arrays.asList(adminrole, caseworker));
        userroles.put(kn.getUsername(), Arrays.asList(adminrole, caseworker));
        userroles.put(jad.getUsername(), Arrays.asList(caseworker));
        userroles.put(jod.getUsername(), Arrays.asList(caseworker));
        return userroles;
    }

    public static List<Role> createRoles() {
        Role admin = Role.with()
            .id(1)
            .rolename("admin")
            .description("Administrate stuff")
            .build();
        Role caseworker = Role.with()
            .id(2)
            .rolename("caseworker")
            .description("Respond to cases")
            .build();
        Role visitor = Role.with()
            .id(3)
            .rolename("visitor")
            .description("Just browsing")
            .build();
        return Arrays.asList(admin, caseworker, visitor);
    }

    public static Map<String, List<Permission>> createRolesPermissions() {
        List<Role> roles = createRoles();
        Role admin = roles.get(0);
        Role caseworker = roles.get(1);
        List<Permission> permissions = Testdata.createPermissions();
        Permission user_admin_api_read = permissions.get(0);
        Permission user_admin_api_write = permissions.get(1);
        Permission caseworker_read = permissions.get(2);
        Permission caseworker_write = permissions.get(3);
        Permission user_read = permissions.get(4);
        Map<String, List<Permission>> rolespermissions = new HashMap<>();
        rolespermissions.put(admin.getRolename(), Arrays.asList(user_admin_api_read, user_admin_api_write, caseworker_read, caseworker_write, user_read));
        rolespermissions.put(caseworker.getRolename(), Arrays.asList(caseworker_read, caseworker_write, user_read));
        return rolespermissions;
    }

    public static List<Permission> createPermissions() {
        Permission user_admin_api_read = Permission.with()
            .id(1)
            .permissionname("user_admin_api_read")
            .description("User admin read access")
            .build();
        Permission user_admin_api_write = Permission.with()
            .id(2)
            .permissionname("user_admin_api_write")
            .description("User admin write access")
            .build();
        Permission caseworker_read = Permission.with()
            .id(3)
            .permissionname("caseworker_read")
            .description("Caseworker read access")
            .build();
        Permission caseworker_write = Permission.with()
            .id(4)
            .permissionname("caseworker_write")
            .description("Caseworker write access")
            .build();
        Permission user_read = Permission.with()
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
