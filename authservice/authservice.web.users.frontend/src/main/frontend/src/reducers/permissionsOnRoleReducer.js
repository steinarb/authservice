import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSIONS_ON_ROLE_UPDATE,
} from '../actiontypes';

const permissionsOnRoleReducer = createReducer([], {
    [PERMISSIONS_ON_ROLE_UPDATE]: (state, action) => action.payload,
});

export default permissionsOnRoleReducer;
