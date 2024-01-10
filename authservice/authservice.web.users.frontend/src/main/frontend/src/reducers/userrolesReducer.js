import { createReducer } from '@reduxjs/toolkit';
import {
    USERROLES_RECEIVE,
    USER_ADD_ROLE_RECEIVE,
    USER_REMOVE_ROLE_RECEIVE,
} from '../actiontypes';

const userrolesReducer = createReducer([], builder => {
    builder
        .addCase(USERROLES_RECEIVE, (state, action) => action.payload)
        .addCase(USER_ADD_ROLE_RECEIVE, (state, action) => action.payload)
        .addCase(USER_REMOVE_ROLE_RECEIVE, (state, action) => action.payload);
});

export default userrolesReducer;
