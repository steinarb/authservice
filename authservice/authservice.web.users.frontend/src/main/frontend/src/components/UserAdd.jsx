import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { usePostUserAddMutation } from '../api';
import { selectUser, clearUser, setUserUsername, setUserEmail, setUserFirstname, setUserLastname } from '../reducers/userSlice';
import { clearPassword, setPassword1, setPassword2 } from '../reducers/passwordSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';

export default function UserAdd() {
    const user = useSelector(state => state.user);
    const password = useSelector(state => state.password);
    const dispatch = useDispatch();
    const [ postUserAdd ] = usePostUserAddMutation();
    const onAddButtonClicked = async () => await postUserAdd({ user, ...password });

    useEffect(() => {
        dispatch(clearUser());
        dispatch(clearPassword());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/users">Up to user adminstration</StyledLinkLeft>
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
                                value={user.username}
                                onChange={e => dispatch(setUserUsername(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Email address</FormLabel>
                        <FormField>
                            <input
                                id="email"
                                className="form-control"
                                type="text"
                                value={user.email}
                                onChange={e => dispatch(setUserEmail(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="firstname">First name</FormLabel>
                        <FormField>
                            <input
                                id="firstname"
                                className="form-control"
                                type="text"
                                value={user.firstname}
                                onChange={e => dispatch(setUserFirstname(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="lastname">Last name</FormLabel>
                        <FormField>
                            <input
                                id="lastname"
                                className="form-control"
                                type="text"
                                value={user.lastname}
                                onChange={e => dispatch(setUserLastname(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password">Password:</FormLabel>
                        <FormField>
                            <input
                                id="password"
                                className="form-control"
                                type="password"
                                value={password.password1}
                                onChange={e => dispatch(setPassword1(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password2">Repeat password:</FormLabel>
                        <FormField>
                            <input
                                id="password2"
                                className="form-control"
                                type="password"
                                value={password.password2}
                                onChange={e => dispatch(setPassword2(e.target.value))} />
                            { password.passwordsNotIdentical && <span>Passwords are not identical!</span> }
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button
                            className="btn btn-primary form-control"
                            onClick={onAddButtonClicked}>
                            Create user</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
