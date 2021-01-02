import { createReducer } from '@reduxjs/toolkit';
import { emptyUser } from '../constants';
import {
    USER_UPDATE,
    USERS_RECEIVED,
} from '../actiontypes';

const userReducer = createReducer({ ...emptyUser }, {
    [USER_UPDATE]: (state, action) => ({ ...state, ...action.payload }),
    [USERS_RECEIVED]: (state, action) => ({ ...emptyUser }),
});

export default userReducer;
