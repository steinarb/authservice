import { createReducer } from '@reduxjs/toolkit';
import {
    SET_PERMISSIONS_ON_ROLE,
    SELECT_ROLE,
} from '../actiontypes';
import { isUnselected } from './common';
const defaultValue = [];

const permissionsOnRoleReducer = createReducer(defaultValue, {
    [SET_PERMISSIONS_ON_ROLE]: (state, action) => action.payload,
    [SELECT_ROLE]: (state, action) => isUnselected(action.payload) ? defaultValue : state,
});

export default permissionsOnRoleReducer;
