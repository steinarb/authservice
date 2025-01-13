import { createSlice } from '@reduxjs/toolkit';
import { isRolesLoaded } from '../matchers';
import { emptyRole } from '../constants';

const initialState = { ...emptyRole };

export const roleSlice = createSlice({
    name: 'role',
    initialState,
    reducers: {
        selectRole: (_, action) => action.payload,
        clearRole: () => initialState,
        setRoleRolename: (state, action) => ({ ...state, rolename: action.payload }),
        setRoleDescription: (state, action) => ({ ...state, description: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isRolesLoaded, () => initialState)
    },
});

export const { selectRole, clearRole, setRoleRolename, setRoleDescription } = roleSlice.actions;

export default roleSlice.reducer;
