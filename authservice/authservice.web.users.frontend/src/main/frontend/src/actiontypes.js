import { createAction } from '@reduxjs/toolkit';

export const SET_PERMISSIONS_NOT_ON_ROLE = createAction('SET_PERMISSIONS_NOT_ON_ROLE');
export const SELECT_PERMISSIONS_NOT_ON_ROLE = createAction('SELECT_PERMISSIONS_NOT_ON_ROLE');
export const SET_PERMISSIONS_ON_ROLE = createAction('SET_PERMISSIONS_ON_ROLE');
export const SELECT_PERMISSIONS_ON_ROLE = createAction('SELECT_PERMISSIONS_ON_ROLE');
export const SET_MODIFY_FAILED_ERROR = createAction('SET_MODIFY_FAILED_ERROR');
export const CLEAR_MODIFY_FAILED_ERROR = createAction('CLEAR_MODIFY_FAILED_ERROR');
