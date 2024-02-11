import { takeLatest, put } from 'redux-saga/effects';
import {
    SAVE_MODIFIED_USER_FAILURE,
    SAVE_PASSWORDS_MODIFY_FAILURE,
    SAVE_ADDED_USER_FAILURE,
    USER_ADD_ROLE_FAILURE,
    USER_REMOVE_ROLE_FAILURE,
    SAVE_MODIFIED_ROLE_FAILURE,
    SAVE_ADDED_ROLE_FAILURE,
    ADD_PERMISSON_TO_ROLE_FAILURE,
    REMOVE_PERMISSON_FROM_ROLE_FAILURE,
    SAVE_MODIFIED_PERMISSION_FAILURE,
    SAVE_ADDED_PERMISSION_FAILURE,
    SET_MODIFY_FAILED_ERROR,
    USERS_REQUEST,
    SAVE_MODIFIED_USER_REQUEST,
    SAVE_PASSWORDS_MODIFY_REQUEST,
    SAVE_ADDED_USER_REQUEST,
    ROLES_REQUEST,
    USER_ADD_ROLE_REQUEST,
    USER_REMOVE_ROLE_REQUEST,
    SAVE_MODIFIED_ROLE_REQUEST,
    SAVE_ADDED_ROLE_REQUEST,
    ADD_PERMISSON_TO_ROLE_REQUEST,
    REMOVE_PERMISSON_FROM_ROLE_REQUEST,
    SAVE_MODIFIED_PERMISSION_REQUEST,
    PERMISSIONS_REQUEST,
    SAVE_ADDED_PERMISSION_REQUEST,
    CLEAR_MODIFY_FAILED_ERROR,
} from '../actiontypes';

export default function* modifyFailedErrorSaga() {
    // Create an appropriate error message and save it in redux
    yield takeLatest(SAVE_MODIFIED_USER_FAILURE, createErrorMessageForSaveModifiedUserFailed);
    yield takeLatest(SAVE_PASSWORDS_MODIFY_FAILURE, createErrorMessageForSaveModifiedPasswordFailed);
    yield takeLatest(SAVE_ADDED_USER_FAILURE, createErrorMessageForSaveAddedUserFailed);
    yield takeLatest(USER_ADD_ROLE_FAILURE, createErrorMessageForAddRoleToUserFailed);
    yield takeLatest(USER_REMOVE_ROLE_FAILURE, createErrorMessageForRemoveRoleFromUserFailed);
    yield takeLatest(SAVE_MODIFIED_ROLE_FAILURE, createErrorMessageForSaveModifiedRoleFailed);
    yield takeLatest(SAVE_ADDED_ROLE_FAILURE, createErrorMessageForSaveAddedRoleFailed);
    yield takeLatest(ADD_PERMISSON_TO_ROLE_FAILURE, createErrorMessageForAddPermissionToRoleFailed);
    yield takeLatest(REMOVE_PERMISSON_FROM_ROLE_FAILURE, createErrorMessageForRemovePermissionFromRoleFailed);
    yield takeLatest(SAVE_MODIFIED_PERMISSION_FAILURE, createErrorMessageForSaveModifiedPermissionFailed);
    yield takeLatest(SAVE_ADDED_PERMISSION_FAILURE, createErrorMessageForSaveAddedPermissionFailed);

    // A new request should clear the existing error message
    yield takeLatest(USERS_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_MODIFIED_USER_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_ADDED_PERMISSION_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_ADDED_PERMISSION_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_PASSWORDS_MODIFY_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_ADDED_USER_REQUEST, clearModifyFailedError);
    yield takeLatest(ROLES_REQUEST, clearModifyFailedError);
    yield takeLatest(USER_ADD_ROLE_REQUEST, clearModifyFailedError);
    yield takeLatest(USER_REMOVE_ROLE_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_MODIFIED_ROLE_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_ADDED_ROLE_REQUEST, clearModifyFailedError);
    yield takeLatest(ADD_PERMISSON_TO_ROLE_REQUEST, clearModifyFailedError);
    yield takeLatest(REMOVE_PERMISSON_FROM_ROLE_REQUEST, clearModifyFailedError);
    yield takeLatest(PERMISSIONS_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_MODIFIED_PERMISSION_REQUEST, clearModifyFailedError);
    yield takeLatest(SAVE_ADDED_PERMISSION_REQUEST, clearModifyFailedError);
}

function* createErrorMessageForSaveModifiedUserFailed(action) {
    const reason = findReason(action);
    const message = 'Saving changes to user failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveModifiedPasswordFailed(action) {
    const reason = findReason(action);
    const message = 'Saving changes to password failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveAddedUserFailed(action) {
    const reason = findReason(action);
    const message = 'Adding user failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForAddRoleToUserFailed(action) {
    const reason = findReason(action);
    const message = 'Adding role to user failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForRemoveRoleFromUserFailed(action) {
    const reason = findReason(action);
    const message = 'Removing role from user failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveModifiedRoleFailed(action) {
    const reason = findReason(action);
    const message = 'Saving changes to role failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveAddedRoleFailed(action) {
    const reason = findReason(action);
    const message = 'Adding role failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForAddPermissionToRoleFailed(action) {
    const reason = findReason(action);
    const message = 'Adding permission to role failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForRemovePermissionFromRoleFailed(action) {
    const reason = findReason(action);
    const message = 'Removing permission from role failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveModifiedPermissionFailed(action) {
    const reason = findReason(action);
    const message = 'Saving changes to permission failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* createErrorMessageForSaveAddedPermissionFailed(action) {
    const reason = findReason(action);
    const message = 'Adding permission failed: ' + reason;
    yield put(SET_MODIFY_FAILED_ERROR(message));
}

function* clearModifyFailedError() {
    yield put(CLEAR_MODIFY_FAILED_ERROR());
}

function findReason(action) {
    const responseCode = action.payload.response.status;

    if (responseCode === 401) {
        return 'User is not logged in';
    }

    if (responseCode === 403) {
        return 'Logged in user doesn\'t have the oldalbumadmin role. Please log in with a user that has the oldalbumadmin role';
    }

    return 'Unknown reason';
}
