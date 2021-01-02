import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    PASSWORDS_UPDATE,
    USER_ADD,
} from '../actiontypes';
import { emptyUserAndPasswords } from '../constants';
import UserSelect from './UserSelect';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class UserModify extends Component {
    render () {
        let {
            passwords,
            passwordsNotIdentical,
            onUserFieldChange,
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
                            <button className="btn btn-primary form-control" onClick={() => onAddUser(passwords)}>Opprett bruker</button>
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
            dispatch(PASSWORDS_UPDATE(passwords));
        },
        onPasswordsFieldChange: (formValue, originalPasswords) => {
            const passwords = { ...originalPasswords, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            dispatch(PASSWORDS_UPDATE({ ...passwords, passwordsNotIdentical }));
        },
        onAddUser: (userAndPasswords) => dispatch(USER_ADD(userAndPasswords)),
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
