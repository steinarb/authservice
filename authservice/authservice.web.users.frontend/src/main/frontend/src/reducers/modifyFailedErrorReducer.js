import { createReducer } from '@reduxjs/toolkit';
import {
    SET_MODIFY_FAILED_ERROR,
    USER_CLEAR,
    ROLE_CLEAR,
    PERMISSION_CLEAR,
    CLEAR_MODIFY_FAILED_ERROR,
} from '../actiontypes';

const defaultValue = '';

const modifyFailedErrorReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SET_MODIFY_FAILED_ERROR, (state, action) => action.payload)
        .addCase(USER_CLEAR, () => defaultValue)
        .addCase(ROLE_CLEAR, () => defaultValue)
        .addCase(PERMISSION_CLEAR, () => defaultValue)
        .addCase(CLEAR_MODIFY_FAILED_ERROR, () => defaultValue);
});

export default modifyFailedErrorReducer;
