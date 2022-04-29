import { createReducer } from '@reduxjs/toolkit';
import {
    ROLEPERMISSIONS_RECEIVE,
    ADD_PERMISSON_TO_ROLE_RECEIVE,
    REMOVE_PERMISSON_FROM_ROLE_RECEIVE,
} from '../actiontypes';

const rolepermissionsReducer = createReducer([], {
    [ROLEPERMISSIONS_RECEIVE]: (state, action) => action.payload,
    [ADD_PERMISSON_TO_ROLE_RECEIVE]: (state, action) => action.payload,
    [REMOVE_PERMISSON_FROM_ROLE_RECEIVE]: (state, action) => action.payload,
});

export default rolepermissionsReducer;
