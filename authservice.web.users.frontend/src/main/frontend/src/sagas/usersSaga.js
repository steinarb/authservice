import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USERS_REQUEST,
    USERS_RECEIVED,
    USERS_ERROR,
} from '../actiontypes';

function getUsers() {
    return axios.get('/authservice/useradmin/api/users');
}

function* requestUsers() {
    try {
        const response = yield call(getUsers);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USERS_RECEIVED(users));
    } catch (error) {
        yield put(USERS_ERROR(error));
    }
}

export default function* usersSaga() {
    yield takeLatest(USERS_REQUEST, requestUsers);
}
