import { takeLatest, select, put } from 'redux-saga/effects';
import {
    PASSWORD1_FIELD_MODIFIED,
    PASSWORD2_FIELD_MODIFIED,
    SET_PASSWORDS_NOT_IDENTICAL,
    CHANGE_PASSWORD_BUTTON_CLICKED,
    SAVE_PASSWORDS_MODIFY_REQUEST,
} from '../actiontypes';

function* comparePasswords() {
    const passwordsNotIdentical = yield select(state => {
        const { password1, password2 } = state;
        if (!password2) {
            return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
        }

        return password1 !== password2;
    });
    yield put(SET_PASSWORDS_NOT_IDENTICAL(passwordsNotIdentical));
}

function* saveModifiedPasswords() {
    const saveModifiedPasswordsRequest = yield select(state => ({
        user: {
            userid: state.userid,
        },
        password1: state.password1,
        password2: state.password2,
        passwordNotIdentical: state.passwordNotIdentical,
    }));
    yield put(SAVE_PASSWORDS_MODIFY_REQUEST(saveModifiedPasswordsRequest));
}

export default function* passwordsSaga() {
    yield takeLatest(PASSWORD1_FIELD_MODIFIED, comparePasswords);
    yield takeLatest(PASSWORD2_FIELD_MODIFIED, comparePasswords);
    yield takeLatest(CHANGE_PASSWORD_BUTTON_CLICKED, saveModifiedPasswords);
}
