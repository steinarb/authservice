import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PERMISSION,
    PERMISSION_CLEAR,
    SAVE_MODIFIED_PERMISSION_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_PERMISSION_RECEIVE,
} from '../actiontypes';

const defaultValue = -1;

const permissionidReducer = createReducer(defaultValue, {
    [SELECT_PERMISSION]: (state, action) => action.payload,
    [PERMISSION_CLEAR]: () => defaultValue,
    [SAVE_MODIFIED_PERMISSION_RECEIVE]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
    [SAVE_ADDED_PERMISSION_RECEIVE]: () => defaultValue,
});

export default permissionidReducer;
