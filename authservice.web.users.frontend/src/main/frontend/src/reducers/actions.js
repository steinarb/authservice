import { createAction } from 'redux-starter-kit';
import {
    USERS_RECEIVED,
    USERS_ERROR,
    USER_UPDATE,
    USERROLES_RECEIVED,
    USERROLES_ERROR,
    PASSWORDS_UPDATE,
    ROLES_RECEIVED,
    ROLES_ERROR,
    ROLE_UPDATE,
    ROLEPERMISSIONS_RECEIVED,
    ROLEPERMISSIONS_ERROR,
    PERMISSIONS_RECEIVED,
    PERMISSIONS_ERROR,
    PERMISSION_UPDATE,
    FORMFIELD_UPDATE,
} from '../actiontypes';

export const usersreceived = createAction(USERS_RECEIVED);
export const userupdate = createAction(USER_UPDATE);
export const userrolesreceived = createAction(USERROLES_RECEIVED);
export const passwordsupdate = createAction(PASSWORDS_UPDATE);
export const rolesreceived = createAction(ROLES_RECEIVED);
export const roleupdate = createAction(ROLE_UPDATE);
export const rolepermissionsreceived = createAction(ROLEPERMISSIONS_RECEIVED);
export const permissionsreceived = createAction(PERMISSIONS_RECEIVED);
export const permissionupdate = createAction(PERMISSION_UPDATE);
export const formfieldupdate = createAction(FORMFIELD_UPDATE);
export const userserrors = createAction(USERS_ERROR);
export const userroleserrors = createAction(USERROLES_ERROR);
export const roleserrors = createAction(ROLES_ERROR);
export const rolepermissionserrors = createAction(ROLEPERMISSIONS_ERROR);
export const permissionserrors = createAction(PERMISSIONS_ERROR);
