import { createReducer } from '@reduxjs/toolkit';
import {
    SET_ROLES_ON_USER,
    SELECT_USER,
} from '../actiontypes';
import { isUnselected } from './common';
const defaultValue = [];

const rolesOnUserReducer = createReducer([], builder => {
    builder
        .addCase(SET_ROLES_ON_USER, (state, action) => action.payload)
        .addCase(SELECT_USER, (state, action) => isUnselected(action.payload.userid) ? defaultValue : state);
});

export default rolesOnUserReducer;
