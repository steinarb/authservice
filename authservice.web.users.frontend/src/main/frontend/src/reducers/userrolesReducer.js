import { createReducer } from '@reduxjs/toolkit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { USERROLES_RECEIVED } from '../actiontypes';

const userrolesReducer = createReducer([], {
    [USERROLES_RECEIVED]: (state, action) => {
        const userroles = action.payload;
        return userroles;
    },
});

export default userrolesReducer;
