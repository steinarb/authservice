import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { passwordsupdate } from './actions';

const passwordsReducer = createReducer({ ...emptyUserAndPasswords }, {
    [passwordsupdate]: (state, action) => {
        const passwords = action.payload;
        return passwords;
    },
});

export default passwordsReducer;
