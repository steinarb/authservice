import { createReducer } from '@reduxjs/toolkit';
import {
    ROLES_ON_USER_UPDATE,
} from '../actiontypes';

const rolesOnUserReducer = createReducer([], {
    [ROLES_ON_USER_UPDATE]: (state, action) => action.payload,
});

export default rolesOnUserReducer;
