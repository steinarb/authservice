import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    PERMISSIONS_REQUEST,
    PERMISSIONS_RECEIVE,
    PERMISSIONS_FAILURE,
} from '../actiontypes';

function getPermissions() {
    return axios.get('/authservice/useradmin/api/permissions');
}

function* requestPermissions() {
    try {
        const response = yield call(getPermissions);
        const permissions = (response.headers['content-type'] === 'application/json') ? response.data : [];
        yield put(PERMISSIONS_RECEIVE(permissions));
    } catch (error) {
        yield put(PERMISSIONS_FAILURE(error));
    }
}

export default function* permissionsSaga() {
    yield takeLatest(PERMISSIONS_REQUEST, requestPermissions);
}
