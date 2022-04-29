import { createReducer } from '@reduxjs/toolkit';
import {
    PASSWORD2_FIELD_MODIFIED,
    PASSWORDS_CLEAR,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_ADDED_USER_RECEIVE,
} from '../actiontypes';
const defaultValue = '';

const password2Reducer = createReducer(defaultValue, {
    [PASSWORD2_FIELD_MODIFIED]: (state, action) => action.payload,
    [PASSWORDS_CLEAR]: () => defaultValue,
    [SAVE_PASSWORDS_MODIFY_RECEIVE]: () => defaultValue,
    [SAVE_ADDED_USER_RECEIVE]: () => defaultValue,
});

export default password2Reducer;
