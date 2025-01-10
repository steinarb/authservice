import { combineReducers } from 'redux';
import { createReducer } from '@reduxjs/toolkit';
import { api } from '../api';
import userid from './useridReducer';
import username from './usernameReducer';
import email from './emailReducer';
import firstname from './firstnameReducer';
import lastname from './lastnameReducer';
import selectedInRolesNotOnUser from './selectedInRolesNotOnUserReducer';
import selectedInRolesOnUser from './selectedInRolesOnUserReducer';
import password1 from './password1Reducer';
import password2 from './password2Reducer';
import passwordsNotIdentical from './passwordsNotIdenticalReducer';
import roleid from './roleidReducer';
import rolename from './rolenameReducer';
import roleDescription from './roleDescriptionReducer';
import selectedInPermissionsNotOnRole from './selectedInPermissionsNotOnRoleReducer';
import selectedInPermissionsOnRole from './selectedInPermissionsOnRoleReducer';
import permissionid from './permissionidReducer';
import permissionname from './permissionnameReducer';
import permissionDescription from './permissionDescriptionReducer';
import modifyFailedError from './modifyFailedErrorReducer';
import errors from './errorsReducer';

export default (basename) => combineReducers({
    [api.reducerPath]: api.reducer,
    userid,
    username,
    email,
    firstname,
    lastname,
    selectedInRolesNotOnUser,
    selectedInRolesOnUser,
    password1,
    password2,
    passwordsNotIdentical,
    roleid,
    rolename,
    roleDescription,
    selectedInPermissionsNotOnRole,
    selectedInPermissionsOnRole,
    permissionid,
    permissionname,
    permissionDescription,
    modifyFailedError,
    errors,
    basename: createReducer(basename, (builder) => builder),
});
