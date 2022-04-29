import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_ADD_ROLE_REQUEST,
    USER_ADD_ROLE_RECEIVE,
    USER_ADD_ROLE_FAILURE,
} from '../actiontypes';

function postUserAddRoles(userAndRoles) {
    return axios.post('/authservice/useradmin/api/user/addroles', userAndRoles);
}

function* userAddRoles(action) {
    try {
        const response = yield call(postUserAddRoles, action.payload);
        const userroles = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(USER_ADD_ROLE_RECEIVE(userroles));
    } catch (error) {
        yield put(USER_ADD_ROLE_FAILURE(error));
    }
}

export default function* userAddRolesSaga() {
    yield takeLatest(USER_ADD_ROLE_REQUEST, userAddRoles);
}
