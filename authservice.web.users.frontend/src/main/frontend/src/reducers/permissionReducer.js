import { createReducer } from '@reduxjs/toolkit';
import { PERMISSION_UPDATE } from '../actiontypes';
import { emptyPermission } from '../constants';

const permissionReducer = createReducer({ ...emptyPermission }, {
    [PERMISSION_UPDATE]: (state, action) => {
        const permission = action.payload;
        return permission;
    },
});

export default permissionReducer;
