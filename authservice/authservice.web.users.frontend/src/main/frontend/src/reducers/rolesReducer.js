import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_RECEIVE,
    SAVE_MODIFIED_ROLE_RECEIVE,
    SAVE_ADDED_ROLE_RECEIVE,
} from '../actiontypes';

const rolesReducer = createReducer([], {
    [ROLES_RECEIVE]: (state, action) => action.payload,
    [SAVE_MODIFIED_ROLE_RECEIVE]: (state, action) => action.payload,
    [SAVE_ADDED_ROLE_RECEIVE]: (state, action) => action.payload,
});

export default rolesReducer;
