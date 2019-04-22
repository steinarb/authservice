import { createReducer } from 'redux-starter-kit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { PASSWORDS_UPDATE } from '../actiontypes';

const passwordsReducer = createReducer({ ...emptyUserAndPasswords }, {
    [PASSWORDS_UPDATE]: (state, action) => {
        const passwords = action.payload;
        return passwords;
    },
});

export default passwordsReducer;
