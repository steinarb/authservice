import { createReducer } from '@reduxjs/toolkit';
import {
    SET_ROLES_NOT_ON_USER,
    SELECT_USER,
} from '../actiontypes';
import { isUnselected } from './common';
const defaultValue = [];

const rolesNotOnUserReducer = createReducer([], {
    [SET_ROLES_NOT_ON_USER]: (state, action) => action.payload,
    [SELECT_USER]: (state, action) => isUnselected(action.payload) ? defaultValue : state,
});

export default rolesNotOnUserReducer;
