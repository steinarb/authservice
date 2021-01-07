import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLE_REMOVE_PERMISSIONS,
    ROLEPERMISSIONS_RECEIVED,
    ROLEPERMISSIONS_ERROR,
    PERMISSIONS_ON_ROLE_CLEAR,
} from '../actiontypes';
import { emptyRoleAndPasswords } from '../constants';

function postRoleRemovePermissions(roleAndPermissions) {
    return axios.post('/authservice/useradmin/api/role/removepermissions', roleAndPermissions);
}

function* roleRemovePermissions(action) {
    try {
        const role = yield select(state => state.role);
        const permissionsOnRole = yield select(state => state.permissionsOnRole);
        const permissions = permissionsOnRole.filter(p => p.id === action.payload);
        const roleAndPermissions = { role, permissions };
        const response = yield call(postRoleRemovePermissions, roleAndPermissions);
        const rolepermissions = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(PERMISSIONS_ON_ROLE_CLEAR());
        yield put(ROLEPERMISSIONS_RECEIVED(rolepermissions));
    } catch (error) {
        yield put(ROLEPERMISSIONS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLE_REMOVE_PERMISSIONS, roleRemovePermissions);
}
