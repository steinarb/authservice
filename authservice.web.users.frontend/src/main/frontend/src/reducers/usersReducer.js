import { createReducer } from '@reduxjs/toolkit';
import { emptyUser } from '../constants';
import { USERS_RECEIVED } from '../actiontypes';

const usersReducer = createReducer([], {
    [USERS_RECEIVED]: (state, action) => {
        const users = action.payload;
        users.unshift(emptyUser);
        return users;
    },
});

export default usersReducer;
