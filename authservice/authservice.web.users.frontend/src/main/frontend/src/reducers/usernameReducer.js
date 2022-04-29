import { createReducer } from '@reduxjs/toolkit';
import {
    SELECTED_USER,
    USERNAME_FIELD_MODIFIED,
    USER_CLEAR,
    SAVE_MODIFIED_USER_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_USER_RECEIVE,
} from '../actiontypes';
import { isUnselected } from './common';

const defaultValue = '';

const usernameReducer = createReducer(defaultValue, {
    [SELECTED_USER]: (state, action) => isUnselected(action.payload.userid) ? defaultValue : action.payload.username,
    [USERNAME_FIELD_MODIFIED]: (state, action) => action.payload,
    [USER_CLEAR]: () => defaultValue,
    [SAVE_MODIFIED_USER_RECEIVE]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
    [SAVE_ADDED_USER_RECEIVE]: () => defaultValue,
});

export default usernameReducer;
