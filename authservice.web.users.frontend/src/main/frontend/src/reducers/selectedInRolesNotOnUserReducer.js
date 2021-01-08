import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_NOT_ON_USER_SELECTED,
    ROLES_NOT_ON_USER_CLEAR,
} from '../actiontypes';
import { emptyRole } from '../constants';

const selectedInRolesNotOnUserReducer = createReducer(emptyRole.id, {
    [ROLES_NOT_ON_USER_SELECTED]: (state, action) => action.payload,
    [ROLES_NOT_ON_USER_CLEAR]: (state, action) => emptyRole.id,
});

export default selectedInRolesNotOnUserReducer;
