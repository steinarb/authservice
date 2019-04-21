import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { USERS_RECEIVED, USERS_ERROR, USER_UPDATE } from '../actiontypes';
import UserSelect from './UserSelect';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class UserModify extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onUsers();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            users,
            usersMap,
            user,
            onUsersFieldChange,
            onFieldChange,
            onSaveUpdatedUser,
        } = this.state;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <Header>
                    <h1>Modify user information</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="users">Select user</FormLabel>
                            <FormField>
                                <UserSelect id="users" className="form-control" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="username">Username</FormLabel>
                            <FormField>
                                <input id="username" className="form-control" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Email address</FormLabel>
                            <FormField>
                                <input id="email" className="form-control" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="firstname">First name</FormLabel>
                            <FormField>
                                <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="lastname">Last name</FormLabel>
                            <FormField>
                                <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <div className="col-5"/>
                            <FormField>
                                <button className="form-control" onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
                            </FormField>
                        </FormRow>
                    </Container>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        users: state.users,
        usersMap: new Map(state.users.map(i => [i.firstname + ' ' + i.lastname, i])),
        user: state.user,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => {
            axios
                .get('/authservice/useradmin/api/users')
                .then(result => dispatch({ type: USERS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERS_ERROR, payload: error }));
        },
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            dispatch({ type: USER_UPDATE, payload: user });
        },
        onFieldChange: (formValue, originalUser) => {
            const user = { ...originalUser, ...formValue };
            dispatch({ type: USER_UPDATE, payload: user });
        },
        onSaveUpdatedUser: (user) => {
            axios
                .post('/authservice/useradmin/api/user/modify', user)
                .then(result => dispatch({ type: USERS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERS_ERROR, payload: error }));
            dispatch({ type: USER_UPDATE, payload: { user: {} } });
        },
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
