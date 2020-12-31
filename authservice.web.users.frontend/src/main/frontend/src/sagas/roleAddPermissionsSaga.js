import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLE_ADD_PERMISSIONS,
    ROLEPERMISSIONS_RECEIVED,
    ROLEPERMISSIONS_ERROR,
} from '../actiontypes';
import { emptyRoleAndPasswords } from '../constants';

function postRoleAddPermissions(roleAndPermissions) {
    return axios.post('/authservice/useradmin/api/role/addpermissions', roleAndPermissions);
}

function* roleAddPermissions(action) {
    try {
        const { role, permissionsNotOnRoleSelected } = action.payload;
        const permissions = [ permissionsNotOnRoleSelected ];
        const roleAndPermissions = { role, permissions };
        const response = yield call(postRoleAddPermissions, roleAndPermissions);
        const rolepermissions = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(ROLEPERMISSIONS_RECEIVED(rolepermissions));
    } catch (error) {
        yield put(ROLEPERMISSIONS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLE_ADD_PERMISSIONS, roleAddPermissions);
}
