import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import {
    userserrors,
    userroleserrors,
    roleserrors,
    rolepermissionserrors,
    permissionserrors,
} from './actions';

const errorsReducer = createReducer({}, {
    [userserrors]: (state, action) => {
        const users = action.payload;
        return { ...state, users };
    },
    [userroleserrors]: (state, action) => {
        const userroles = action.payload;
        return { ...state, userroles };
    },
    [roleserrors]: (state, action) => {
        const roles = action.payload;
        return { ...state, roles };
    },
    [rolepermissionserrors]: (state, action) => {
        const rolepermissions = action.payload;
        return { ...state, rolepermissions };
    },
    [permissionserrors]: (state, action) => {
        const permissions = action.payload;
        return { ...state, permissions };
    },
});

export default errorsReducer;
