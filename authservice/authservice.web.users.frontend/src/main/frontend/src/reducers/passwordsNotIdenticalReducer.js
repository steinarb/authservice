import { createReducer } from '@reduxjs/toolkit';
import {
    SET_PASSWORDS_NOT_IDENTICAL,
    PASSWORDS_CLEAR,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_USER_RECEIVE,
} from '../actiontypes';
const defaultValue = false;

const passwordsNotIdenticalReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SET_PASSWORDS_NOT_IDENTICAL, (state, action) => action.payload)
        .addCase(PASSWORDS_CLEAR, () => defaultValue)
        .addCase(SAVE_PASSWORDS_MODIFY_RECEIVE, () => defaultValue)
        .addCase(SAVE_ADDED_USER_RECEIVE, () => defaultValue);
});

export default passwordsNotIdenticalReducer;
