import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_NOT_ON_USER_SELECTED,
    ROLES_NOT_ON_USER_CLEAR,
} from '../actiontypes';
import { emptyPermission } from '../constants';

const selectedInRolesNotOnUserReducer = createReducer(emptyPermission.id, {
    [ROLES_NOT_ON_USER_SELECTED]: (state, action) => action.payload,
    [ROLES_NOT_ON_USER_CLEAR]: (state, action) => emptyPermission.id,
});

export default selectedInRolesNotOnUserReducer;
