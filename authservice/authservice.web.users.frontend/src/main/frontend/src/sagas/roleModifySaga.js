import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    SAVE_MODIFIED_ROLE_REQUEST,
    SAVE_MODIFIED_ROLE_RECEIVE,
    SAVE_MODIFIED_ROLE_FAILURE,
} from '../actiontypes';

function postRoleModify(role) {
    return axios.post('/authservice/useradmin/api/role/modify', role);
}

function* modifyRole(action) {
    try {
        const response = yield call(postRoleModify, action.payload);
        const roles = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(SAVE_MODIFIED_ROLE_RECEIVE(roles));
    } catch (error) {
        yield put(SAVE_MODIFIED_ROLE_FAILURE(error));
    }
}

export default function* roleModifySaga() {
    yield takeLatest(SAVE_MODIFIED_ROLE_REQUEST, modifyRole);
}
