import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_UPDATE,
    PASSWORDS_UPDATE,
    PASSWORDS_MODIFY,
    USERS_RECEIVED,
    USERS_ERROR,
} from '../actiontypes';
import { emptyUser, emptyUserAndPasswords } from '../constants';

function postUserAdd(userAndPasswords) {
    return axios.post('/authservice/useradmin/api/passwords/update', userAndPasswords);
}

function* addUser(action) {
    try {
        const userAndPasswords = action.payload;
        const response = yield call(postUserAdd, userAndPasswords);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(USER_UPDATE(emptyUser));
        yield put(USERS_RECEIVED(users));
        yield put(PASSWORDS_UPDATE(emptyUserAndPasswords));
    } catch (error) {
        yield put(USERS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(PASSWORDS_MODIFY, addUser);
}