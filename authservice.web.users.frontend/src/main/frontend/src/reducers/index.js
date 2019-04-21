import { combineReducers } from 'redux';
import { createReducer, createAction } from 'redux-starter-kit';
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
} from './actiontypes';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from './constants';

const usersreceived = createAction(USERS_RECEIVED);

const usersReducer = createReducer([], {
    [usersreceived]: (state, action) => {
        const users = action.payload;
        users.unshift(emptyUser);
        return users;
    },
});


const userupdate = createAction(USER_UPDATE);

const userReducer = createReducer({ ...emptyUser }, {
    [userupdate]: (state, action) => {
        const user = action.payload;
        return user;
    },
});


const userrolesreceived = createAction(USERROLES_RECEIVED);

const userrolesReducer = createReducer([], {
    [userrolesreceived]: (state, action) => {
        const userroles = action.payload;
        return userroles;
    },
});


const passwordsupdate = createAction(PASSWORDS_UPDATE);

const passwordsReducer = createReducer({ ...emptyUserAndPasswords }, {
    [passwordsupdate]: (state, action) => {
        const passwords = action.payload;
        return passwords;
    },
});


const rolesreceived = createAction(ROLES_RECEIVED);

const rolesReducer = createReducer([], {
    [rolesreceived]: (state, action) => {
        const roles = action.payload;
        return roles;
    },
});


const roleupdate = createAction(ROLE_UPDATE);

const roleReducer = createReducer({ ...emptyRole }, {
    [roleupdate]: (state, action) => {
        const role = action.payload;
        return role;
    },
});


const rolepermissionsreceived = createAction(ROLEPERMISSIONS_RECEIVED);

const rolepermissionsReducer = createReducer([], {
    [rolepermissionsreceived]: (state, action) => {
        const rolepermissions = action.payload;
        return rolepermissions;
    },
});


const permissionsreceived = createAction(PERMISSIONS_RECEIVED);

const permissionsReducer = createReducer([], {
    [permissionsreceived]: (state, action) => {
        const permissions = action.payload;
        return permissions;
    },
});


const permissionupdate = createAction(PERMISSION_UPDATE);

const permissionReducer = createReducer({ ...emptyPermission }, {
    [permissionupdate]: (state, action) => {
        const permission = action.payload;
        return permission;
    },
});


const formfieldupdate = createAction(FORMFIELD_UPDATE);

const formfieldReducer = createReducer({}, {
    [formfieldupdate]: (state, action) => {
        const formfield = action.payload;
        return { ...state, ...formfield };
    },
});


const userserrors = createAction(USERS_ERROR);
const userroleserrors = createAction(USERROLES_ERROR);
const roleserrors = createAction(ROLES_ERROR);
const rolepermissionserrors = createAction(ROLEPERMISSIONS_ERROR);
const permissionserrors = createAction(PERMISSIONS_ERROR);

const errorsReducer = createReducer({}, {
    [userserrors]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [userroleserrors]: (state, action) => {
        const userroles = action.payload;
        return { ...state, userroles };
    },
    [roleserrors]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [rolepermissionserrors]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [permissionserrors]: (state, action) => {
        const permissions = action.payload;
        return { ...state, permissions };
    },
});


const rootsReducer = combineReducers({
    users: usersReducer,
    user: userReducer,
    userroles: userrolesReducer,
    passwords: passwordsReducer,
    roles: rolesReducer,
    role: roleReducer,
    rolepermissions: rolepermissionsReducer,
    permissions: permissionsReducer,
    permission: permissionReducer,
    formfield: formfieldReducer,
    errors: errorsReducer,
});

export default rootsReducer;
