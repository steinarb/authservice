import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_CLEAR,
    PASSWORDS_MODIFY,
    PASSWORDS_CLEAR,
    USERS_RECEIVED,
    USERS_ERROR,
} from '../actiontypes';

function postUserAdd(userAndPasswords) {
    return axios.post('/authservice/useradmin/api/passwords/update', userAndPasswords);
}

function* addUser(action) {
    try {
        const userAndPasswords = action.payload;
        const response = yield call(postUserAdd, userAndPasswords);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USER_CLEAR());
        yield put(USERS_RECEIVED(users));
        yield put(PASSWORDS_CLEAR());
    } catch (error) {
        yield put(USERS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(PASSWORDS_MODIFY, addUser);
}
