import { createReducer } from '@reduxjs/toolkit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { USERROLES_RECEIVED } from '../actiontypes';

const userrolesReducer = createReducer([], {
    [USERROLES_RECEIVED]: (state, action) => action.payload,
});

export default userrolesReducer;
