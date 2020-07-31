/*
 * Copyright 2020 Steinar Bang
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
        User admin = new User(1, "admin", "admin@gmail.com", "Admin", "Istrator");
        User on = new User(2, "on", "olanordmann2345@gmail.com", "Ola", "Nordmann");
        User kn = new User(3, "kn", "karinordmann3456@gmail.com", "Kari", "Nordmann");
        User jad = new User(4, "jad", "janedoe7896@gmail.com", "Jane", "Doe");
        User jod = new User(5, "jod", "johndoe6789@gmail.com", "John", "Doe");
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
        Role admin = new Role(1, "admin", "Administrate stuff");
        Role caseworker = new Role(2, "caseworker", "Respond to cases");
        Role visitor = new Role(3, "visitor", "Just browsing");
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
        Permission user_admin_api_read = new Permission(1, "user_admin_api_read", "User admin read access");
        Permission user_admin_api_write = new Permission(2, "user_admin_api_write", "User admin write access");
        Permission caseworker_read = new Permission(3, "caseworker_read", "Caseworker read access");
        Permission caseworker_write = new Permission(4, "caseworker_write", "Caseworker write access");
        Permission user_read = new Permission(5, "user_read", "User read access");
        return Arrays.asList(user_admin_api_read, user_admin_api_write, caseworker_read, caseworker_write, user_read);
    }

    private Testdata() {
        // Private to avoid instantiation
    }

}
