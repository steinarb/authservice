import { createReducer } from '@reduxjs/toolkit';
import {
    PERMISSION_UPDATE,
    PERMISSION_CLEAR,
} from '../actiontypes';
import { emptyPermission } from '../constants';

const permissionReducer = createReducer({ ...emptyPermission }, {
    [PERMISSION_UPDATE]: (state, action) => ({ ...state, ...action.payload }),
    [PERMISSION_CLEAR]: (state, action) => ({ ...emptyPermission }),
});

export default permissionReducer;
