import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_MODIFIED_PERMISSION_REQUEST,
    SAVE_MODIFIED_PERMISSION_RECEIVE,
    SAVE_MODIFIED_PERMISSION_FAILURE,
} from '../actiontypes';

function postPermissionModify(permission) {
    return axios.post('/authservice/useradmin/api/permission/modify', permission);
}

function* modifyPermission(action) {
    try {
        const response = yield call(postPermissionModify, action.payload);
        const permissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_MODIFIED_PERMISSION_RECEIVE(permissions));
    } catch (error) {
        yield put(SAVE_MODIFIED_PERMISSION_FAILURE(error));
    }
}

export default function* permissionModifySaga() {
    yield takeLatest(SAVE_MODIFIED_PERMISSION_REQUEST, modifyPermission);
}
