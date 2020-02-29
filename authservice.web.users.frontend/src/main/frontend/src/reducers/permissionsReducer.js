import { combineReducers } from 'redux';
import { createReducer, createAction } from '@reduxjs/toolkit';
import { emptyPermission } from '../constants';
import {
    PERMISSIONS_RECEIVED,
} from '../actiontypes';

const permissionsReducer = createReducer([], {
    [ PERMISSIONS_RECEIVED]: (state, action) => {
        const permissions = action.payload;
        permissions.unshift(emptyPermission);
        return permissions;
    },
});

export default permissionsReducer;
