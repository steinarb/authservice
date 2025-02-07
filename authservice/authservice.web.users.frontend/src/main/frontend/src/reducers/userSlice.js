import { createSlice } from '@reduxjs/toolkit';
import { isUsersLoaded } from '../matchers';
import { api } from '../api';
import { emptyUser } from '../constants';

const initialState = { ...emptyUser };

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        selectUser: (_, action) => action.payload,
        clearUser: () => initialState,
        setUserUsername: (state, action) => ({ ...state, username: action.payload }),
        setUserEmail: (state, action) => ({ ...state, email: action.payload }),
        setUserFirstname: (state, action) => ({ ...state, firstname: action.payload }),
        setUserLastname: (state, action) => ({ ...state, lastname: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isUsersLoaded, () => initialState)
            .addMatcher(api.endpoints.getUserUnlock.matchFulfilled, (state, action) => action.payload.find(u => u.username === state.username) || state)
    },
});

export const { selectUser, clearUser, setUserUsername, setUserEmail, setUserFirstname, setUserLastname } = userSlice.actions;

export default userSlice.reducer;
