import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    ADD_PERMISSON_TO_ROLE_REQUEST,
    ADD_PERMISSON_TO_ROLE_RECEIVE,
    ADD_PERMISSON_TO_ROLE_FAILURE,
} from '../actiontypes';

function postRoleAddPermissions(roleAndPermissions) {
    return axios.post('/authservice/useradmin/api/role/addpermissions', roleAndPermissions);
}

function* roleAddPermissions(action) {
    try {
        const response = yield call(postRoleAddPermissions, action.payload);
        const rolepermissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ADD_PERMISSON_TO_ROLE_RECEIVE(rolepermissions));
    } catch (error) {
        yield put(ADD_PERMISSON_TO_ROLE_FAILURE(error));
    }
}

export default function* roleAddPermissionsSaga() {
    yield takeLatest(ADD_PERMISSON_TO_ROLE_REQUEST, roleAddPermissions);
}
