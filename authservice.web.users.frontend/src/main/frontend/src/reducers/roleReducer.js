import { createReducer } from '@reduxjs/toolkit';
import { emptyRole } from '../constants';
import {
    ROLE_UPDATE,
    ROLE_CLEAR,
} from '../actiontypes';

const roleReducer = createReducer({ ...emptyRole }, {
    [ROLE_UPDATE]: (state, action) => ({ ...state, ...action.payload }),
    [ROLE_CLEAR]: () => ({ ...emptyRole }),
});

export default roleReducer;
