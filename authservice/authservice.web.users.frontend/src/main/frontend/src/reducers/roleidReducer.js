import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLE,
    ROLE_CLEAR,
    SAVE_MODIFIED_ROLE_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_ROLE_RECEIVE,
} from '../actiontypes';

const defaultValue = -1;

const roleidReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_ROLE, (state, action) => action.payload.id)
        .addCase(ROLE_CLEAR, () => defaultValue)
        .addCase(SAVE_MODIFIED_ROLE_RECEIVE, () => defaultValue)
        .addCase(SAVE_PASSWORDS_MODIFY_RECEIVE, () => defaultValue)
        .addCase(SAVE_ADDED_ROLE_RECEIVE, () => defaultValue);
});

export default roleidReducer;
