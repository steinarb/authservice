import { takeLatest, select, put } from 'redux-saga/effects';
import {
    ROLE_UPDATE,
    ROLEPERMISSIONS_RECEIVED,
    PERMISSIONS_ON_ROLE_UPDATE,
    PERMISSIONS_NOT_ON_ROLE_UPDATE,
} from '../actiontypes';
import { emptyPermission } from '../constants';

function* findPermissionsOnRolesAndFindPermissionsNotOnRoles() {
    const role = yield select(state => state.role);
    const allPermissions = yield select(state => state.permissions);
    const permissions = allPermissions.filter(r => r.id !== emptyPermission.id);
    const rolepermissions = yield select(state => state.rolepermissions);
    const permissionsOnRole = rolepermissions[role.rolename] || [];
    yield put(PERMISSIONS_ON_ROLE_UPDATE(permissionsOnRole));
    const permissionsNotOnRole = permissions.filter(r => !permissionsOnRole.find(rou => rou.id === r.id));
    yield put(PERMISSIONS_NOT_ON_ROLE_UPDATE(permissionsNotOnRole));
}

export default function* permissionsOnRoleSaga() {
    yield takeLatest(ROLEPERMISSIONS_RECEIVED, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
    yield takeLatest(ROLE_UPDATE, findPermissionsOnRolesAndFindPermissionsNotOnRoles);
}
