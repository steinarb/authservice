import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    PERMISSION_MODIFY,
    PERMISSION_CLEAR,
    PERMISSIONS_RECEIVED,
    PERMISSIONS_ERROR,
} from '../actiontypes';

function postPermissionModify(permission) {
    return axios.post('/authservice/useradmin/api/permission/modify', permission);
}

function* modifyPermission(action) {
    try {
        const permission = action.payload;
        const response = yield call(postPermissionModify, permission);
        const permissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(PERMISSIONS_RECEIVED(permissions));
        yield put(PERMISSION_CLEAR());
    } catch (error) {
        yield put(PERMISSIONS_ERROR(error));
    }
}

export default function* permissionModifySaga() {
    yield takeLatest(PERMISSION_MODIFY, modifyPermission);
}
