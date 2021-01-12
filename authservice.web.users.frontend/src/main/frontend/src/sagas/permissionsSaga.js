import { takeLatest, call, put } from 'redux-saga/effects';
import axios from 'axios';
import {
    PERMISSIONS_REQUEST,
    PERMISSIONS_RECEIVED,
    PERMISSIONS_ERROR,
} from '../actiontypes';

function getPermissions() {
    return axios.get('/authservice/useradmin/api/permissions');
}

function* requestPermissions() {
    try {
        const response = yield call(getPermissions);
        const permissions = (response.headers['content-type'] == 'application/json') ? response.data : [];
        yield put(PERMISSIONS_RECEIVED(permissions));
    } catch (error) {
        yield put(PERMISSIONS_ERROR(error));
    }
}

export default function* () {
    yield takeLatest(PERMISSIONS_REQUEST, requestPermissions);
}
