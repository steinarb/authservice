import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { userrolesreceived } from './actions';

const userrolesReducer = createReducer([], {
    [userrolesreceived]: (state, action) => {
        const userroles = action.payload;
        return userroles;
    },
});

export default userrolesReducer;
