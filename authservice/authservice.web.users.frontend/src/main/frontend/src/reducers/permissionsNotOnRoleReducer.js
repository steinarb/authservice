import { createReducer } from '@reduxjs/toolkit';
import {
    SET_PERMISSIONS_NOT_ON_ROLE,
    SELECT_ROLE,
} from '../actiontypes';
import { isUnselected } from './common';
const defaultValue = [];

const permissionsNotOnRoleReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SET_PERMISSIONS_NOT_ON_ROLE, (state, action) => action.payload)
        .addCase(SELECT_ROLE, (state, action) => isUnselected(action.payload.id) ? defaultValue : state);
});

export default permissionsNotOnRoleReducer;
