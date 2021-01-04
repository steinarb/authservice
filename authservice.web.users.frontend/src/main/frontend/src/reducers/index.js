import { combineReducers } from 'redux';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import usersReducer from './usersReducer';
import userReducer from './userReducer';
import userroles from './userrolesReducer';
import rolesNotOnUser from './rolesNotOnUserReducer';
import selectedInRolesNotOnUser from './selectedInRolesNotOnUserReducer';
import rolesOnUser from './rolesOnUserReducer';
import selectedInRolesOnUser from './selectedInRolesOnUserReducer';
import passwordsReducer from './passwordsReducer';
import rolesReducer from './rolesReducer';
import roleReducer from './roleReducer';
import rolepermissionsReducer from './rolepermissionsReducer';
import permissionsReducer from './permissionsReducer';
import permissionReducer from './permissionReducer';
import formfieldReducer from './formfieldReducer';
import errorsReducer from './errorsReducer';

const rootsReducer = combineReducers({
    users: usersReducer,
    user: userReducer,
    userroles,
    rolesNotOnUser,
    selectedInRolesNotOnUser,
    rolesOnUser,
    selectedInRolesOnUser,
    passwords: passwordsReducer,
    roles: rolesReducer,
    role: roleReducer,
    rolepermissions: rolepermissionsReducer,
    permissions: permissionsReducer,
    permission: permissionReducer,
    formfield: formfieldReducer,
    errors: errorsReducer,
});

export default rootsReducer;
