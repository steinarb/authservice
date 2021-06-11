import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    USER_UPDATE,
    USER_CLEAR,
    PASSWORDS_UPDATE,
    PASSWORDS_CLEAR,
    USER_ADD,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserAdd(props) {
    const {
        user,
        passwords,
        passwordsNotIdentical,
        onUsername,
        onEmail,
        onFirstname,
        onLastname,
        onPassword1,
        onPassword2,
        onAddUser,
        onUserClear,
        onPasswordsClear,
    } = props;

    useEffect(() => {
        onUserClear();
        onPasswordsClear();
    },[onUserClear, onPasswordsClear]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <h1>Add user</h1>
                <div className="col-sm-2"></div>
            </nav>
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
                            <input id="password" className="form-control" type='password' value={passwords.password1} onChange={onPassword1} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password2">Repeat password:</FormLabel>
                        <FormField>
                            <input id="password2" className="form-control" type='password' value={passwords.password2} onChange={onPassword2} />
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

const mapStateToProps = (state) => {
    return {
        user: state.user,
        passwords: state.passwords,
        passwordsNotIdentical: state.passwords.passwordsNotIdentical,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUserClear: () => dispatch(USER_CLEAR()),
        onPasswordsClear: () => dispatch(PASSWORDS_CLEAR()),
        onUsername: e => dispatch(USER_UPDATE({ username: e.target.value })),
        onEmail: e => dispatch(USER_UPDATE({ email: e.target.value })),
        onFirstname: e => dispatch(USER_UPDATE({ firstname: e.target.value })),
        onLastname: e => dispatch(USER_UPDATE({ lastname: e.target.value })),
        onPassword1: e => dispatch(PASSWORDS_UPDATE({ password1: e.target.value })),
        onPassword2: e => dispatch(PASSWORDS_UPDATE({ password2: e.target.value })),
        onAddUser: (passwords, user) => dispatch(USER_ADD({ ...passwords, user })),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(UserAdd);
