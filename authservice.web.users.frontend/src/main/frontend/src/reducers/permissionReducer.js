import { createReducer } from 'redux-starter-kit';
import { permissionupdate } from './actions';
import { emptyPermission } from '../constants';

const permissionReducer = createReducer({ ...emptyPermission }, {
    [permissionupdate]: (state, action) => {
        const permission = action.payload;
        return permission;
    },
});

export default permissionReducer;
