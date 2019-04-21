import { createReducer } from 'redux-starter-kit';
import { emptyUser } from '../constants';
import { userupdate } from './actions';

const userReducer = createReducer({ ...emptyUser }, {
    [userupdate]: (state, action) => {
        const user = action.payload;
        return user;
    },
});

export default userReducer;
