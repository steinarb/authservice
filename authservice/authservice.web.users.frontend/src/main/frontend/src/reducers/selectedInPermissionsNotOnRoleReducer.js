import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_PERMISSIONS_NOT_ON_ROLE,
} from '../actiontypes';
import { emptyPermission } from '../constants';

const selectedInPermissionsNotOnRoleReducer = createReducer(emptyPermission.id, {
    [SELECT_PERMISSIONS_NOT_ON_ROLE]: (state, action) => action.payload,
});

export default selectedInPermissionsNotOnRoleReducer;
