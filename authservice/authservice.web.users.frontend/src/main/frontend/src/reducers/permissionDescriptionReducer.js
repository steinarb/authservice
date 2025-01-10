import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PERMISSION,
    PERMISSION_DESCRIPTION_FIELD_MODIFIED,
    PERMISSION_CLEAR,
} from '../actiontypes';
import { isUsersLoaded, isPermissionsLoaded } from '../matchers';

const defaultValue = '';

const permissionDescriptionReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_PERMISSION, (state, action) => action.payload.description)
        .addCase(PERMISSION_DESCRIPTION_FIELD_MODIFIED, (state, action) => action.payload)
        .addCase(PERMISSION_CLEAR, () => defaultValue)
        .addMatcher(isPermissionsLoaded, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default permissionDescriptionReducer;
