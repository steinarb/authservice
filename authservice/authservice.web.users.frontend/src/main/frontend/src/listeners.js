import { createListenerMiddleware, isAnyOf } from '@reduxjs/toolkit';
import {
    PASSWORD1_FIELD_MODIFIED,
    PASSWORD2_FIELD_MODIFIED,
    SET_PASSWORDS_NOT_IDENTICAL,
} from './actiontypes';

const listeners = createListenerMiddleware();

listeners.startListening({
    matcher: isAnyOf(PASSWORD1_FIELD_MODIFIED, PASSWORD2_FIELD_MODIFIED),
    effect: (action, listenerApi) => {
        const { password1, password2 } = listenerApi.getState();
        const passwordsNotIdentical = !!password2 && password1 !== password2;
        listenerApi.dispatch(SET_PASSWORDS_NOT_IDENTICAL(passwordsNotIdentical));
    }
});

export default listeners;
