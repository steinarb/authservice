import { createReducer } from 'redux-starter-kit';
import { emptyUser } from '../constants';
import { USER_UPDATE } from '../actiontypes';

const userReducer = createReducer({ ...emptyUser }, {
    [USER_UPDATE]: (state, action) => {
        const user = action.payload;
        return user;
    },
});

export default userReducer;
