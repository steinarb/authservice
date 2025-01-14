import { createSlice } from '@reduxjs/toolkit';
import { isUserRolesLoaded } from '../matchers';
import { emptyRole } from '../constants';

const initialState = {
    notOnUser: emptyRole.id,
    onUser: emptyRole.id,
};

export const selectedInRolesSlice = createSlice({
    name: 'selectedInRoles',
    initialState,
    reducers: {
        clearSelectedInRoles: () => initialState,
        selectRoleNotOnUser: (state, action) => ({ ...state, notOnUser: action.payload }),
        selectRoleOnUser: (state, action) => ({ ...state, onUser: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isUserRolesLoaded, () => initialState)
    },
});

export const { clearSelectedInRoles, selectRoleNotOnUser, selectRoleOnUser } = selectedInRolesSlice.actions;

export default selectedInRolesSlice.reducer;
