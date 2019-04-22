import { createAction } from 'redux-starter-kit';

export const USERS_RECEIVED = createAction('USERS_RECEIVED');
export const USERS_ERROR = createAction('USERS_ERROR');
export const USER_UPDATE = createAction('USER_UPDATE');
export const USERROLES_RECEIVED = createAction('USERROLES_RECEIVED');
export const USERROLES_ERROR = createAction('USERROLES_ERROR');
export const PASSWORDS_UPDATE = createAction('PASSWORDS_UPDATE');
export const ROLES_RECEIVED = createAction('ROLES_RECEIVED');
export const ROLES_ERROR = createAction('ROLES_ERROR');
export const ROLE_UPDATE = createAction('ROLE_UPDATE');
export const ROLEPERMISSIONS_RECEIVED = createAction('ROLEPERMISSIONS_RECEIVED');
export const ROLEPERMISSIONS_ERROR = createAction('ROLEPERMISSIONS_ERROR');
export const PERMISSIONS_RECEIVED = createAction('PERMISSIONS_RECEIVED');
export const PERMISSIONS_ERROR = createAction('PERMISSIONS_ERROR');
export const PERMISSION_UPDATE = createAction('PERMISSION_UPDATE');
export const FORMFIELD_UPDATE = createAction('FORMFIELD_UPDATE');
