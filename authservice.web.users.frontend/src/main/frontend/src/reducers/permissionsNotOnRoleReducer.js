import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSIONS_NOT_ON_ROLE_UPDATE,
} from '../actiontypes';

const permissionsNotOnRoleReducer = createReducer([], {
    [PERMISSIONS_NOT_ON_ROLE_UPDATE]: (state, action) => action.payload,
});

export default permissionsNotOnRoleReducer;
