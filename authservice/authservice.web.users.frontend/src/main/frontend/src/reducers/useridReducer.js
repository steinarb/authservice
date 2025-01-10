import { createReducer } from '@reduxjs/toolkit';
import {
    SELECT_USER,
    USER_CLEAR,
} from '../actiontypes';
import { isUsersLoaded } from '../matchers';

const defaultValue = -1;

const useridReducer = createReducer(defaultValue, builder => {
    builder
        .addCase(SELECT_USER, (state, action) => action.payload.userid)
        .addMatcher(isUsersLoaded, () => defaultValue)
        .addCase(USER_CLEAR, () => defaultValue);
});

export default useridReducer;
