import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_ON_USER_SELECTED,
    ROLES_ON_USER_CLEAR,
} from '../actiontypes';
import { emptyRole } from '../constants';

const selectedInRolesOnUserReducer = createReducer(emptyRole.id, {
    [ROLES_ON_USER_SELECTED]: (state, action) => action.payload,
    [ROLES_ON_USER_CLEAR]: (state, action) => emptyRole.id,
});

export default selectedInRolesOnUserReducer;
