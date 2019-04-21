import { createReducer } from 'redux-starter-kit';
import { formfieldupdate } from './actions';

const formfieldReducer = createReducer({}, {
    [formfieldupdate]: (state, action) => {
        const formfield = action.payload;
        return { ...state, ...formfield };
    },
});

export default formfieldReducer;
