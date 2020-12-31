import { takeLatest, call, put, fork } from 'redux-saga/effects';
import axios from 'axios';
import {
    USER_ADD_ROLES,
    USERROLES_RECEIVED,
    USERROLES_ERROR,
} from '../actiontypes';
import { emptyUserAndPasswords } from '../constants';

function postUserAddRoles(userAndRoles) {
    return axios.post('/authservice/useradmin/api/user/addroles', userAndRoles);
}

function* userAddRoles(action) {
    try {
        const { user, rolesNotOnUserSelected } = action.payload;
        const roles = [ rolesNotOnUserSelected ];
        const userAndRoles = { user, roles };
        const response = yield call(postUserAddRoles, userAndRoles);
        const userroles = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(USERROLES_RECEIVED(userroles));
    } catch (error) {
        yield put(USERROLES_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(USER_ADD_ROLES, userAddRoles);
}
