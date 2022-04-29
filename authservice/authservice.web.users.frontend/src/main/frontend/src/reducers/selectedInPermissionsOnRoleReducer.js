import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PERMISSIONS_ON_ROLE,
} from '../actiontypes';
import { emptyPermission } from '../constants';

const selectedInPermissionsOnRoleReducer = createReducer(emptyPermission.id, {
    [SELECT_PERMISSIONS_ON_ROLE]: (state, action) => action.payload,
});

export default selectedInPermissionsOnRoleReducer;
