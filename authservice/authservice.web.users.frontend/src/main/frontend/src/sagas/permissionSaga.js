import { takeLatest, select, put } from 'redux-saga/effects';
import {
    MODIFY_PERMISSION_BUTTON_CLICKED,
    ADD_NEW_PERMISSION_BUTTON_CLICKED,
    SAVE_MODIFIED_PERMISSION_REQUEST,
    SAVE_ADDED_PERMISSION_REQUEST
} from '../actiontypes';

function* saveModifiedPermission() {
    const permission = yield select(state => ({
        id: state.permissionid,
        permissionname: state.permissionname,
        description: state.permissionDescription,
    }));
    yield put(SAVE_MODIFIED_PERMISSION_REQUEST(permission));
}

function* saveAddedPermission() {
    const permissionAndPasswords = yield select(state => ({
        permissionname: state.permissionname,
        description: state.permissionDescription,
    }));
    yield put(SAVE_ADDED_PERMISSION_REQUEST(permissionAndPasswords));
}

export default function* permissionSaga() {
    yield takeLatest(MODIFY_PERMISSION_BUTTON_CLICKED, saveModifiedPermission);
    yield takeLatest(ADD_NEW_PERMISSION_BUTTON_CLICKED, saveAddedPermission);
}
