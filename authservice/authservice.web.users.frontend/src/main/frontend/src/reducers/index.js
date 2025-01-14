import { combineReducers } from 'redux';
import { createReducer } from '@reduxjs/toolkit';
import { api } from '../api';
import user from './userSlice';
import selectedInRoles from './selectedInRolesSlice';
import password from './passwordSlice';
import role from './roleSlice';
import selectedInPermissionsNotOnRole from './selectedInPermissionsNotOnRoleReducer';
import selectedInPermissionsOnRole from './selectedInPermissionsOnRoleReducer';
import permission from './permissionSlice';
import modifyFailedError from './modifyFailedErrorReducer';
import errors from './errorsReducer';

export default (basename) => combineReducers({
    [api.reducerPath]: api.reducer,
    user,
    selectedInRoles,
    password,
    role,
    selectedInPermissionsNotOnRole,
    selectedInPermissionsOnRole,
    permission,
    modifyFailedError,
    errors,
    basename: createReducer(basename, (builder) => builder),
});
