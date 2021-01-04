import { takeLatest, select, put } from 'redux-saga/effects';
import {
    USER_UPDATE,
    USERROLES_RECEIVED,
    ROLES_ON_USER_UPDATE,
    ROLES_NOT_ON_USER_UPDATE,
} from '../actiontypes';
import { emptyRole } from '../constants';

function* findRolesOnUsersAndFindRolesNotOnUsers() {
    const user = yield select(state => state.user);
    const allRoles = yield select(state => state.roles);
    const roles = allRoles.filter(r => r.id !== emptyRole.id);
    const userroles = yield select(state => state.userroles);
    const rolesOnUser = userroles[user.username] || [];
    yield put(ROLES_ON_USER_UPDATE(rolesOnUser));
    const rolesNotOnUser = roles.filter(r => !rolesOnUser.find(rou => rou.id === r.id));
    yield put(ROLES_NOT_ON_USER_UPDATE(rolesNotOnUser));
}

export default function* () {
    yield takeLatest(USERROLES_RECEIVED, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(USER_UPDATE, findRolesOnUsersAndFindRolesNotOnUsers);
}
