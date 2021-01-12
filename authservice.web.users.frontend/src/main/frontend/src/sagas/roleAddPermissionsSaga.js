import { takeLatest, call, put, select } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLE_ADD_PERMISSIONS,
    ROLEPERMISSIONS_RECEIVED,
    ROLEPERMISSIONS_ERROR,
    PERMISSIONS_NOT_ON_ROLE_CLEAR,
} from '../actiontypes';

function postRoleAddPermissions(roleAndPermissions) {
    return axios.post('/authservice/useradmin/api/role/addpermissions', roleAndPermissions);
}

function* roleAddPermissions(action) {
    try {
        const role = yield select(state => state.role);
        const permissionsNotOnRole = yield select(state => state.permissionsNotOnRole);
        const permissions = permissionsNotOnRole.filter(p => p.id === action.payload);
        const roleAndPermissions = { role, permissions };
        const response = yield call(postRoleAddPermissions, roleAndPermissions);
        const rolepermissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(PERMISSIONS_NOT_ON_ROLE_CLEAR());
        yield put(ROLEPERMISSIONS_RECEIVED(rolepermissions));
    } catch (error) {
        yield put(ROLEPERMISSIONS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLE_ADD_PERMISSIONS, roleAddPermissions);
}
