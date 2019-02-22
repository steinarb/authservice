import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { USERS_RECEIVED, USERS_ERROR, PASSWORDS_UPDATE } from './actiontypes';
import UserSelect from './components/UserSelect';
import { emptyUserAndPasswords } from './constants';
import { Header } from './components/bootstrap/Header';
import { Container } from './components/bootstrap/Container';
import { StyledLinkLeft } from './components/bootstrap/StyledLinkLeft';
import {FormRow } from './components/bootstrap/FormRow';
import {FormLabel } from './components/bootstrap/FormLabel';
import {FormField } from './components/bootstrap/FormField';

class UserChangePasswords extends Component {
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
            passwords = emptyUserAndPasswords,
            passwordsNotIdentical,
            onUsersFieldChange,
            onPasswordsFieldChange,
            onSaveUpdatedPassword,
        } = this.state;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <Header>
                    <h1>Change password for user</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="users">Select user</FormLabel>
                            <FormField>
                                <UserSelect id="users" className="form-control" users={users} usersMap={usersMap} value={passwords.user.fullname} onUsersFieldChange={onUsersFieldChange} />
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
                            <FormField>
                                <button className="form-control" onClick={() => onSaveUpdatedPassword(passwords)}>Change password</button>
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
        passwords: state.passwords,
        passwordsNotIdentical: state.passwords.passwordsNotIdentical,
    };
};

const checkIfPasswordsAreNotIdentical = (passwords) => {
    let { password1, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password1 !== password2;
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
            const user = usersMap.get(selectedValue);
            const passwords = { ...emptyUserAndPasswords, user };
            dispatch({ type: PASSWORDS_UPDATE, payload: passwords });
        },
        onPasswordsFieldChange: (formValue, passwordsFromState) => {
            const passwords = { ...passwordsFromState, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            let changedField = {
                ...passwords,
                passwordsNotIdentical,
            };
            dispatch({ type: PASSWORDS_UPDATE, payload: changedField });
        },
        onSaveUpdatedPassword: (user) => {
            axios
                .post('/authservice/useradmin/api/passwords/update', user)
                .then(result => dispatch({ type: USERS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERS_ERROR, payload: error }));
            dispatch({ type: PASSWORDS_UPDATE, payload: emptyUserAndPasswords });
        },
    };
};

UserChangePasswords = connect(mapStateToProps, mapDispatchToProps)(UserChangePasswords);

export default UserChangePasswords;
