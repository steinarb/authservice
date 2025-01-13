import { createSlice } from '@reduxjs/toolkit';
import { isUsersLoaded } from '../matchers';
import { emptyPasswords } from '../constants';

const initialState = { ...emptyPasswords };

export const passwordSlice = createSlice({
    name: 'password',
    initialState,
    reducers: {
        selectPassword: (_, action) => action.payload,
        clearPassword: () => initialState,
        setPassword1: (state, action) => ({ ...state, password1: action.payload }),
        setPassword2: (state, action) => ({ ...state, password2: action.payload }),
        setPasswordsNotIdentical: (state, action) => ({ ...state, passwordsNotIdentical: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isUsersLoaded, () => initialState)
    },
});

export const { selectPassword, clearPassword, setPassword1, setPassword2, setPasswordsNotIdentical } = passwordSlice.actions;

export default passwordSlice.reducer;
