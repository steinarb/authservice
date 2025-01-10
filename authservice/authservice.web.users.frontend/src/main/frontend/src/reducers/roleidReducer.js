import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLE,
    ROLE_CLEAR,
} from '../actiontypes';
import { isUsersLoaded, isRolesLoaded } from '../matchers';

const defaultValue = -1;

const roleidReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_ROLE, (state, action) => action.payload.id)
        .addMatcher(isRolesLoaded, () => defaultValue)
        .addCase(ROLE_CLEAR, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default roleidReducer;
