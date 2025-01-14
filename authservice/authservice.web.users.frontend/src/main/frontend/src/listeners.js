import { createListenerMiddleware, isAnyOf } from '@reduxjs/toolkit';
import { setPassword1, setPassword2, setPasswordsNotIdentical } from './reducers/passwordSlice';
import { isFailedRequest } from './matchers';

const listeners = createListenerMiddleware();

listeners.startListening({
    matcher: isFailedRequest,
    effect: ({ payload }) => {
        const { originalStatus } = payload || {};
        const statusCode = parseInt(originalStatus);
        if (statusCode === 401 || statusCode === 403) {
            location.reload(true); // Will return to current location after the login process
        }
    }
})

listeners.startListening({
    matcher: isAnyOf(setPassword1, setPassword2),
    effect: (action, listenerApi) => {
        const { password1, password2 } = listenerApi.getState().password;
        const passwordsNotIdentical = !!password2 && password1 !== password2;
        listenerApi.dispatch(setPasswordsNotIdentical(passwordsNotIdentical));
    }
});

export default listeners;
