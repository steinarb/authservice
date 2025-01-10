import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    LASTNAME_FIELD_MODIFIED,
    USER_CLEAR,
} from '../actiontypes';
import { isUsersLoaded } from '../matchers';

const defaultValue = '';

const lastnameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_USER, (state, action) => action.payload.lastname)
        .addCase(LASTNAME_FIELD_MODIFIED, (state, action) => action.payload)
        .addMatcher(isUsersLoaded, () => defaultValue)
        .addCase(USER_CLEAR, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default lastnameReducer;
