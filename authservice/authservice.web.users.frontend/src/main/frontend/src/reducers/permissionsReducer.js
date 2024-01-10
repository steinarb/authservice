import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSIONS_RECEIVE,
    SAVE_MODIFIED_PERMISSION_RECEIVE,
} from '../actiontypes';

const permissionsReducer = createReducer([], builder => {
    builder
        .addCase(PERMISSIONS_RECEIVE, (state, action) => action.payload)
        .addCase(SAVE_MODIFIED_PERMISSION_RECEIVE, (state, action) => action.payload);
});

export default permissionsReducer;
