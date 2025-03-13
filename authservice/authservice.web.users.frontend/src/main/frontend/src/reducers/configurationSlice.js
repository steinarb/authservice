import { createSlice } from '@reduxjs/toolkit';
import { isConfigurationLoaded } from '../matchers';
import { emptyRole } from '../constants';

const initialState = {
    excessiveFailedLoginLimit: 3,
    isModified: false,
};

export const configurationSlice = createSlice({
    name: 'configuration',
    initialState,
    reducers: {
        setExcessiveFailedLoginLimit: (state, action) => ({ ...state, excessiveFailedLoginLimit: action.payload, isModified: true }),
    },
    extraReducers: builder => {
        builder
            .addMatcher(isConfigurationLoaded, (_, action) => ({ ...action.payload, isModified: false }))
    },
});

export const { setExcessiveFailedLoginLimit } = configurationSlice.actions;
export default configurationSlice.reducer;
