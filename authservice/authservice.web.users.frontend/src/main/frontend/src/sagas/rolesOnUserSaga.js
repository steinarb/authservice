import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECTED_USER,
    USERROLES_RECEIVE,
    USER_ADD_ROLE_RECEIVE,
    USER_REMOVE_ROLE_RECEIVE,
    SET_ROLES_ON_USER,
    SET_ROLES_NOT_ON_USER,
    ADD_USER_ROLE_BUTTON_CLICKED,
    USER_ADD_ROLE_REQUEST,
    REMOVE_USER_ROLE_BUTTON_CLICKED,
    USER_REMOVE_ROLE_REQUEST,
} from '../actiontypes';
import { isUnselected } from '../reducers/common';

function* findRolesOnUsersAndFindRolesNotOnUsers() {
    const userid = yield select(state => state.userid);
    if (isUnselected(userid)) {
        yield put(SET_ROLES_ON_USER([]));
        yield put(SET_ROLES_NOT_ON_USER([]));
    } else {
        const { rolesOnUser, rolesNotOnUser } = yield select(state => {
            const rolesOnUser = state.userroles[state.username] || [];
            return {
                rolesOnUser,
                rolesNotOnUser: state.roles.filter(r => !rolesOnUser.find(rou => rou.id === r.id)),
            };
        });
        yield put(SET_ROLES_ON_USER(rolesOnUser));
        yield put(SET_ROLES_NOT_ON_USER(rolesNotOnUser));
    }
}

function* addRoleToUser() {
    const userAndRoles = yield select(state => ({
        user: {
            username: state.username,
        },
        roles: state.roles.filter(r => r.id === state.selectedInRolesNotOnUser),
    }));
    yield put(USER_ADD_ROLE_REQUEST(userAndRoles));
}

function* removeRoleToUser() {
    const userAndRoles = yield select(state => ({
        user: {
            username: state.username,
        },
        roles: state.roles.filter(r => r.id === state.selectedInRolesOnUser),
    }));
    yield put(USER_REMOVE_ROLE_REQUEST(userAndRoles));
}

export default function* rolesOnUserSaga() {
    yield takeLatest(USERROLES_RECEIVE, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(USER_ADD_ROLE_RECEIVE, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(USER_REMOVE_ROLE_RECEIVE, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(SELECTED_USER, findRolesOnUsersAndFindRolesNotOnUsers);
    yield takeLatest(ADD_USER_ROLE_BUTTON_CLICKED, addRoleToUser);
    yield takeLatest(REMOVE_USER_ROLE_BUTTON_CLICKED, removeRoleToUser);
}
