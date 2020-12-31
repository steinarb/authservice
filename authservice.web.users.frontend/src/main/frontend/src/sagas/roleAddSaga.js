import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLE_ADD,
    ROLE_UPDATE,
    ROLES_RECEIVED,
    ROLES_ERROR,
} from '../actiontypes';
import { emptyRole } from '../constants';

function postRoleAdd(role) {
    return axios.post('/authservice/useradmin/api/role/add', role);
}

function* addRole(action) {
    try {
        const role = action.payload;
        const response = yield call(postRoleAdd, role);
        const roles = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(ROLES_RECEIVED(roles));
        yield put(ROLE_UPDATE(emptyRole));
    } catch (error) {
        yield put(ROLES_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLE_ADD, addRole);
}
