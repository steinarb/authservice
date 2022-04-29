import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLES_NOT_ON_USER,
} from '../actiontypes';
import { emptyRole } from '../constants';

const selectedInRolesNotOnUserReducer = createReducer(emptyRole.id, {
    [SELECT_ROLES_NOT_ON_USER]: (state, action) => action.payload,
});

export default selectedInRolesNotOnUserReducer;
