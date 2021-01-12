import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLES_REQUEST,
    ROLES_RECEIVED,
    ROLES_ERROR,
} from '../actiontypes';

function getRoles() {
    return axios.get('/authservice/useradmin/api/roles');
}

function* requestRoles() {
    try {
        const response = yield call(getRoles);
        const roles = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(ROLES_RECEIVED(roles));
    } catch (error) {
        yield put(ROLES_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLES_REQUEST, requestRoles);
}
