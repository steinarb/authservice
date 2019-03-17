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
package no.priv.bang.authservice.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource.Util;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceDatabaseService;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;


/***
 * A DS component that implements a {@link UserManagementService}
 */
@Component(service=UserManagementService.class, immediate=true)
public class UserManagementServiceProvider implements UserManagementService {
    private LogService logservice;
    private AuthserviceDatabaseService database;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference
    public void setDatabase(AuthserviceDatabaseService database) {
        this.database = database;
    }

    @Activate
    public void activate() {
        // Called after all injections have been satisfied
    }

    @Override
    public List<User> getUsers() {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from users order by user_id")) {
                List<User> users = new ArrayList<>();
                try(ResultSet results = statement.executeQuery()) {
                    while(results.next()) {
                        User user = unpackUser(results);
                        users.add(user);
                    }
                }
                return users;
            }
        } catch (SQLException e) {
            String message = "UserManagmentService failed to get the list of users";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public List<User> modifyUser(User user) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update users set username=?, email=?, firstname=?, lastname=? where user_id=?")) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getFirstname());
                statement.setString(4, user.getLastname());
                statement.setInt(5, user.getUserid());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to update user with id %d", user.getUserid());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getUsers();
    }

    @Override
    public List<User> updatePassword(UserAndPasswords userAndPasswords) {
        if (userAndPasswords.getPassword1() == null || userAndPasswords.getPassword1().isEmpty()) {
            String message = "Failed to set password: Password can't be empty";
            logservice.log(LogService.LOG_WARNING, message);
            throw new AuthservicePasswordEmptyException(message);
        }

        if (userAndPasswords.getUser() == null) {
            String message = "Failed to set password: User can't be empty";
            logservice.log(LogService.LOG_WARNING, message);
            throw new AuthservicePasswordEmptyException(message);
        }

        if (!userAndPasswords.getPassword1().equals(userAndPasswords.getPassword2())) {
            String message = "Failed to set password: Passwords not identical";
            logservice.log(LogService.LOG_WARNING, message);
            throw new AuthservicePasswordsNotIdenticalException(message);
        }

        int userId = userAndPasswords.getUser().getUserid();
        String password = userAndPasswords.getPassword1();
        String salt = getNewSalt();
        String hashedPassword = hashPassword(password, salt);
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update users set password=?, password_salt=? where user_id=?")) { // NOSONAR it's hard to administrated passwords without mentioning the word "password"...
                statement.setString(1, hashedPassword);
                statement.setString(2, salt);
                statement.setInt(3, userId);
                int rowcount = statement.executeUpdate();
                if (rowcount < 1) {
                    String message = String.format("Failed to set password: userId %d didn't match any user in the database", userId);
                    logservice.log(LogService.LOG_WARNING, message);
                    throw new AuthserviceException(message);
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to change password for user with id %d", userId);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getUsers();
    }

    @Override
    public List<User> addUser(UserAndPasswords newUserWithPasswords) {
        User addedUser = null;
        try(Connection connection = database.getConnection()) {
            User newUser = newUserWithPasswords.getUser();
            try(PreparedStatement statement = connection.prepareStatement("insert into users (username,email,password, password_salt,firstname,lastname) values (?, ?, 'dummy', 'dummy', ?, ?)")) {
                statement.setString(1, newUser.getUsername());
                statement.setString(2, newUser.getEmail());
                statement.setString(3, newUser.getFirstname());
                statement.setString(4, newUser.getLastname());
                statement.executeUpdate();
            }

            try(PreparedStatement statement = connection.prepareStatement("select * from users where username=? order by user_id")) {
                statement.setString(1, newUser.getUsername());
                try (ResultSet results = statement.executeQuery()) {
                    if (results.next()) {
                        addedUser = unpackUser(results);
                    } else {
                        throw new SQLException("Couldn't find inserted user in the database");
                    }
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to add user with username %s", newUserWithPasswords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        UserAndPasswords passwords = new UserAndPasswords(addedUser, newUserWithPasswords.getPassword1(), newUserWithPasswords.getPassword2(), false);
        return updatePassword(passwords);
    }

    @Override
    public List<Role> getRoles() {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from roles order by role_id")) {
                try(ResultSet results = statement.executeQuery()) {
                    List<Role> roles = new ArrayList<>();
                    while(results.next()) {
                        int id = results.getInt(1);
                        String rolename = results.getString(2);
                        String description = results.getString(3);
                        Role role = new Role(id, rolename, description);
                        roles.add(role);
                    }

                    return roles;
                }
            }
        } catch (SQLException e) {
            String message = "UserManagmentService failed to get the list of roles";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public List<Role> modifyRole(Role role) {
        int roleid = role.getId();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update roles set role_name=?, description=? where role_id=?")) {
                statement.setString(1, role.getRolename());
                statement.setString(2, role.getDescription());
                statement.setInt(3, roleid);
                int updated = statement.executeUpdate();
                if (updated < 1) {
                    logservice.log(LogService.LOG_WARNING, String.format("No rows were changed when updating role with id %d", roleid));
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to modify role with id %d", roleid);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getRoles();
    }

    @Override
    public List<Role> addRole(Role newRole) {
        String rolename = newRole.getRolename();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into roles (role_name, description) values (?, ?)")) {
                statement.setString(1, rolename);
                statement.setString(2, newRole.getDescription());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to add role with rolename %s", rolename);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getRoles();
    }

    @Override
    public List<Permission> getPermissions() {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from permissions order by permission_id")) {
                try(ResultSet results = statement.executeQuery()) {
                    List<Permission> permissions = new ArrayList<>();
                    while(results.next()) {
                        int id = results.getInt(1);
                        String permissionname = results.getString(2);
                        String description = results.getString(3);
                        Permission role = new Permission(id, permissionname, description);
                        permissions.add(role);
                    }

                    return permissions;
                }
            }
        } catch (SQLException e) {
            String message = "UserManagmentService failed to get the list of permissions";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public List<Permission> modifyPermission(Permission permission) {
        int permissionid = permission.getId();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update permissions set permission_name=?, description=? where permission_id=?")) {
                statement.setString(1, permission.getPermissionname());
                statement.setString(2, permission.getDescription());
                statement.setInt(3, permissionid);
                int updated = statement.executeUpdate();
                if (updated < 1) {
                    logservice.log(LogService.LOG_WARNING, String.format("No rows were changed when updating permission with id %d", permissionid));
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to modify permission with id %d", permissionid);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getPermissions();
    }

    @Override
    public List<Permission> addPermission(Permission newPermission) {
        String permissionname = newPermission.getPermissionname();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into permissions (permission_name, description) values (?, ?)")) {
                statement.setString(1, permissionname);
                statement.setString(2, newPermission.getDescription());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to add permission with permission name %s", permissionname);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getPermissions();
    }

    @Override
    public Map<String, List<Role>> getUserRoles() {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from users join user_roles on user_roles.username=users.username join roles on roles.role_name=user_roles.role_name")) {
                try(ResultSet results = statement.executeQuery()) {
                    Map<String, List<Role>> userroles = new HashMap<>();
                    while(results.next()) {
                        User user = unpackUser(results);
                        int id = results.getInt(11);
                        String rolename = results.getString(12);
                        String description = results.getString(13);
                        Role role = new Role(id, rolename, description);
                        addRoleToMap(userroles, user, role);
                    }

                    return userroles;
                }
            }
        } catch (SQLException e) {
            String message = "UserManagmentService failed to get the user to role mappings";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public Map<String, List<Role>> addUserRoles(UserRoles userroles) {
        User user = userroles.getUser();
        List<Role> roles = userroles.getRoles();
        Set<String> existingRoles = findExistingRolesForUser(user);
        try(Connection connection = database.getConnection()) {
            List<Role> rolesNotAlreadyOnUser = roles.stream().filter(r -> !existingRoles.contains(r.getRolename())).collect(Collectors.toList());
            for (Role role : rolesNotAlreadyOnUser) {
                try(PreparedStatement statement = connection.prepareStatement("insert into user_roles (role_name, username) values (?, ?)")) {
                    statement.setString(1, role.getRolename());
                    statement.setString(2, user.getUsername());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to add roles to user %s", user.getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getUserRoles();
    }

    @Override
    public Map<String, List<Role>> removeUserRoles(UserRoles userroles) {
        User user = userroles.getUser();
        List<Role> roles = userroles.getRoles();
        if (!roles.isEmpty()) {
            String roleSet = roles.stream().map(r -> "\'" + r.getRolename() + "\'").collect(Collectors.joining(","));
            String sql = String.format("delete from user_roles where username=? and role_name in (%s)", roleSet);
            try(Connection connection = database.getConnection()) {
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, user.getUsername());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                String message = String.format("UserManagmentService failed to delete roles from user %s", user.getUsername());
                logservice.log(LogService.LOG_ERROR, message, e);
                throw new AuthserviceException(message, e);
            }
        }

        return getUserRoles();
    }

    @Override
    public Map<String, List<Permission>> getRolesPermissions() {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from roles join roles_permissions on roles_permissions.role_name=roles.role_name join permissions on permissions.permission_name=roles_permissions.permission_name")) {
                try(ResultSet results = statement.executeQuery()) {
                    Map<String, List<Permission>> rolespermissions = new HashMap<>();
                    while(results.next()) {
                        int roleId = results.getInt(1);
                        String rolename = results.getString(2);
                        String roleDescription = results.getString(3);
                        Role role = new Role(roleId, rolename, roleDescription);
                        int permissionId = results.getInt(7);
                        String permissionname = results.getString(8);
                        String permissionDescription = results.getString(9);
                        Permission permission = new Permission(permissionId, permissionname, permissionDescription);
                        addPermissionToMap(rolespermissions, role, permission);
                    }

                    return rolespermissions;
                }
            }
        } catch (SQLException e) {
            String message = "UserManagmentService failed to get the roles to permissions mapping";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public Map<String, List<Permission>> addRolePermissions(RolePermissions rolepermissions) {
        Role role = rolepermissions.getRole();
        List<Permission> permissions = rolepermissions.getPermissions();
        Set<String> existingPermissions = findExistingPermissionsForRole(role);
        try(Connection connection = database.getConnection()) {
            List<Permission> permissionsNotAlreadyOnrole = permissions.stream().filter(p -> !existingPermissions.contains(p.getPermissionname())).collect(Collectors.toList());
            for (Permission permission : permissionsNotAlreadyOnrole) {
                try(PreparedStatement statement = connection.prepareStatement("insert into roles_permissions (role_name, permission_name) values (?, ?)")) {
                    statement.setString(1, role.getRolename());
                    statement.setString(2, permission.getPermissionname());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to add roles to user %s", role.getRolename());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }

        return getRolesPermissions();
    }

    @Override
    public Map<String, List<Permission>> removeRolePermissions(RolePermissions rolepermissions) {
        Role role = rolepermissions.getRole();
        List<Permission> permissions = rolepermissions.getPermissions();
        if (!permissions.isEmpty()) {
            String roleSet = permissions.stream().map(p -> "\'" + p.getPermissionname() + "\'").collect(Collectors.joining(","));
            String sql = String.format("delete from roles_permissions where role_name=? and permission_name in (%s)", roleSet);
            try(Connection connection = database.getConnection()) {
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, role.getRolename());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                String message = String.format("UserManagmentService failed to delete permissions from role %s", role.getRolename());
                logservice.log(LogService.LOG_ERROR, message, e);
                throw new AuthserviceException(message, e);
            }
        }

        return getRolesPermissions();
    }

    void addRoleToMap(Map<String, List<Role>> userroles, User user, Role role) {
        userroles.computeIfAbsent(user.getUsername(), s -> new ArrayList<>());
        userroles.get(user.getUsername()).add(role);
    }

    void addPermissionToMap(Map<String, List<Permission>> rolespermissions, Role role, Permission permission) {
        String rolename = role.getRolename();
        if (!rolespermissions.containsKey(rolename)) {
            rolespermissions.put(rolename, new ArrayList<>());
        }

        rolespermissions.get(role.getRolename()).add(permission);
    }

    Set<String> findExistingRolesForUser(User user) {
        String username = user.getUsername();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select role_name from user_roles where username=?")) {
                statement.setString(1, username);
                try(ResultSet results = statement.executeQuery()) {
                    Set<String> existingroles = new HashSet<>();
                    while(results.next()) {
                        existingroles.add(results.getString(1));
                    }

                    return existingroles;
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to find existing roles for user %s", username);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    Set<String> findExistingPermissionsForRole(Role role) {
        String rolename = role.getRolename();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select permission_name from roles_permissions where role_name=?")) {
                statement.setString(1, rolename);
                try(ResultSet results = statement.executeQuery()) {
                    Set<String> existingpermissions = new HashSet<>();
                    while(results.next()) {
                        existingpermissions.add(results.getString(1));
                    }

                    return existingpermissions;
                }
            }
        } catch (SQLException e) {
            String message = String.format("UserManagmentService failed to find existing permissions for role %s", rolename);
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    static String getNewSalt() {
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toBase64();
    }

    static String hashPassword(String newUserPassword, String salt) {
        Object decodedSaltUsedWhenHashing = Util.bytes(Base64.getDecoder().decode(salt));
        return new Sha256Hash(newUserPassword, decodedSaltUsedWhenHashing, 1024).toBase64();
    }

    User unpackUser(ResultSet results) throws SQLException {
        int id = results.getInt(1);
        String username = results.getString(2);
        String email = results.getString(5);
        String firstname = results.getString(6);
        String lastname = results.getString(7);
        return new User(id, username, email, firstname, lastname);
    }

}
