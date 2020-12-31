import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_MODIFY,
    USER_UPDATE,
    USERS_RECEIVED,
    USERS_ERROR,
} from '../actiontypes';
import { emptyUser } from '../constants';

function postUserModify(user) {
    return axios.post('/authservice/useradmin/api/user/modify', user);
}

function* modifyUser(action) {
    try {
        const user = action.payload;
        const response = yield call(postUserModify, user);
        const users = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(USERS_RECEIVED(users));
        yield put(USER_UPDATE(emptyUser));
    } catch (error) {
        yield put(USERS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(USER_MODIFY, modifyUser);
}
