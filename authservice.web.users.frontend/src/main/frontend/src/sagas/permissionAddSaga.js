import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    PERMISSION_ADD,
    PERMISSION_CLEAR,
    PERMISSIONS_RECEIVED,
    PERMISSIONS_ERROR,
} from '../actiontypes';

function postPermissionAdd(permission) {
    return axios.post('/authservice/useradmin/api/permission/add', permission);
}

function* addPermission(action) {
    try {
        const permission = action.payload;
        const response = yield call(postPermissionAdd, permission);
        const permissions = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(PERMISSIONS_RECEIVED(permissions));
        yield put(PERMISSION_CLEAR());
    } catch (error) {
        yield put(PERMISSIONS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(PERMISSION_ADD, addPermission);
}
