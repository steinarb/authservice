import { createListenerMiddleware, isAnyOf } from '@reduxjs/toolkit';
import { setPassword1, setPassword2, setPasswordsNotIdentical } from './reducers/passwordSlice';

const listeners = createListenerMiddleware();

listeners.startListening({
    matcher: isAnyOf(setPassword1, setPassword2),
    effect: (action, listenerApi) => {
        const { password1, password2 } = listenerApi.getState().password;
        const passwordsNotIdentical = !!password2 && password1 !== password2;
        listenerApi.dispatch(setPasswordsNotIdentical(passwordsNotIdentical));
    }
});

export default listeners;
