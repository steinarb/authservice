import { createReducer } from 'redux-starter-kit';
import { rolesreceived } from './actions';

const rolesReducer = createReducer([], {
    [rolesreceived]: (state, action) => {
        const roles = action.payload;
        return roles;
    },
});

export default rolesReducer;
