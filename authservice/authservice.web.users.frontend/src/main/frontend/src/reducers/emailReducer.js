import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    EMAIL_FIELD_MODIFIED,
    USER_CLEAR,
    SAVE_MODIFIED_USER_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_USER_RECEIVE,
} from '../actiontypes';

const defaultValue = '';

const emailReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_USER, (state, action) => action.payload.email)
        .addCase(EMAIL_FIELD_MODIFIED, (state, action) => action.payload)
        .addCase(USER_CLEAR, () => defaultValue)
        .addCase(SAVE_MODIFIED_USER_RECEIVE, () => defaultValue)
        .addCase(SAVE_PASSWORDS_MODIFY_RECEIVE, () => defaultValue)
        .addCase(SAVE_ADDED_USER_RECEIVE, () => defaultValue);
});

export default emailReducer;
