import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_ROLES_ON_USER,
} from '../actiontypes';
import { emptyRole } from '../constants';

const selectedInRolesOnUserReducer = createReducer(emptyRole.id, {
    [SELECT_ROLES_ON_USER]: (state, action) => action.payload,
});

export default selectedInRolesOnUserReducer;
