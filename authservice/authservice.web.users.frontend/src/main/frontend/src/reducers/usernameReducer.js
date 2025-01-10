import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    USERNAME_FIELD_MODIFIED,
    USER_CLEAR,
} from '../actiontypes';
import { isUsersLoaded } from '../matchers';

const defaultValue = '';

const usernameReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_USER, (state, action) => action.payload.username)
        .addCase(USERNAME_FIELD_MODIFIED, (state, action) => action.payload)
        .addMatcher(isUsersLoaded, () => defaultValue)
        .addCase(USER_CLEAR, () => defaultValue);
});

export default usernameReducer;
