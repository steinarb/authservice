import { createReducer } from '@reduxjs/toolkit';
import { PERMISSION_UPDATE } from '../actiontypes';
import { emptyPermission } from '../constants';

const permissionReducer = createReducer({ ...emptyPermission }, {
    [PERMISSION_UPDATE]: (state, action) => ({ ...state, ...action.payload }),
});

export default permissionReducer;
