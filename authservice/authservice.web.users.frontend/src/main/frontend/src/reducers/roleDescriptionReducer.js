import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLE,
    ROLE_DESCRIPTION_FIELD_MODIFIED,
    ROLE_CLEAR,
} from '../actiontypes';
import { isUsersLoaded, isRolesLoaded } from '../matchers';

const defaultValue = '';

const roleDescriptionReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_ROLE, (state, action) => action.payload.description)
        .addCase(ROLE_DESCRIPTION_FIELD_MODIFIED, (state, action) => action.payload)
        .addMatcher(isRolesLoaded, () => defaultValue)
        .addCase(ROLE_CLEAR, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default roleDescriptionReducer;
