import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { USERS_RECEIVED, USERS_ERROR, PASSWORDS_UPDATE } from './actiontypes';
import UserSelect from './components/UserSelect';
import { emptyUserAndPasswords } from './constants';

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
                <h1>Change password for user</h1>
                <br/>
                <Link to="/authservice/useradmin/users">Up to user adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="users">Select user</label>
                    <UserSelect id="users" users={users} usersMap={usersMap} value={passwords.user.fullname} onUsersFieldChange={onUsersFieldChange} />
                    <br/>
                    <label htmlFor="password">Password:</label>
                    <input id="password" type='password' value={passwords.password1} onChange={(event) => onPasswordsFieldChange({ password1: event.target.value }, passwords)} />
                    <br/>
                    <label htmlFor="password2">Repeat password:</label>
                    <input id="password2" type='password' value={passwords.password2} onChange={(event) => onPasswordsFieldChange({ password2: event.target.value }, passwords)} />
                    { passwordsNotIdentical && <span>Passordene er ikke identiske</span> }
                    <br/>
                    <button onClick={() => onSaveUpdatedPassword(passwords)}>Change password</button>
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
