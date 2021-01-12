import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_ADD,
    USER_CLEAR,
    PASSWORDS_CLEAR,
    USERS_RECEIVED,
    USERS_ERROR,
} from '../actiontypes';

function postUserAdd(userAndPasswords) {
    return axios.post('/authservice/useradmin/api/user/add', userAndPasswords);
}

function* addUser(action) {
    try {
        const userAndPasswords = action.payload;
        const response = yield call(postUserAdd, userAndPasswords);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USERS_RECEIVED(users));
        yield put(USER_CLEAR());
        yield put(PASSWORDS_CLEAR());
    } catch (error) {
        yield put(USERS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(USER_ADD, addUser);
}
