import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    SELECT_USER,
    USERNAME_FIELD_MODIFIED,
    EMAIL_FIELD_MODIFIED,
    FIRSTNAME_FIELD_MODIFIED,
    LASTNAME_FIELD_MODIFIED,
    MODIFY_USER_BUTTON_CLICKED,
    USERS_REQUEST,
    USER_CLEAR,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';

export default function UserModify() {
    const userid = useSelector(state => state.userid);
    const username = useSelector(state => state.username);
    const email = useSelector(state => state.email);
    const firstname = useSelector(state => state.firstname);
    const lastname = useSelector(state => state.lastname);
    const users = useSelector(state => state.users);
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(USERS_REQUEST());
        dispatch(USER_CLEAR());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <h1>Modify user information</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="users">Select user</FormLabel>
                        <FormField>
                            <select
                                id="users"
                                className="form-control"
                                onChange={e => dispatch(SELECT_USER(parseInt(e.target.value)))}
                                value={userid}>
                                <option key="-1" value="-1" />
                                {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
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
                        <div className="col-5"/>
                        <FormField>
                            <button
                                className="btn btn-primary form-control"
                                onClick={() => dispatch(MODIFY_USER_BUTTON_CLICKED())}>
                                Lagre endringer av bruker</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
