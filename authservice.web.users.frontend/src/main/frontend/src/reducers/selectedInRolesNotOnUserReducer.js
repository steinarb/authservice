import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_NOT_ON_USER_SELECTED,
} from '../actiontypes';

const selectedInRolesNotOnUserReducer = createReducer(-1, {
    [ROLES_NOT_ON_USER_SELECTED]: (state, action) => action.payload,
});

export default selectedInRolesNotOnUserReducer;
