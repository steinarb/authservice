import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECT_ROLE,
    SELECTED_ROLE,
    MODIFY_ROLE_BUTTON_CLICKED,
    ADD_ROLE_BUTTON_CLICKED,
    SAVE_MODIFIED_ROLE_REQUEST,
    SAVE_ADDED_ROLE_REQUEST
} from '../actiontypes';

function* selectedRole(action) {
    if (action.payload === -1) {
        yield put(SELECTED_ROLE({ id: -1 }));
    } else {
        const roles = yield select(state => state.roles);
        const role = roles.find(u => u.id === action.payload);
        if (role) {
            yield put(SELECTED_ROLE(role));
        }
    }
}

function* saveModifiedRole() {
    const role = yield select(state => ({
        id: state.roleid,
        rolename: state.rolename,
        description: state.roleDescription,
    }));
    yield put(SAVE_MODIFIED_ROLE_REQUEST(role));
}

function* saveAddedRole() {
    const roleAndPasswords = yield select(state => ({
        rolename: state.rolename,
        description: state.roleDescription,
    }));
    yield put(SAVE_ADDED_ROLE_REQUEST(roleAndPasswords));
}

export default function* roleSaga() {
    yield takeLatest(SELECT_ROLE, selectedRole);
    yield takeLatest(MODIFY_ROLE_BUTTON_CLICKED, saveModifiedRole);
    yield takeLatest(ADD_ROLE_BUTTON_CLICKED, saveAddedRole);
}
