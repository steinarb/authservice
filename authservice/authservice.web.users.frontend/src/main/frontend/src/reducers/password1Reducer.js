import { createReducer } from '@reduxjs/toolkit';
import {
    PASSWORD1_FIELD_MODIFIED,
    PASSWORDS_CLEAR,
} from '../actiontypes';
import { isUsersLoaded } from '../matchers';
const defaultValue = '';

const password1Reducer = createReducer(defaultValue, builder => {
    builder
        .addCase(PASSWORD1_FIELD_MODIFIED, (state, action) => action.payload)
        .addCase(PASSWORDS_CLEAR, () => defaultValue)
        .addMatcher(isUsersLoaded, () => defaultValue);
});

export default password1Reducer;
