import {
    emptyUser,
    emptyRole,
} from '../constants';

export function findSelectedUser(e, users) {
    const selectedUserId = parseInt(e.target.value);
    return users.find(u => u.userid === selectedUserId) || emptyUser;
}

export function findSelectedRole(e, roles) {
    const selectedRoleId = parseInt(e.target.value);
    return roles.find(u => u.id === selectedRoleId) || emptyRole;
}
