import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_ADDED_PERMISSION_REQUEST,
    SAVE_ADDED_PERMISSION_RECEIVE,
    SAVE_ADDED_PERMISSION_FAILURE,
} from '../actiontypes';

function postPermissionAdd(permission) {
    return axios.post('/authservice/useradmin/api/permission/add', permission);
}

function* addPermission(action) {
    try {
        const response = yield call(postPermissionAdd, action.payload);
        const permissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_ADDED_PERMISSION_RECEIVE(permissions));
    } catch (error) {
        yield put(SAVE_ADDED_PERMISSION_FAILURE(error));
    }
}

export default function* permissionAddSaga() {
    yield takeLatest(SAVE_ADDED_PERMISSION_REQUEST, addPermission);
}
