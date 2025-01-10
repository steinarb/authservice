import { createReducer } from '@reduxjs/toolkit';
import { SELECT_PERMISSION, PERMISSION_CLEAR } from '../actiontypes';
import { isUsersLoaded, isPermissionsLoaded } from '../matchers';

const defaultValue = -1;

const permissionidReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_PERMISSION, (state, action) => action.payload.id)
        .addCase(PERMISSION_CLEAR, () => defaultValue)
        .addMatcher(isPermissionsLoaded, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default permissionidReducer;
