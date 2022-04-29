import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_ADDED_ROLE_REQUEST,
    SAVE_ADDED_ROLE_RECEIVE,
    SAVE_ADDED_ROLE_FAILURE,
} from '../actiontypes';

function postRoleAdd(role) {
    return axios.post('/authservice/useradmin/api/role/add', role);
}

function* addRole(action) {
    try {
        const response = yield call(postRoleAdd, action.payload);
        const roles = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_ADDED_ROLE_RECEIVE(roles));
    } catch (error) {
        yield put(SAVE_ADDED_ROLE_FAILURE(error));
    }
}

export default function* roleAddSaga() {
    yield takeLatest(SAVE_ADDED_ROLE_REQUEST, addRole);
}
