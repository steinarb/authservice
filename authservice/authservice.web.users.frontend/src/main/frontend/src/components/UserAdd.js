import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    USERNAME_FIELD_MODIFIED,
    EMAIL_FIELD_MODIFIED,
    FIRSTNAME_FIELD_MODIFIED,
    LASTNAME_FIELD_MODIFIED,
    PASSWORD1_FIELD_MODIFIED,
    PASSWORD2_FIELD_MODIFIED,
    ADD_USER_BUTTON_CLICKED,
    USER_CLEAR,
    PASSWORDS_CLEAR,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';

export default function UserAdd() {
    const username = useSelector(state => state.username);
    const email = useSelector(state => state.email);
    const firstname = useSelector(state => state.firstname);
    const lastname = useSelector(state => state.lastname);
    const password1 = useSelector(state => state.password1);
    const password2 = useSelector(state => state.password2);
    const passwordsNotIdentical = useSelector(state => state.passwordsNotIdentical);
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(USER_CLEAR());
        dispatch(PASSWORDS_CLEAR());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <h1>Add user</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="username">Username</FormLabel>
                        <FormField>
                            <input
                                id="username"
                                className="form-control"
                                type="text"
                                value={username}
                                onChange={e => dispatch(USERNAME_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Email address</FormLabel>
                        <FormField>
                            <input
                                id="email"
                                className="form-control"
                                type="text"
                                value={email}
                                onChange={e => dispatch(EMAIL_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="firstname">First name</FormLabel>
                        <FormField>
                            <input
                                id="firstname"
                                className="form-control"
                                type="text"
                                value={firstname}
                                onChange={e => dispatch(FIRSTNAME_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="lastname">Last name</FormLabel>
                        <FormField>
                            <input
                                id="lastname"
                                className="form-control"
                                type="text"
                                value={lastname}
                                onChange={e => dispatch(LASTNAME_FIELD_MODIFIED(e.target.value))} />
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
                        <button
                            className="btn btn-primary form-control"
                            onClick={() => dispatch(ADD_USER_BUTTON_CLICKED())}>
                            Opprett bruker</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
