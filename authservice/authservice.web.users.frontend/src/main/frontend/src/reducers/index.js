import { combineReducers } from 'redux';
import users from './usersReducer';
import userid from './useridReducer';
import username from './usernameReducer';
import email from './emailReducer';
import firstname from './firstnameReducer';
import lastname from './lastnameReducer';
import userroles from './userrolesReducer';
import rolesNotOnUser from './rolesNotOnUserReducer';
import selectedInRolesNotOnUser from './selectedInRolesNotOnUserReducer';
import rolesOnUser from './rolesOnUserReducer';
import selectedInRolesOnUser from './selectedInRolesOnUserReducer';
import password1 from './password1Reducer';
import password2 from './password2Reducer';
import passwordsNotIdentical from './passwordsNotIdenticalReducer';
import roles from './rolesReducer';
import roleid from './roleidReducer';
import rolename from './rolenameReducer';
import roleDescription from './roleDescriptionReducer';
import rolepermissions from './rolepermissionsReducer';
import permissionsNotOnRole from './permissionsNotOnRoleReducer';
import selectedInPermissionsNotOnRole from './selectedInPermissionsNotOnRoleReducer';
import permissionsOnRole from './permissionsOnRoleReducer';
import selectedInPermissionsOnRole from './selectedInPermissionsOnRoleReducer';
import permissions from './permissionsReducer';
import permissionid from './permissionidReducer';
import permissionname from './permissionnameReducer';
import permissionDescription from './permissionDescriptionReducer';
import errors from './errorsReducer';

const rootsReducer = combineReducers({
    users,
    userid,
    username,
    email,
    firstname,
    lastname,
    userroles,
    rolesNotOnUser,
    selectedInRolesNotOnUser,
    rolesOnUser,
    selectedInRolesOnUser,
    password1,
    password2,
    passwordsNotIdentical,
    roles,
    roleid,
    rolename,
    roleDescription,
    rolepermissions,
    permissionsNotOnRole,
    selectedInPermissionsNotOnRole,
    permissionsOnRole,
    selectedInPermissionsOnRole,
    permissions,
    permissionid,
    permissionname,
    permissionDescription,
    errors,
});

export default rootsReducer;
