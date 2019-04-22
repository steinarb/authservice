import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { ROLEPERMISSIONS_RECEIVED } from '../actiontypes';

const rolepermissionsReducer = createReducer([], {
    [ROLEPERMISSIONS_RECEIVED]: (state, action) => {
        const rolepermissions = action.payload;
        return rolepermissions;
    },
});

export default rolepermissionsReducer;
