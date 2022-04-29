import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    USERNAME_FIELD_MODIFIED,
    EMAIL_FIELD_MODIFIED,
    FIRSTNAME_FIELD_MODIFIED,
    LASTNAME_FIELD_MODIFIED,
    PASSWORD1_FIELD_MODIFIED,
    PASSWORD2_FIELD_MODIFIED,
    ADD_USER_BUTTON_CLICKED,
    USER_CLEAR,
    PASSWORDS_CLEAR,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserAdd(props) {
    const {
        username,
        email,
        firstname,
        lastname,
        password1,
        password2,
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
                            <input id="username" className="form-control" type="text" value={username} onChange={onUsername} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Email address</FormLabel>
                        <FormField>
                            <input id="email" className="form-control" type="text" value={email} onChange={onEmail} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="firstname">First name</FormLabel>
                        <FormField>
                            <input id="firstname" className="form-control" type="text" value={firstname} onChange={onFirstname} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="lastname">Last name</FormLabel>
                        <FormField>
                            <input id="lastname" className="form-control" type="text" value={lastname} onChange={onLastname} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password">Password:</FormLabel>
                        <FormField>
                            <input id="password" className="form-control" type='password' value={password1} onChange={onPassword1} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="password2">Repeat password:</FormLabel>
                        <FormField>
                            <input id="password2" className="form-control" type='password' value={password2} onChange={onPassword2} />
                            { passwordsNotIdentical && <span>Passwords are not identical!</span> }
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button className="btn btn-primary form-control" onClick={onAddUser}>Opprett bruker</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        username: state.username,
        email: state.email,
        firstname: state.firstname,
        lastname: state.lastname,
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUserClear: () => dispatch(USER_CLEAR()),
        onPasswordsClear: () => dispatch(PASSWORDS_CLEAR()),
        onUsername: e => dispatch(USERNAME_FIELD_MODIFIED(e.target.value)),
        onEmail: e => dispatch(EMAIL_FIELD_MODIFIED(e.target.value)),
        onFirstname: e => dispatch(FIRSTNAME_FIELD_MODIFIED(e.target.value)),
        onLastname: e => dispatch(LASTNAME_FIELD_MODIFIED(e.target.value)),
        onPassword1: e => dispatch(PASSWORD1_FIELD_MODIFIED(e.target.value)),
        onPassword2: e => dispatch(PASSWORD2_FIELD_MODIFIED(e.target.value)),
        onAddUser: () => dispatch(ADD_USER_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(UserAdd);
