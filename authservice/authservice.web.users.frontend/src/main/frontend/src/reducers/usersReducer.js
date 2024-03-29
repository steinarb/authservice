import { createReducer } from '@reduxjs/toolkit';
import {
    USERS_RECEIVE,
    SAVE_MODIFIED_USER_RECEIVE,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
} from '../actiontypes';

const usersReducer = createReducer([], builder => {
    builder
        .addCase(USERS_RECEIVE, (state, action) => action.payload)
        .addCase(SAVE_MODIFIED_USER_RECEIVE, (state, action) => action.payload)
        .addCase(SAVE_PASSWORDS_MODIFY_RECEIVE, (state, action) => action.payload);
});

export default usersReducer;
