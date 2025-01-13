import { createSlice } from '@reduxjs/toolkit';
import { isPermissionsLoaded } from '../matchers';
import { emptyPermission } from '../constants';

const initialState = { ...emptyPermission };

export const permissionSlice = createSlice({
    name: 'permission',
    initialState,
    reducers: {
        selectPermission: (_, action) => action.payload,
        clearPermission: () => initialState,
        setPermissionPermissionname: (state, action) => ({ ...state, permissionname: action.payload }),
        setPermissionDescription: (state, action) => ({ ...state, description: action.payload }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isPermissionsLoaded, () => initialState)
    },
});

export const { selectPermission, clearPermission, setPermissionPermissionname, setPermissionDescription } = permissionSlice.actions;

export default permissionSlice.reducer;
