import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    USERS_REQUEST,
    SELECT_USER,
    PASSWORD1_FIELD_MODIFIED,
    PASSWORD2_FIELD_MODIFIED,
    CHANGE_PASSWORD_BUTTON_CLICKED,
    USER_CLEAR,
    PASSWORDS_CLEAR,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedUser } from './common';

export default function UserChangePasswords() {
    const userid = useSelector(state => state.userid);
    const users = useSelector(state => state.users);
    const password1 = useSelector(state => state.password1);
    const password2 = useSelector(state => state.password2);
    const passwordsNotIdentical = useSelector(state => state.passwordsNotIdentical);
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(USERS_REQUEST());
        dispatch(USER_CLEAR());
        dispatch(PASSWORDS_CLEAR());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/users">Up to user adminstration</StyledLinkLeft>
                <h1>Change password for user</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="users">Select user</FormLabel>
                        <FormField>
                            <select
                                id="users"
                                className="form-control"
                                onChange={e => dispatch(SELECT_USER(findSelectedUser(e, users)))}
                                value={userid}>
                                <option key="-1" value="-1" />
                                {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password">Password:</FormLabel>
                        <FormField>
                            <input
                                id="password"
                                className="form-control"
                                type="password"
                                value={password1}
                                onChange={e => dispatch(PASSWORD1_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password2">Repeat password:</FormLabel>
                        <FormField>
                            <input
                                id="password2"
                                className="form-control"
                                type="password"
                                value={password2}
                                onChange={e => dispatch(PASSWORD2_FIELD_MODIFIED(e.target.value))} />
                            { passwordsNotIdentical && <span>Passwords are not identical!</span> }
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormField>
                            <button
                                className="btn btn-primary form-control"
                                onClick={() => dispatch(CHANGE_PASSWORD_BUTTON_CLICKED())}>
                                Change password</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
