import {
    emptyUser,
    emptyRole,
    emptyPermission,
} from '../constants';

export function findSelectedUser(e, users) {
    const selectedUserId = parseInt(e.target.value);
    return users.find(u => u.userid === selectedUserId) || emptyUser;
}

export function findSelectedRole(e, roles) {
    const selectedRoleId = parseInt(e.target.value);
    return roles.find(u => u.id === selectedRoleId) || emptyRole;
}

export function findSelectedPermission(e, permissions) {
    const selectedPermissionId = parseInt(e.target.value);
    return permissions.find(u => u.id === selectedPermissionId) || emptyPermission;
}
