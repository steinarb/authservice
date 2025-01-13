import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetUsersQuery, usePostUserModifyMutation } from '../api';
import { selectUser, clearUser, setUserUsername, setUserEmail, setUserFirstname, setUserLastname } from '../reducers/userSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedUser } from './common';

export default function UserModify() {
    const user = useSelector(state => state.user);
    const { data: users = [] } = useGetUsersQuery();
    const dispatch = useDispatch();
    const [ postUserModify ] = usePostUserModifyMutation();
    const onModifyUserClicked = async () => await postUserModify(user);

    useEffect(() => {
        dispatch(clearUser());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/users">Up to user adminstration</StyledLinkLeft>
                <h1>Modify user information</h1>
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
                        <div className="col-5"/>
                        <FormField>
                            <button
                                className="btn btn-primary form-control"
                                onClick={onModifyUserClicked}>
                                Save changes to user</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
