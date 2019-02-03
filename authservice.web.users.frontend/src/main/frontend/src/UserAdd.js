import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { USERS_RECEIVED, USERS_ERROR, PASSWORDS_UPDATE } from './actiontypes';
import { emptyUserAndPasswords } from './constants';
import UserSelect from './components/UserSelect';

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
                <h1>Add user</h1>
                <br/>
                <Link to="/authservice/useradmin/users">Up to user adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="username">Username</label>
                    <input id="username" type="text" value={passwords.user.username} onChange={(event) => onUserFieldChange({username: event.target.value}, passwords)} />
                    <br/>
                    <label htmlFor="email">Email address</label>
                    <input id="email" type="text" value={passwords.user.email} onChange={(event) => onUserFieldChange({email: event.target.value}, passwords)} />
                    <br/>
                    <label htmlFor="firstname">First name</label>
                    <input id="firstname" type="text" value={passwords.user.firstname} onChange={(event) => onUserFieldChange({firstname: event.target.value}, passwords)} />
                    <br/>
                    <label htmlFor="lastname">Last name</label>
                    <input id="lastname" type="text" value={passwords.user.lastname} onChange={(event) => onUserFieldChange({lastname: event.target.value}, passwords)} />
                    <br/>
                    <label htmlFor="password">Password:</label>
                    <input id="password" type='password' value={passwords.password1} onChange={(event) => onPasswordsFieldChange({ password1: event.target.value }, passwords)} />
                    <br/>
                    <label htmlFor="password2">Repeat password:</label>
                    <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                    { passwordsNotIdentical && <span>Passordene er ikke identiske</span> }
                    <br/>
                    <button onClick={() => onAddUser(passwords)}>Opprett bruker</button>
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
