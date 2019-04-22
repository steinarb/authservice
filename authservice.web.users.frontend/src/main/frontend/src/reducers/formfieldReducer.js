import { createReducer } from 'redux-starter-kit';
import { FORMFIELD_UPDATE } from '../actiontypes';

const formfieldReducer = createReducer({}, {
    [FORMFIELD_UPDATE]: (state, action) => {
        const formfield = action.payload;
        return { ...state, ...formfield };
    },
});

export default formfieldReducer;
