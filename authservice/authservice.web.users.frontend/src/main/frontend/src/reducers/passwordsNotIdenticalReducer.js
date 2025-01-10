import { createReducer } from '@reduxjs/toolkit';
import {
    SET_PASSWORDS_NOT_IDENTICAL,
    PASSWORDS_CLEAR,
} from '../actiontypes';
import { isUsersLoaded } from '../matchers';

const defaultValue = false;

const passwordsNotIdenticalReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SET_PASSWORDS_NOT_IDENTICAL, (state, action) => action.payload)
        .addCase(PASSWORDS_CLEAR, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default passwordsNotIdenticalReducer;
