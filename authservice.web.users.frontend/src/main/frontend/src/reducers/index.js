import { combineReducers } from 'redux';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import usersReducer from './usersReducer';
import userReducer from './userReducer';
import userrolesReducer from './userrolesReducer';
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
    userroles: userrolesReducer,
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
