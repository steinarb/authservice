import { createReducer } from '@reduxjs/toolkit';
import { emptyUser } from '../constants';
import {
    USER_UPDATE,
    USER_CLEAR,
    USERS_RECEIVED,
} from '../actiontypes';

const userReducer = createReducer({ ...emptyUser }, {
    [USER_UPDATE]: (state, action) => ({ ...state, ...action.payload }),
    [USER_CLEAR]: (state, action) => ({ ...emptyUser }),
    [USERS_RECEIVED]: (state, action) => ({ ...emptyUser }),
});

export default userReducer;
