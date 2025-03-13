import { isAnyOf } from '@reduxjs/toolkit';
import { api } from './api';

export const isConfigurationLoaded = isAnyOf(
    api.endpoints.getConfig.matchFulfilled,
    api.endpoints.postConfigModify.matchFulfilled,
);

export const isUsersLoaded = isAnyOf(
    api.endpoints.getUsers.matchFulfilled,
    api.endpoints.postUserModify.matchFulfilled,
    api.endpoints.postPasswordUpdate.matchFulfilled,
    api.endpoints.postUserAdd.matchFulfilled,
);

export const isUserRolesLoaded = isAnyOf(
    api.endpoints.getUserRoles.matchFulfilled,
    api.endpoints.postUserAddroles.matchFulfilled,
    api.endpoints.postUserRemoveroles.matchFulfilled,
);

export const isRolesLoaded = isAnyOf(
    api.endpoints.getRoles.matchFulfilled,
    api.endpoints.postRoleModify.matchFulfilled,
    api.endpoints.postRoleAdd.matchFulfilled,
);

export const isRolePermissionsLoaded = isAnyOf(
    api.endpoints.getRolePermissions.matchFulfilled,
    api.endpoints.postRoleAddpermissions.matchFulfilled,
    api.endpoints.postRoleRemovepermissions.matchFulfilled,
);

export const isPermissionsLoaded = isAnyOf(
    api.endpoints.getPermissions.matchFulfilled,
    api.endpoints.postPermissionModify.matchFulfilled,
    api.endpoints.postPermissionAdd.matchFulfilled,
);

export const isUsersFailure = isAnyOf(
    api.endpoints.getUsers.matchRejected,
    api.endpoints.postUserModify.matchRejected,
    api.endpoints.postPasswordUpdate.matchRejected,
    api.endpoints.postUserAdd.matchRejected,
);

export const isUserRolesFailure = isAnyOf(
    api.endpoints.getUserRoles.matchRejected,
    api.endpoints.postUserAddroles.matchRejected,
    api.endpoints.postUserRemoveroles.matchRejected,
);

export const isRolesFailure = isAnyOf(
    api.endpoints.getRoles.matchRejected,
    api.endpoints.postRoleModify.matchRejected,
    api.endpoints.postRoleAdd.matchRejected,
);

export const isRolePermissionsFailure = isAnyOf(
    api.endpoints.getRolePermissions.matchRejected,
    api.endpoints.postRoleAddpermissions.matchRejected,
    api.endpoints.postRoleRemovepermissions.matchRejected,
);

export const isPermissionsFailure = isAnyOf(
    api.endpoints.getPermissions.matchRejected,
    api.endpoints.postPermissionModify.matchRejected,
    api.endpoints.postPermissionAdd.matchRejected,
);

export const isFailedRequest = isAnyOf(
    isUsersFailure,
    isUserRolesFailure,
    isRolesFailure,
    isRolePermissionsFailure,
    isPermissionsFailure,
);
