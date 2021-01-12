import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_MODIFY,
    USER_CLEAR,
    USERS_RECEIVED,
    USERS_ERROR,
} from '../actiontypes';

function postUserModify(user) {
    return axios.post('/authservice/useradmin/api/user/modify', user);
}

function* modifyUser(action) {
    try {
        const user = action.payload;
        const response = yield call(postUserModify, user);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USERS_RECEIVED(users));
        yield put(USER_CLEAR());
    } catch (error) {
        yield put(USERS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(USER_MODIFY, modifyUser);
}
