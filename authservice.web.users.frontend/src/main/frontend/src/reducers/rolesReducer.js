import { createReducer } from 'redux-starter-kit';
import { ROLES_RECEIVED } from '../actiontypes';

const rolesReducer = createReducer([], {
    [ROLES_RECEIVED]: (state, action) => {
        const roles = action.payload;
        return roles;
    },
});

export default rolesReducer;
