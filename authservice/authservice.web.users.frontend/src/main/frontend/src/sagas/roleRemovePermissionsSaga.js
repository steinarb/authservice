import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    REMOVE_PERMISSON_FROM_ROLE_REQUEST,
    REMOVE_PERMISSON_FROM_ROLE_RECEIVE,
    REMOVE_PERMISSON_FROM_ROLE_FAILURE,
} from '../actiontypes';

function postRoleRemovePermissions(roleAndPermissions) {
    return axios.post('/authservice/useradmin/api/role/removepermissions', roleAndPermissions);
}

function* roleRemovePermissions(action) {
    try {
        const response = yield call(postRoleRemovePermissions, action.payload);
        const rolepermissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(REMOVE_PERMISSON_FROM_ROLE_RECEIVE(rolepermissions));
    } catch (error) {
        yield put(REMOVE_PERMISSON_FROM_ROLE_FAILURE(error));
    }
}

export default function* roleRemovePermissionsSaga() {
    yield takeLatest(REMOVE_PERMISSON_FROM_ROLE_REQUEST, roleRemovePermissions);
}
