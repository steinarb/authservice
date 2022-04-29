import { createReducer } from '@reduxjs/toolkit';
import {
    USERS_FAILURE,
    USER_ADD_ROLE_FAILURE,
    SAVE_MODIFIED_USER_FAILURE,
    SAVE_PASSWORDS_MODIFY_FAILURE,
    SAVE_ADDED_USER_FAILURE,
    USERROLES_FAILURE,
    ROLES_FAILURE,
    USER_REMOVE_ROLE_FAILURE,
    SAVE_MODIFIED_ROLE_FAILURE,
    SAVE_ADDED_ROLE_FAILURE,
    ROLEPERMISSIONS_FAILURE,
    ADD_PERMISSON_TO_ROLE_FAILURE,
    REMOVE_PERMISSON_FROM_ROLE_FAILURE,
    PERMISSIONS_FAILURE,
    SAVE_MODIFIED_PERMISSION_FAILURE,
    SAVE_ADDED_PERMISSION_FAILURE,
} from '../actiontypes';

const errorsReducer = createReducer({}, {
    [USERS_FAILURE]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [USER_ADD_ROLE_FAILURE]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [SAVE_MODIFIED_USER_FAILURE]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [SAVE_PASSWORDS_MODIFY_FAILURE]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [SAVE_ADDED_USER_FAILURE]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [USERROLES_FAILURE]: (state, action) => {
        const userroles = action.payload;
        return { ...state, userroles };
    },
    [ROLES_FAILURE]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [USER_REMOVE_ROLE_FAILURE]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [SAVE_MODIFIED_ROLE_FAILURE]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [SAVE_ADDED_ROLE_FAILURE]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [ROLEPERMISSIONS_FAILURE]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [ADD_PERMISSON_TO_ROLE_FAILURE]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [REMOVE_PERMISSON_FROM_ROLE_FAILURE]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [PERMISSIONS_FAILURE]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [SAVE_MODIFIED_PERMISSION_FAILURE]: (state, action) => {
        const permissions = action.payload;
        return { ...state, permissions };
    },
    [SAVE_ADDED_PERMISSION_FAILURE]: (state, action) => {
        const permissions = action.payload;
        return { ...state, permissions };
    },
});

export default errorsReducer;
