import { createReducer } from 'redux-starter-kit';
import { emptyRole } from '../constants';
import { ROLES_RECEIVED } from '../actiontypes';

const rolesReducer = createReducer([], {
    [ROLES_RECEIVED]: (state, action) => {
        const roles = action.payload;
        roles.unshift(emptyRole);
        return roles;
    },
});

export default rolesReducer;
