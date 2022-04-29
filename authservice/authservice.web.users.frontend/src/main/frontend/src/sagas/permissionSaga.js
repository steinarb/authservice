import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECT_PERMISSION,
    SELECTED_PERMISSION,
    MODIFY_PERMISSION_BUTTON_CLICKED,
    ADD_NEW_PERMISSION_BUTTON_CLICKED,
    SAVE_MODIFIED_PERMISSION_REQUEST,
    SAVE_ADDED_PERMISSION_REQUEST
} from '../actiontypes';

function* selectedPermission(action) {
    if (action.payload === -1) {
        yield put(SELECTED_PERMISSION({ id: -1 }));
    } else {
        const permissions = yield select(state => state.permissions);
        const permission = permissions.find(u => u.id === action.payload);
        if (permission) {
            yield put(SELECTED_PERMISSION(permission));
        }
    }
}

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
    yield takeLatest(SELECT_PERMISSION, selectedPermission);
    yield takeLatest(MODIFY_PERMISSION_BUTTON_CLICKED, saveModifiedPermission);
    yield takeLatest(ADD_NEW_PERMISSION_BUTTON_CLICKED, saveAddedPermission);
}
