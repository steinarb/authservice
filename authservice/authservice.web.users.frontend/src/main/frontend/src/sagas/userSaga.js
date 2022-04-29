import { takeLatest, select, put } from 'redux-saga/effects';
import {
    SELECT_USER,
    SELECTED_USER,
    MODIFY_USER_BUTTON_CLICKED,
    ADD_USER_BUTTON_CLICKED,
    SAVE_MODIFIED_USER_REQUEST,
    SAVE_ADDED_USER_REQUEST
} from '../actiontypes';

function* selectedUser(action) {
    if (action.payload === -1) {
        yield put(SELECTED_USER({ userid: -1 }));
    } else {
        const users = yield select(state => state.users);
        const user = users.find(u => u.userid === action.payload);
        if (user) {
            yield put(SELECTED_USER(user));
        }
    }
}

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
    yield takeLatest(SELECT_USER, selectedUser);
    yield takeLatest(MODIFY_USER_BUTTON_CLICKED, saveModifiedUser);
    yield takeLatest(ADD_USER_BUTTON_CLICKED, saveAddedUser);
}
