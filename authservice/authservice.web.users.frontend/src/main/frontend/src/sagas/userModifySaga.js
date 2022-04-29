import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_MODIFIED_USER_REQUEST,
    SAVE_MODIFIED_USER_RECEIVE,
    SAVE_MODIFIED_USER_FAILURE,
} from '../actiontypes';

function postUserModify(user) {
    return axios.post('/authservice/useradmin/api/user/modify', user);
}

function* modifyUser(action) {
    try {
        const user = action.payload;
        const response = yield call(postUserModify, user);
        const users = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_MODIFIED_USER_RECEIVE(users));
    } catch (error) {
        yield put(SAVE_MODIFIED_USER_FAILURE(error));
    }
}

export default function* userModifySaga() {
    yield takeLatest(SAVE_MODIFIED_USER_REQUEST, modifyUser);
}
