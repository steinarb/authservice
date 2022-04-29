import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECTED_ROLE,
    ROLEPERMISSIONS_RECEIVE,
    ADD_PERMISSON_TO_ROLE_RECEIVE,
    REMOVE_PERMISSON_FROM_ROLE_RECEIVE,
    SET_PERMISSIONS_ON_ROLE,
    SET_PERMISSIONS_NOT_ON_ROLE,
    ADD_PERMISSION_TO_ROLE_BUTTON_CLICKED,
    ADD_PERMISSON_TO_ROLE_REQUEST,
    REMOVE_PERMISSION_FROM_ROLE_BUTTON_CLICKED,
    REMOVE_PERMISSON_FROM_ROLE_REQUEST,
} from '../actiontypes';
import { isUnselected } from '../reducers/common';

function* findPermissionsOnRolesAndFindPermissionsNotOnRoles() {
    const roleid = yield select(state => state.roleid);
    if (isUnselected(roleid)) {
        yield put(SET_PERMISSIONS_ON_ROLE([]));
        yield put(SET_PERMISSIONS_NOT_ON_ROLE([]));
    } else {
        const { permissionsOnRole, permissionsNotOnRole } = yield select(state => {
            const permissionsOnRole = state.rolepermissions[state.rolename] || [];
            return {
                permissionsOnRole,
                permissionsNotOnRole: state.permissions.filter(p => !permissionsOnRole.find(por => por.id === p.id)),
            };
        });
        yield put(SET_PERMISSIONS_ON_ROLE(permissionsOnRole));
        yield put(SET_PERMISSIONS_NOT_ON_ROLE(permissionsNotOnRole));
    }
}

function* addPermissionToRole() {
    const roleAndPermissions = yield select(state => ({
        role: { rolename: state.rolename },
        permissions: state.permissionsNotOnRole.filter(p => p.id === state.selectedInPermissionsNotOnRole),
    }));
    yield put(ADD_PERMISSON_TO_ROLE_REQUEST(roleAndPermissions));
}

function* removePermissionFromRole() {
    const roleAndPermissions = yield select(state => ({
        role: { rolename: state.rolename },
        permissions: state.permissionsOnRole.filter(p => p.id === state.selectedInPermissionsOnRole),
    }));
    yield put(REMOVE_PERMISSON_FROM_ROLE_REQUEST(roleAndPermissions));
}

export default function* permissionsOnRoleSaga() {
    yield takeLatest(ROLEPERMISSIONS_RECEIVE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(ADD_PERMISSON_TO_ROLE_RECEIVE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(REMOVE_PERMISSON_FROM_ROLE_RECEIVE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(SELECTED_ROLE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(ADD_PERMISSION_TO_ROLE_BUTTON_CLICKED, addPermissionToRole);
    yield takeLatest(REMOVE_PERMISSION_FROM_ROLE_BUTTON_CLICKED, removePermissionFromRole);
}
