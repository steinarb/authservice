import { createReducer } from '@reduxjs/toolkit';
import {
    SET_ROLES_ON_USER,
    SELECT_USER,
} from '../actiontypes';
import { isUnselected } from './common';
const defaultValue = [];

const rolesOnUserReducer = createReducer([], {
    [SET_ROLES_ON_USER]: (state, action) => action.payload,
    [SELECT_USER]: (state, action) => isUnselected(action.payload) ? defaultValue : state,
});

export default rolesOnUserReducer;
