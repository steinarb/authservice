import { createReducer } from '@reduxjs/toolkit';
import { emptyRole } from '../constants';
import { ROLE_UPDATE } from '../actiontypes';

const roleReducer = createReducer({ ...emptyRole }, {
    [ROLE_UPDATE]: (state, action) => ({ ...state, ...action.payload }),
});

export default roleReducer;
