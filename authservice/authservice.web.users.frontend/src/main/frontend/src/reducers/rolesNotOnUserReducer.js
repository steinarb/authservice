import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_NOT_ON_USER_UPDATE,
} from '../actiontypes';

const rolesNotOnUserReducer = createReducer([], {
    [ROLES_NOT_ON_USER_UPDATE]: (state, action) => action.payload,
});

export default rolesNotOnUserReducer;
