import { createReducer } from '@reduxjs/toolkit';
import { clearUser } from '../reducers/userSlice';
import { clearRole } from '../reducers/roleSlice';
import { clearPermission } from '../reducers/permissionSlice';
import {
    SET_MODIFY_FAILED_ERROR,
    CLEAR_MODIFY_FAILED_ERROR,
} from '../actiontypes';
import { isUsersLoaded, isRolesLoaded } from '../matchers';

const defaultValue = '';

const modifyFailedErrorReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SET_MODIFY_FAILED_ERROR, (state, action) => action.payload)
        .addMatcher(isUsersLoaded, () => defaultValue)
        .addCase(clearUser, () => defaultValue)
        .addMatcher(isRolesLoaded, () => defaultValue)
        .addCase(clearRole, () => defaultValue)
        .addCase(clearPermission, () => defaultValue)
        .addCase(CLEAR_MODIFY_FAILED_ERROR, () => defaultValue);
});

export default modifyFailedErrorReducer;
