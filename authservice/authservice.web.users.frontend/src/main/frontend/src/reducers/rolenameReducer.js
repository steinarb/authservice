import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLE,
    ROLENAME_FIELD_MODIFIED,
    ROLE_CLEAR,
    SAVE_MODIFIED_ROLE_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_ROLE_RECEIVE,
} from '../actiontypes';

const defaultValue = '';

const rolenameReducer = createReducer(defaultValue, {
    [SELECT_ROLE]: (state, action) => action.payload.rolename,
    [ROLENAME_FIELD_MODIFIED]: (state, action) => action.payload,
    [ROLE_CLEAR]: () => defaultValue,
    [SAVE_MODIFIED_ROLE_RECEIVE]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
    [SAVE_ADDED_ROLE_RECEIVE]: () => defaultValue,
});

export default rolenameReducer;
