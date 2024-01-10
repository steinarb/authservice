import { createReducer } from '@reduxjs/toolkit';
import {
    ROLEPERMISSIONS_RECEIVE,
    ADD_PERMISSON_TO_ROLE_RECEIVE,
    REMOVE_PERMISSON_FROM_ROLE_RECEIVE,
} from '../actiontypes';

const rolepermissionsReducer = createReducer([], builder => {
    builder
        .addCase(ROLEPERMISSIONS_RECEIVE, (state, action) => action.payload)
        .addCase(ADD_PERMISSON_TO_ROLE_RECEIVE, (state, action) => action.payload)
        .addCase(REMOVE_PERMISSON_FROM_ROLE_RECEIVE, (state, action) => action.payload);
});

export default rolepermissionsReducer;
