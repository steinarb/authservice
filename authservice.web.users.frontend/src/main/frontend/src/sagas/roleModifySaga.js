import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    ROLE_MODIFY,
    ROLE_CLEAR,
    ROLES_RECEIVED,
    ROLES_ERROR,
} from '../actiontypes';

function postRoleModify(role) {
    return axios.post('/authservice/useradmin/api/role/modify', role);
}

function* modifyRole(action) {
    try {
        const role = action.payload;
        const response = yield call(postRoleModify, role);
        const roles = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(ROLES_RECEIVED(roles));
        yield put(ROLE_CLEAR());
    } catch (error) {
        yield put(ROLES_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(ROLE_MODIFY, modifyRole);
}
