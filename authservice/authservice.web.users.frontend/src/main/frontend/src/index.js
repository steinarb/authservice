import 'regenerator-runtime';
import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import rootReducer from './reducers';
import { api } from './api';
import listeners from './listeners';

// Calculate the basename based on the URL of the vite assets directory
const baseUrl = Array.from(document.scripts).map(s => s.src).filter(src => src.includes('assets/'))[0].replace(/\/assets\/.*/, '');
const basename = new URL(baseUrl).pathname;

const store = configureStore({
    reducer: rootReducer(basename),
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(api.middleware).prepend(listeners.middleware),
});

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
