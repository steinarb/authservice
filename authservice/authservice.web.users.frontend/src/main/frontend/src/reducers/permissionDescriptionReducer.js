import { createReducer } from '@reduxjs/toolkit';
import {
    SELECTED_PERMISSION,
    PERMISSION_DESCRIPTION_FIELD_MODIFIED,
    PERMISSION_CLEAR,
    SAVE_MODIFIED_PERMISSION_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_PERMISSION_RECEIVE,
} from '../actiontypes';
import { isUnselected } from './common';

const defaultValue = '';

const permissionDescriptionReducer = createReducer(defaultValue, {
    [SELECTED_PERMISSION]: (state, action) => isUnselected(action.payload.id) ? defaultValue : action.payload.description,
    [PERMISSION_DESCRIPTION_FIELD_MODIFIED]: (state, action) => action.payload,
    [PERMISSION_CLEAR]: () => defaultValue,
    [SAVE_MODIFIED_PERMISSION_RECEIVE]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
    [SAVE_ADDED_PERMISSION_RECEIVE]: () => defaultValue,
});

export default permissionDescriptionReducer;
