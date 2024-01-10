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

const errorsReducer = createReducer({}, builder => {
    builder
        .addCase(USERS_FAILURE, (state, action) => {
            const users = action.payload;
            return { ...state, users };
        })
        .addCase(USER_ADD_ROLE_FAILURE, (state, action) => {
            const users = action.payload;
            return { ...state, users };
        })
        .addCase(SAVE_MODIFIED_USER_FAILURE, (state, action) => {
            const users = action.payload;
            return { ...state, users };
        })
        .addCase(SAVE_PASSWORDS_MODIFY_FAILURE, (state, action) => {
            const users = action.payload;
            return { ...state, users };
        })
        .addCase(SAVE_ADDED_USER_FAILURE, (state, action) => {
            const users = action.payload;
            return { ...state, users };
        })
        .addCase(USERROLES_FAILURE, (state, action) => {
            const userroles = action.payload;
            return { ...state, userroles };
        })
        .addCase(ROLES_FAILURE, (state, action) => {
            const roles = action.payload;
            return { ...state, roles };
        })
        .addCase(USER_REMOVE_ROLE_FAILURE, (state, action) => {
            const roles = action.payload;
            return { ...state, roles };
        })
        .addCase(SAVE_MODIFIED_ROLE_FAILURE, (state, action) => {
            const roles = action.payload;
            return { ...state, roles };
        })
        .addCase(SAVE_ADDED_ROLE_FAILURE, (state, action) => {
            const roles = action.payload;
            return { ...state, roles };
        })
        .addCase(ROLEPERMISSIONS_FAILURE, (state, action) => {
            const roles = action.payload;
            return { ...state, roles };
        })
        .addCase(ADD_PERMISSON_TO_ROLE_FAILURE, (state, action) => {
            const rolepermissions = action.payload;
            return { ...state, rolepermissions };
        })
        .addCase(REMOVE_PERMISSON_FROM_ROLE_FAILURE, (state, action) => {
            const rolepermissions = action.payload;
            return { ...state, rolepermissions };
        })
        .addCase(PERMISSIONS_FAILURE, (state, action) => {
            const rolepermissions = action.payload;
            return { ...state, rolepermissions };
        })
        .addCase(SAVE_MODIFIED_PERMISSION_FAILURE, (state, action) => {
            const permissions = action.payload;
            return { ...state, permissions };
        })
        .addCase(SAVE_ADDED_PERMISSION_FAILURE, (state, action) => {
            const permissions = action.payload;
            return { ...state, permissions };
        });
});

export default errorsReducer;
