import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USERS_REQUEST,
    USERS_RECEIVE,
    USERS_FAILURE,
} from '../actiontypes';

function getUsers() {
    return axios.get('/authservice/useradmin/api/users');
}

function* requestUsers() {
    try {
        const response = yield call(getUsers);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USERS_RECEIVE(users));
    } catch (error) {
        yield put(USERS_FAILURE(error));
    }
}

export default function* usersSaga() {
    yield takeLatest(USERS_REQUEST, requestUsers);
}
