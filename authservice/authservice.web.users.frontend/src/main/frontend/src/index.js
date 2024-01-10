import 'regenerator-runtime';
import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import createSagaMiddleware from 'redux-saga';
import { configureStore, Tuple } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import rootReducer from './reducers';

import rootSaga from './sagas';
const sagaMiddleware = createSagaMiddleware();

const store = configureStore({
    reducer: rootReducer,
    middleware: () => new Tuple(sagaMiddleware),
});
sagaMiddleware.run(rootSaga);

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
    <Provider store={store}>
        <App/>
    </Provider>,
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
