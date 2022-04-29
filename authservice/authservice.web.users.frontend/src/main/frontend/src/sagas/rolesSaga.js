import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLES_REQUEST,
    ROLES_RECEIVE,
    ROLES_FAILURE,
} from '../actiontypes';

function getRoles() {
    return axios.get('/authservice/useradmin/api/roles');
}

function* requestRoles() {
    try {
        const response = yield call(getRoles);
        const roles = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(ROLES_RECEIVE(roles));
    } catch (error) {
        yield put(ROLES_FAILURE(error));
    }
}

export default function* rolesSaga() {
    yield takeLatest(ROLES_REQUEST, requestRoles);
}
