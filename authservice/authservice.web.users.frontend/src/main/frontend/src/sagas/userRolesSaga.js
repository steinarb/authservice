import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USERROLES_REQUEST,
    USERROLES_RECEIVE,
    USERROLES_FAILURE,
} from '../actiontypes';

function getUserRoles() {
    return axios.get('/authservice/useradmin/api/users/roles');
}

function* requestUserRoles() {
    try {
        const response = yield call(getUserRoles);
        const userroles = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USERROLES_RECEIVE(userroles));
    } catch (error) {
        yield put(USERROLES_FAILURE(error));
    }
}

export default function* userRolesSaga() {
    yield takeLatest(USERROLES_REQUEST, requestUserRoles);
}
