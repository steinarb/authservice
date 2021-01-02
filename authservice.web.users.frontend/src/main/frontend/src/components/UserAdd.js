import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    USER_UPDATE,
    PASSWORDS_UPDATE,
    USER_ADD,
} from '../actiontypes';
import { emptyUserAndPasswords } from '../constants';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class UserModify extends Component {
    render () {
        let {
            user,
            passwords,
            passwordsNotIdentical,
            onUsername,
            onEmail,
            onFirstname,
            onLastname,
            onPasswordsFieldChange,
            onAddUser,
        } = this.props;

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
                                <input id="username" className="form-control" type="text" value={user.username} onChange={onUsername} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Email address</FormLabel>
                            <FormField>
                                <input id="email" className="form-control" type="text" value={user.email} onChange={onEmail} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="firstname">First name</FormLabel>
                            <FormField>
                                <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={onFirstname} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="lastname">Last name</FormLabel>
                            <FormField>
                                <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={onLastname} />
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
                            <button className="btn btn-primary form-control" onClick={() => onAddUser(passwords, user)}>Opprett bruker</button>
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
        user: state.user,
        passwords: state.passwords,
        passwordsNotIdentical: state.passwords.passwordsNotIdentical,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUsername: e => dispatch(USER_UPDATE({ username: e.target.value })),
        onEmail: e => dispatch(USER_UPDATE({ email: e.target.value })),
        onFirstname: e => dispatch(USER_UPDATE({ firstname: e.target.value })),
        onLastname: e => dispatch(USER_UPDATE({ lastname: e.target.value })),
        onPasswordsFieldChange: (formValue, originalPasswords) => {
            const passwords = { ...originalPasswords, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            dispatch(PASSWORDS_UPDATE({ ...passwords, passwordsNotIdentical }));
        },
        onAddUser: (passwords, user) => dispatch(USER_ADD({ ...passwords, user })),
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
