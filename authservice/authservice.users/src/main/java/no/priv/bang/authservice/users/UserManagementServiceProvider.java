/*
 * Copyright 2019-2024 Steinar Bang
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

import javax.sql.DataSource;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.lang.util.ByteSource.Util;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

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
    private Logger logger;
    private DataSource datasource;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/authservice)")
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Activate
    public void activate() {
        // Called after all injections have been satisfied
    }

    @Override
    public User getUser(String username) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from users where username=?")) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return unpackUser(results);
                    }

                    var message = String.format("User \"%s\" not found", username);
                    logger.warn(message);
                    throw new AuthserviceException(message);
                }
            }
        } catch (SQLException e) {
            var message = String.format("Unable to fetch user \"%s\" from the database", username);
            logger.error(message);
            throw new AuthserviceException(message);
        }
    }

    @Override
    public List<Role> getRolesForUser(String username) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select distinct r.* from roles r join user_roles ur on ur.role_name=r.role_name where ur.username=?")) {
                statement.setString(1, username);
                return getRolesFromQuery(statement);
            }
        } catch (SQLException e) {
            var message = String.format("Unable to fetch roles for user \"%s\" from the database", username);
            logger.error(message);
            throw new AuthserviceException(message);
        }
    }

    @Override
    public List<Permission> getPermissionsForUser(String username) {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select distinct p.* from permissions p join roles_permissions rp on rp.permission_name=p.permission_name join user_roles up on rp.role_name=up.role_name where up.username=?")) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    var permissions = new ArrayList<Permission>();
                    while(results.next()) {
                        var permission = Permission.with()
                            .id(results.getInt(1))
                            .permissionname(results.getString(2))
                            .description(results.getString(3))
                            .build();
                        permissions.add(permission);
                    }

                    return permissions;
                }
            }
        } catch (SQLException e) {
            var message = String.format("Unable to fetch roles for user \"%s\" from the database", username);
            logger.error(message);
            throw new AuthserviceException(message);
        }
    }

    @Override
    public List<User> getUsers() {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from users order by user_id")) {
                var users = new ArrayList<User>();
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        var user = unpackUser(results);
                        users.add(user);
                    }
                }

                return users;
            }
        } catch (SQLException e) {
            var message = "UserManagmentService failed to get the list of users";
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public List<User> modifyUser(User user) {
        try(Connection connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update users set username=?, email=?, firstname=?, lastname=? where user_id=?")) {
                statement.setString(1, user.username());
                statement.setString(2, user.email());
                statement.setString(3, user.firstname());
                statement.setString(4, user.lastname());
                statement.setInt(5, user.userid());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to update user with id %d", user.userid());
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getUsers();
    }

    @Override
    public List<User> updatePassword(UserAndPasswords userAndPasswords) {
        if (userAndPasswords.password1() == null || userAndPasswords.password1().isEmpty()) {
            var message = "Failed to set password: Password can't be empty";
            logger.warn(message);
            throw new AuthservicePasswordEmptyException(message);
        }

        if (userAndPasswords.user() == null) {
            var message = "Failed to set password: User can't be empty";
            logger.warn(message);
            throw new AuthservicePasswordEmptyException(message);
        }

        if (!userAndPasswords.password1().equals(userAndPasswords.password2())) {
            var message = "Failed to set password: Passwords not identical";
            logger.warn(message);
            throw new AuthservicePasswordsNotIdenticalException(message);
        }

        var userId = userAndPasswords.user().userid();
        var password = userAndPasswords.password1();
        var salt = getNewSalt();
        var hashedPassword = hashPassword(password, salt);
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update users set password=?, password_salt=? where user_id=?")) { // NOSONAR it's hard to administrated passwords without mentioning the word "password"...
                statement.setString(1, hashedPassword);
                statement.setString(2, salt);
                statement.setInt(3, userId);
                var rowcount = statement.executeUpdate();
                if (rowcount < 1) {
                    var message = String.format("Failed to set password: userId %d didn't match any user in the database", userId);
                    logger.warn(message);
                    throw new AuthserviceException(message);
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to change password for user with id %d", userId);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getUsers();
    }

    @Override
    public List<User> addUser(UserAndPasswords newUserWithPasswords) {
        User addedUser = null;
        try(var connection = datasource.getConnection()) {
            var newUser = newUserWithPasswords.user();
            try(var statement = connection.prepareStatement("insert into users (username,email,password, password_salt,firstname,lastname) values (?, ?, 'dummy', 'dummy', ?, ?)")) {
                statement.setString(1, newUser.username());
                statement.setString(2, newUser.email());
                statement.setString(3, newUser.firstname());
                statement.setString(4, newUser.lastname());
                statement.executeUpdate();
            }

            try(var statement = connection.prepareStatement("select * from users where username=? order by user_id")) {
                statement.setString(1, newUser.username());
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        addedUser = unpackUser(results);
                    } else {
                        throw new SQLException("Couldn't find inserted user in the database");
                    }
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to add user with username %s", newUserWithPasswords.user().username());
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        var passwords = UserAndPasswords.with()
            .user(addedUser)
            .password1(newUserWithPasswords.password1())
            .password2(newUserWithPasswords.password2())
            .build();
        return updatePassword(passwords);
    }

    @Override
    public List<Role> getRoles() {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from roles order by role_id")) {
                return getRolesFromQuery(statement);
            }
        } catch (SQLException e) {
            var message = "UserManagmentService failed to get the list of roles";
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public List<Role> modifyRole(Role role) {
        var roleid = role.id();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update roles set role_name=?, description=? where role_id=?")) {
                statement.setString(1, role.rolename());
                statement.setString(2, role.description());
                statement.setInt(3, roleid);
                var updated = statement.executeUpdate();
                if (updated < 1) {
                    logger.warn(String.format("No rows were changed when updating role with id %d", roleid));
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to modify role with id %d", roleid);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getRoles();
    }

    @Override
    public List<Role> addRole(Role newRole) {
        var rolename = newRole.rolename();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into roles (role_name, description) values (?, ?)")) {
                statement.setString(1, rolename);
                statement.setString(2, newRole.description());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to add role with rolename %s", rolename);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getRoles();
    }

    @Override
    public List<Permission> getPermissions() {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from permissions order by permission_id")) {
                try(var results = statement.executeQuery()) {
                    var permissions = new ArrayList<Permission>();
                    while(results.next()) {
                        var role = Permission.with()
                            .id(results.getInt(1))
                            .permissionname(results.getString(2))
                            .description(results.getString(3))
                            .build();
                        permissions.add(role);
                    }

                    return permissions;
                }
            }
        } catch (SQLException e) {
            var message = "UserManagmentService failed to get the list of permissions";
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public List<Permission> modifyPermission(Permission permission) {
        var permissionid = permission.id();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update permissions set permission_name=?, description=? where permission_id=?")) {
                statement.setString(1, permission.permissionname());
                statement.setString(2, permission.description());
                statement.setInt(3, permissionid);
                var updated = statement.executeUpdate();
                if (updated < 1) {
                    logger.warn(String.format("No rows were changed when updating permission with id %d", permissionid));
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to modify permission with id %d", permissionid);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getPermissions();
    }

    @Override
    public List<Permission> addPermission(Permission newPermission) {
        var permissionname = newPermission.permissionname();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into permissions (permission_name, description) values (?, ?)")) {
                statement.setString(1, permissionname);
                statement.setString(2, newPermission.description());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to add permission with permission name %s", permissionname);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getPermissions();
    }

    @Override
    public Map<String, List<Role>> getUserRoles() {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from users join user_roles on user_roles.username=users.username join roles on roles.role_name=user_roles.role_name")) {
                try(var results = statement.executeQuery()) {
                    var userroles = new HashMap<String, List<Role>>();
                    while(results.next()) {
                        var user = unpackUser(results);
                        Role role = Role.with()
                            .id(results.getInt(11))
                            .rolename(results.getString(12))
                            .description(results.getString(13))
                            .build();
                        addRoleToMap(userroles, user, role);
                    }

                    return userroles;
                }
            }
        } catch (SQLException e) {
            var message = "UserManagmentService failed to get the user to role mappings";
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public Map<String, List<Role>> addUserRoles(UserRoles userroles) {
        var user = userroles.user();
        var roles = userroles.roles();
        var existingRoles = findExistingRolesForUser(user);
        try(var connection = datasource.getConnection()) {
            var rolesNotAlreadyOnUser = roles.stream().filter(r -> !existingRoles.contains(r.rolename())).toList();
            for (var role : rolesNotAlreadyOnUser) {
                try(var statement = connection.prepareStatement("insert into user_roles (role_name, username) values (?, ?)")) {
                    statement.setString(1, role.rolename());
                    statement.setString(2, user.username());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to add roles to user %s", user.username());
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getUserRoles();
    }

    @Override
    public Map<String, List<Role>> removeUserRoles(UserRoles userroles) {
        var user = userroles.user();
        var roles = userroles.roles();
        if (!roles.isEmpty()) {
            var roleSet = roles.stream().map(r -> "\'" + r.rolename() + "\'").collect(Collectors.joining(","));
            var sql = String.format("delete from user_roles where username=? and role_name in (%s)", roleSet);
            try(var connection = datasource.getConnection()) {
                try(var statement = connection.prepareStatement(sql)) {
                    statement.setString(1, user.username());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                var message = String.format("UserManagmentService failed to delete roles from user %s", user.username());
                logger.error(message, e);
                throw new AuthserviceException(message, e);
            }
        }

        return getUserRoles();
    }

    @Override
    public Map<String, List<Permission>> getRolesPermissions() {
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from roles join roles_permissions on roles_permissions.role_name=roles.role_name join permissions on permissions.permission_name=roles_permissions.permission_name")) {
                try(var results = statement.executeQuery()) {
                    var rolespermissions = new HashMap<String, List<Permission>>();
                    while(results.next()) {
                        var role = Role.with()
                            .id(results.getInt(1))
                            .rolename(results.getString(2))
                            .description(results.getString(3))
                            .build();
                        var permission = Permission.with()
                            .id(results.getInt(7))
                            .permissionname(results.getString(8))
                            .description(results.getString(9))
                            .build();
                        addPermissionToMap(rolespermissions, role, permission);
                    }

                    return rolespermissions;
                }
            }
        } catch (SQLException e) {
            var message = "UserManagmentService failed to get the roles to permissions mapping";
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    @Override
    public Map<String, List<Permission>> addRolePermissions(RolePermissions rolepermissions) {
        var role = rolepermissions.role();
        var permissions = rolepermissions.permissions();
        var existingPermissions = findExistingPermissionsForRole(role);
        try(var connection = datasource.getConnection()) {
            var permissionsNotAlreadyOnrole = permissions.stream().filter(p -> !existingPermissions.contains(p.permissionname())).toList();
            for (var permission : permissionsNotAlreadyOnrole) {
                try(var statement = connection.prepareStatement("insert into roles_permissions (role_name, permission_name) values (?, ?)")) {
                    statement.setString(1, role.rolename());
                    statement.setString(2, permission.permissionname());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to add roles to user %s", role.rolename());
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }

        return getRolesPermissions();
    }

    @Override
    public Map<String, List<Permission>> removeRolePermissions(RolePermissions rolepermissions) {
        var role = rolepermissions.role();
        var permissions = rolepermissions.permissions();
        if (!permissions.isEmpty()) {
            var roleSet = permissions.stream().map(p -> "\'" + p.permissionname() + "\'").collect(Collectors.joining(","));
            var sql = String.format("delete from roles_permissions where role_name=? and permission_name in (%s)", roleSet);
            try(var connection = datasource.getConnection()) {
                try(var statement = connection.prepareStatement(sql)) {
                    statement.setString(1, role.rolename());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                var message = String.format("UserManagmentService failed to delete permissions from role %s", role.rolename());
                logger.error(message, e);
                throw new AuthserviceException(message, e);
            }
        }

        return getRolesPermissions();
    }

    void addRoleToMap(Map<String, List<Role>> userroles, User user, Role role) {
        userroles.computeIfAbsent(user.username(), s -> new ArrayList<>());
        userroles.get(user.username()).add(role);
    }

    void addPermissionToMap(Map<String, List<Permission>> rolespermissions, Role role, Permission permission) {
        var rolename = role.rolename();
        rolespermissions.computeIfAbsent(rolename, k -> new ArrayList<Permission>());
        rolespermissions.get(role.rolename()).add(permission);
    }

    Set<String> findExistingRolesForUser(User user) {
        var username = user.username();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select role_name from user_roles where username=?")) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    var existingroles = new HashSet<String>();
                    while(results.next()) {
                        existingroles.add(results.getString(1));
                    }

                    return existingroles;
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to find existing roles for user %s", username);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    Set<String> findExistingPermissionsForRole(Role role) {
        var rolename = role.rolename();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select permission_name from roles_permissions where role_name=?")) {
                statement.setString(1, rolename);
                try(var results = statement.executeQuery()) {
                    var existingpermissions = new HashSet<String>();
                    while(results.next()) {
                        existingpermissions.add(results.getString(1));
                    }

                    return existingpermissions;
                }
            }
        } catch (SQLException e) {
            var message = String.format("UserManagmentService failed to find existing permissions for role %s", rolename);
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    static String getNewSalt() {
        var randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toBase64();
    }

    static String hashPassword(String newUserPassword, String salt) {
        var decodedSaltUsedWhenHashing = Util.bytes(Base64.getDecoder().decode(salt));
        return new Sha256Hash(newUserPassword, decodedSaltUsedWhenHashing, 1024).toBase64();
    }

    User unpackUser(ResultSet results) throws SQLException {
        return User.with()
            .userid(results.getInt(1))
            .username(results.getString(2))
            .email(results.getString(5))
            .firstname(results.getString(6))
            .lastname(results.getString(7))
            .build();
    }

    List<Role> getRolesFromQuery(PreparedStatement statement) throws SQLException {
        try(var results = statement.executeQuery()) {
            var roles = new ArrayList<Role>();
            while(results.next()) {
                var role = Role.with()
                    .id(results.getInt(1))
                    .rolename(results.getString(2))
                    .description(results.getString(3))
                    .build();
                roles.add(role);
            }

            return roles;
        }
    }

}
