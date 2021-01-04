import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_ON_USER_SELECTED,
} from '../actiontypes';

const selectedInRolesOnUserReducer = createReducer(-1, {
    [ROLES_ON_USER_SELECTED]: (state, action) => action.payload,
});

export default selectedInRolesOnUserReducer;
