import { createSlice } from '@reduxjs/toolkit';
import { isRolePermissionsLoaded } from '../matchers';
import { emptyPermission } from '../constants';

const initialState = {
    notOnRole: emptyPermission.id,
    onRole: emptyPermission.id,
};

export const selectedInPermissionsSlice = createSlice({
    name: 'selectedInPermissions',
    initialState,
    reducers: {
        clearSelectedInPermissions: () => initialState,
        selectPermissionNotOnRole: (state, action) => ({ ...state, notOnRole: action.payload }),
        selectPermissionOnRole: (state, action) => ({ ...state, onRole: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isRolePermissionsLoaded, () => initialState)
    },
});

export const { clearSelectedInPermissions, selectPermissionNotOnRole, selectPermissionOnRole } = selectedInPermissionsSlice.actions;

export default selectedInPermissionsSlice.reducer;
