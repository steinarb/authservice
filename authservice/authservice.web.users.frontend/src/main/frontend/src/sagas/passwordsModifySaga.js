import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_PASSWORDS_MODIFY_REQUEST,
    SAVE_PASSWORDS_MODIFY_RECEIVE,
    SAVE_PASSWORDS_MODIFY_FAILURE,
} from '../actiontypes';

function postUserAdd(userAndPasswords) {
    return axios.post('/authservice/useradmin/api/passwords/update', userAndPasswords);
}

function* addUser(action) {
    try {
        const userAndPasswords = action.payload;
        const response = yield call(postUserAdd, userAndPasswords);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_PASSWORDS_MODIFY_RECEIVE(users));
    } catch (error) {
        yield put(SAVE_PASSWORDS_MODIFY_FAILURE(error));
    }
}

export default function* passwordsModifySaga() {
    yield takeLatest(SAVE_PASSWORDS_MODIFY_REQUEST, addUser);
}
