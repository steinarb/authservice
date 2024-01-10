import { createReducer } from '@reduxjs/toolkit';
import {
    PASSWORD1_FIELD_MODIFIED,
    PASSWORDS_CLEAR,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_USER_RECEIVE,
} from '../actiontypes';
const defaultValue = '';

const password1Reducer = createReducer(defaultValue, builder => {
    builder
        .addCase(PASSWORD1_FIELD_MODIFIED, (state, action) => action.payload)
        .addCase(PASSWORDS_CLEAR, () => defaultValue)
        .addCase(SAVE_PASSWORDS_MODIFY_RECEIVE, () => defaultValue)
        .addCase(SAVE_ADDED_USER_RECEIVE, () => defaultValue);
});

export default password1Reducer;
