import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PERMISSION,
    PERMISSION_CLEAR,
    SAVE_MODIFIED_PERMISSION_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_PERMISSION_RECEIVE,
} from '../actiontypes';

const defaultValue = -1;

const permissionidReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_PERMISSION, (state, action) => action.payload.id)
        .addCase(PERMISSION_CLEAR, () => defaultValue)
        .addCase(SAVE_MODIFIED_PERMISSION_RECEIVE, () => defaultValue)
        .addCase(SAVE_PASSWORDS_MODIFY_RECEIVE, () => defaultValue)
        .addCase(SAVE_ADDED_PERMISSION_RECEIVE, () => defaultValue);
});

export default permissionidReducer;
