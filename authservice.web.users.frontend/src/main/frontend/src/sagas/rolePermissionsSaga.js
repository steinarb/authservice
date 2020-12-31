import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLEPERMISSIONS_REQUEST,
    ROLEPERMISSIONS_RECEIVED,
    ROLEPERMISSIONS_ERROR,
} from '../actiontypes';

function getRolePermissions() {
    return axios.get('/authservice/useradmin/api/roles/permissions');
}

function* requestRolePermissions() {
    try {
        const response = yield call(getRolePermissions);
        const rolepermissions = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(ROLEPERMISSIONS_RECEIVED(rolepermissions));
    } catch (error) {
        yield put(ROLEPERMISSIONS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLEPERMISSIONS_REQUEST, requestRolePermissions);
}
