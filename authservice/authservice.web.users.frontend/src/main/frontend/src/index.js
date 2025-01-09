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

// Calculate the basename based on the URL of the vite assets directory
const baseUrl = Array.from(document.scripts).map(s => s.src).filter(src => src.includes('assets/'))[0].replace(/\/assets\/.*/, '');
const basename = new URL(baseUrl).pathname;

const store = configureStore({
    reducer: rootReducer(basename),
    middleware: () => new Tuple(sagaMiddleware),
});
sagaMiddleware.run(rootSaga);

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
    <Provider store={store}>
        <App basename={basename} />
    </Provider>,
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
