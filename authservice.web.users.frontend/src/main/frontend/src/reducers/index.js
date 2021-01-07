import { combineReducers } from 'redux';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import users from './usersReducer';
import user from './userReducer';
import userroles from './userrolesReducer';
import rolesNotOnUser from './rolesNotOnUserReducer';
import selectedInRolesNotOnUser from './selectedInRolesNotOnUserReducer';
import rolesOnUser from './rolesOnUserReducer';
import selectedInRolesOnUser from './selectedInRolesOnUserReducer';
import passwords from './passwordsReducer';
import roles from './rolesReducer';
import role from './roleReducer';
import rolepermissions from './rolepermissionsReducer';
import permissionsNotOnRole from './permissionsNotOnRoleReducer';
import selectedInPermissionsNotOnRole from './selectedInPermissionsNotOnRoleReducer';
import permissionsOnRole from './permissionsOnRoleReducer';
import selectedInPermissionsOnRole from './selectedInPermissionsOnRoleReducer';
import permissions from './permissionsReducer';
import permission from './permissionReducer';
import formfield from './formfieldReducer';
import errors from './errorsReducer';

const rootsReducer = combineReducers({
    users,
    user,
    userroles,
    rolesNotOnUser,
    selectedInRolesNotOnUser,
    rolesOnUser,
    selectedInRolesOnUser,
    passwords,
    roles,
    role,
    rolepermissions,
    permissionsNotOnRole,
    selectedInPermissionsNotOnRole,
    permissionsOnRole,
    selectedInPermissionsOnRole,
    permissions,
    permission,
    formfield,
    errors,
});

export default rootsReducer;
