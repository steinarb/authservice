import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { rolepermissionsreceived } from './actions';

const rolepermissionsReducer = createReducer([], {
    [rolepermissionsreceived]: (state, action) => {
        const rolepermissions = action.payload;
        return rolepermissions;
    },
});

export default rolepermissionsReducer;
