import { createReducer } from '@reduxjs/toolkit';
import { isUsersFailure, isUserRolesFailure, isRolesFailure, isRolePermissionsFailure, isPermissionsFailure } from '../matchers';

const errorsReducer = createReducer({}, builder => {
    builder
        .addMatcher(isUsersFailure, (state, action) => {
            const users = action.payload;
            return { ...state, users };
        })
        .addMatcher(isUserRolesFailure, (state, action) => {
            const userroles = action.payload;
            return { ...state, userroles };
        })
        .addMatcher(isRolesFailure, (state, action) => {
            const roles = action.payload;
            return { ...state, roles };
        })
        .addMatcher(isRolePermissionsFailure, (state, action) => {
            const rolepermissions = action.payload;
            return { ...state, rolepermissions };
        })
        .addMatcher(isPermissionsFailure, (state, action) => {
            const rolepermissions = action.payload;
            return { ...state, permissions };
        });
});

export default errorsReducer;
