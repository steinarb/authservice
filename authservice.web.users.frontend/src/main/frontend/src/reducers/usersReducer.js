import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { usersreceived } from './actions';

const usersReducer = createReducer([], {
    [usersreceived]: (state, action) => {
        const users = action.payload;
        users.unshift(emptyUser);
        return users;
    },
});

export default usersReducer;
