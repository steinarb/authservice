import { createReducer } from 'redux-starter-kit';
import { emptyRole } from '../constants';
import { roleupdate } from './actions';

const roleReducer = createReducer({ ...emptyRole }, {
    [roleupdate]: (state, action) => {
        const role = action.payload;
        return role;
    },
});

export default roleReducer;
