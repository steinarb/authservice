import { takeLatest, select, put } from 'redux-saga/effects';
import {
    MODIFY_ROLE_BUTTON_CLICKED,
    ADD_ROLE_BUTTON_CLICKED,
    SAVE_MODIFIED_ROLE_REQUEST,
    SAVE_ADDED_ROLE_REQUEST
} from '../actiontypes';

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
    yield takeLatest(MODIFY_ROLE_BUTTON_CLICKED, saveModifiedRole);
    yield takeLatest(ADD_ROLE_BUTTON_CLICKED, saveAddedRole);
}
