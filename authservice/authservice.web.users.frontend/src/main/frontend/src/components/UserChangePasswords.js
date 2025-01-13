import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetUsersQuery, usePostPasswordUpdateMutation } from '../api';
import { selectUser, clearUser } from '../reducers/userSlice';
import { clearPassword, setPassword1, setPassword2 } from '../reducers/passwordSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedUser } from './common';

export default function UserChangePasswords() {
    const user = useSelector(state => state.user);
    const { data: users = [] } = useGetUsersQuery();
    const password = useSelector(state => state.password);
    const dispatch = useDispatch();
    const [ postPasswordUpdate ] = usePostPasswordUpdateMutation();
    const onChangePasswordsClicked = async () => await postPasswordUpdate({ user, ...password });

    useEffect(() => {
        dispatch(clearUser());
        dispatch(clearPassword());
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
                                onChange={e => dispatch(selectUser(findSelectedUser(e, users)))}
                                value={user.userid}>
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
                        <FormField>
                            <button
                                className="btn btn-primary form-control"
                                onClick={onChangePasswordsClicked}>
                                Change password</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
