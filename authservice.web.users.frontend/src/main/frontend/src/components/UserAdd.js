import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { USERS_RECEIVED, USERS_ERROR, PASSWORDS_UPDATE } from '../actiontypes';
import { emptyUserAndPasswords } from '../constants';
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

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            passwords,
            passwordsNotIdentical,
            onUserFieldChange,
            onPasswordsFieldChange,
            onAddUser,
        } = this.state;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <Header>
                    <h1>Add user</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="username">Username</FormLabel>
                            <FormField>
                                <input id="username" className="form-control" type="text" value={passwords.user.username} onChange={(event) => onUserFieldChange({username: event.target.value}, passwords)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Email address</FormLabel>
                            <FormField>
                                <input id="email" className="form-control" type="text" value={passwords.user.email} onChange={(event) => onUserFieldChange({email: event.target.value}, passwords)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="firstname">First name</FormLabel>
                            <FormField>
                                <input id="firstname" className="form-control" type="text" value={passwords.user.firstname} onChange={(event) => onUserFieldChange({firstname: event.target.value}, passwords)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="lastname">Last name</FormLabel>
                            <FormField>
                                <input id="lastname" className="form-control" type="text" value={passwords.user.lastname} onChange={(event) => onUserFieldChange({lastname: event.target.value}, passwords)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="password">Password:</FormLabel>
                            <FormField>
                                <input id="password" className="form-control" type='password' value={passwords.password1} onChange={(event) => onPasswordsFieldChange({ password1: event.target.value }, passwords)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="password2">Repeat password:</FormLabel>
                            <FormField>
                                <input id="password2" className="form-control" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                                { passwordsNotIdentical && <span>Passwords are not identical!</span> }
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <button className="form-control" onClick={() => onAddUser(passwords)}>Opprett bruker</button>
                        </FormRow>
                    </Container>
                </form>
            </div>
        );
    }
}

const checkIfPasswordsAreNotIdentical = (passwords) => {
    let { password1, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password1 !== password2;
};

const mapStateToProps = (state) => {
    return {
        passwords: state.passwords,
        passwordsNotIdentical: state.passwords.passwordsNotIdentical,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUserFieldChange: (formValue, originalPasswords) => {
            const { user } = originalPasswords;
            const passwords = { ...originalPasswords, user: { ...user, ...formValue } };
            dispatch({ type: PASSWORDS_UPDATE, payload: passwords });
        },
        onPasswordsFieldChange: (formValue, originalPasswords) => {
            const passwords = { ...originalPasswords, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            dispatch({ type: PASSWORDS_UPDATE, payload: { ...passwords, passwordsNotIdentical } });
        },
        onAddUser: (passwords) => {
            axios
                .post('/authservice/useradmin/api/user/add', passwords)
                .then(result => dispatch({ type: USERS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERS_ERROR, payload: error }));
            dispatch({ type: PASSWORDS_UPDATE, payload: { ...emptyUserAndPasswords } });
        },
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
