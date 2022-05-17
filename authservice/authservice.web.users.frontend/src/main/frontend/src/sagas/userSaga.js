import { takeLatest, select, put } from 'redux-saga/effects';
import {
    MODIFY_USER_BUTTON_CLICKED,
    ADD_USER_BUTTON_CLICKED,
    SAVE_MODIFIED_USER_REQUEST,
    SAVE_ADDED_USER_REQUEST
} from '../actiontypes';

function* saveModifiedUser() {
    const user = yield select(state => ({
        userid: state.userid,
        username: state.username,
        email: state.email,
        firstname: state.firstname,
        lastname: state.lastname,
    }));
    yield put(SAVE_MODIFIED_USER_REQUEST(user));
}

function* saveAddedUser() {
    const userAndPasswords = yield select(state => ({
        user: {
            username: state.username,
            email: state.email,
            firstname: state.firstname,
            lastname: state.lastname,
        },
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    }));
    yield put(SAVE_ADDED_USER_REQUEST(userAndPasswords));
}

export default function* userSaga() {
    yield takeLatest(MODIFY_USER_BUTTON_CLICKED, saveModifiedUser);
    yield takeLatest(ADD_USER_BUTTON_CLICKED, saveAddedUser);
}
