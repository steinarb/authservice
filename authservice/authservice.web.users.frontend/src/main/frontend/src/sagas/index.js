import { fork, all } from 'redux-saga/effects';
import userSaga from './userSaga';
import usersSaga from './usersSaga';
import userModifySaga from './userModifySaga';
import passwordsSaga from './passwordsSaga';
import passwordsModifySaga from './passwordsModifySaga';
import userAddSaga from './userAddSaga';
import userRolesSaga from './userRolesSaga';
import rolesOnUserSaga from './rolesOnUserSaga';
import userAddRolesSaga from './userAddRolesSaga';
import userRemoveRolesSaga from './userRemoveRolesSaga';
import roleSaga from './roleSaga';
import rolesSaga from './rolesSaga';
import roleModifySaga from './roleModifySaga';
import roleAddSaga from './roleAddSaga';
import rolePermissionsSaga from './rolePermissionsSaga';
import permissionsOnRoleSaga from './permissionsOnRoleSaga';
import roleAddPermissionsSaga from './roleAddPermissionsSaga';
import roleRemovePermissionsSaga from './roleRemovePermissionsSaga';
import permissionSaga from './permissionSaga';
import permissionsSaga from './permissionsSaga';
import permissionModifySaga from './permissionModifySaga';
import permissionAddSaga from './permissionAddSaga';

export default function* rootSaga() {
    yield all([
        fork(userSaga),
        fork(usersSaga),
        fork(userModifySaga),
        fork(passwordsSaga),
        fork(passwordsModifySaga),
        fork(userAddSaga),
        fork(userRolesSaga),
        fork(rolesOnUserSaga),
        fork(userAddRolesSaga),
        fork(userRemoveRolesSaga),
        fork(roleSaga),
        fork(rolesSaga),
        fork(roleModifySaga),
        fork(roleAddSaga),
        fork(rolePermissionsSaga),
        fork(permissionsOnRoleSaga),
        fork(roleAddPermissionsSaga),
        fork(roleRemovePermissionsSaga),
        fork(permissionSaga),
        fork(permissionsSaga),
        fork(permissionModifySaga),
        fork(permissionAddSaga),
    ]);
}
