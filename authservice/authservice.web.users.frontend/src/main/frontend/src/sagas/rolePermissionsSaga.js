import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLEPERMISSIONS_REQUEST,
    ROLEPERMISSIONS_RECEIVE,
    ROLEPERMISSIONS_FAILURE,
} from '../actiontypes';

function getRolePermissions() {
    return axios.get('/authservice/useradmin/api/roles/permissions');
}

function* requestRolePermissions() {
    try {
        const response = yield call(getRolePermissions);
        const rolepermissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ROLEPERMISSIONS_RECEIVE(rolepermissions));
    } catch (error) {
        yield put(ROLEPERMISSIONS_FAILURE(error));
    }
}

export default function* rolePermissionsSaga() {
    yield takeLatest(ROLEPERMISSIONS_REQUEST, requestRolePermissions);
}
