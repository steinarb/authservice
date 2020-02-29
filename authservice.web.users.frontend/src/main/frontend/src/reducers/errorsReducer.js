import { createReducer } from '@reduxjs/toolkit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import {
    USERS_ERROR,
    USERROLES_ERROR,
    ROLES_ERROR,
    ROLEPERMISSIONS_ERROR,
    PERMISSIONS_ERROR,
} from '../actiontypes';

const errorsReducer = createReducer({}, {
    [USERS_ERROR]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [USERROLES_ERROR]: (state, action) => {
        const userroles = action.payload;
        return { ...state, userroles };
    },
    [ROLES_ERROR]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [ROLEPERMISSIONS_ERROR]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [PERMISSIONS_ERROR]: (state, action) => {
        const permissions = action.payload;
        return { ...state, permissions };
    },
});

export default errorsReducer;
