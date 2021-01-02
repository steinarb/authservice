import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    USER_UPDATE,
    PASSWORDS_UPDATE,
    PASSWORDS_MODIFY,
} from '../actiontypes';
import { emptyUserAndPasswords } from '../constants';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class UserChangePasswords extends Component {
    componentDidMount() {
        this.props.onUsers();
    }

    render () {
        let {
            user,
            users,
            passwords = emptyUserAndPasswords,
            passwordsNotIdentical,
            onUsersChange,
            onPasswordsFieldChange,
            onSaveUpdatedPassword,
        } = this.props;

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
                                <select id="users" className="form-control" onChange={e => onUsersChange(e, users)} value={user.userid}>
                                    {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                                </select>
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
                                <button className="btn btn-primary form-control" onClick={() => onSaveUpdatedPassword(passwords)}>Change password</button>
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
        user: state.user,
        users: state.users,
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
        onUsers: () => dispatch(USERS_REQUEST()),
        onUsersChange: (e, users) => {
            const userid = parseInt(e.target.value, 10);
            let user = users.find(u => u.userid === userid);
            dispatch(USER_UPDATE({ ...user }));
        },
        onPasswordsFieldChange: (formValue, passwordsFromState) => {
            const passwords = { ...passwordsFromState, ...formValue };
            const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
            let changedField = {
                ...passwords,
                passwordsNotIdentical,
            };
            dispatch(PASSWORDS_UPDATE(changedField));
        },
        onSaveUpdatedPassword: (userAndPasswords) => dispatch(PASSWORDS_MODIFY(userAndPasswords)),
    };
};

UserChangePasswords = connect(mapStateToProps, mapDispatchToProps)(UserChangePasswords);

export default UserChangePasswords;
