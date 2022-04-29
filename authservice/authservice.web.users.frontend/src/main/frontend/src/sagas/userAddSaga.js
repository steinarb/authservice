import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_ADDED_USER_REQUEST,
    SAVE_ADDED_USER_RECEIVE,
    SAVE_ADDED_USER_FAILURE,
} from '../actiontypes';

function postUserAdd(userAndPasswords) {
    return axios.post('/authservice/useradmin/api/user/add', userAndPasswords);
}

function* addUser(action) {
    try {
        const userAndPasswords = action.payload;
        const response = yield call(postUserAdd, userAndPasswords);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_ADDED_USER_RECEIVE(users));
    } catch (error) {
        yield put(SAVE_ADDED_USER_FAILURE(error));
    }
}

export default function* userAddSaga() {
    yield takeLatest(SAVE_ADDED_USER_REQUEST, addUser);
}
