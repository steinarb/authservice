import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    SELECT_USER,
    USERNAME_FIELD_MODIFIED,
    EMAIL_FIELD_MODIFIED,
    FIRSTNAME_FIELD_MODIFIED,
    LASTNAME_FIELD_MODIFIED,
    MODIFY_USER_BUTTON_CLICKED,
    USERS_REQUEST,
    USER_CLEAR,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserModify(props) {
    const {
        userid,
        username,
        email,
        firstname,
        lastname,
        users,
        onUsersChange,
        onUsername,
        onEmail,
        onFirstname,
        onLastname,
        onSaveUpdatedUser,
        onUsers,
        onUserClear,
    } = props;

    useEffect(() => {
        onUsers();
        onUserClear();
    },[onUsers, onUserClear]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <h1>Modify user information</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="users">Select user</FormLabel>
                        <FormField>
                            <select id="users" className="form-control" onChange={onUsersChange} value={userid}>
                                <option key="-1" value="-1" />
                                {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
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
                        <div className="col-5"/>
                        <FormField>
                            <button className="btn btn-primary form-control" onClick={onSaveUpdatedUser}>Lagre endringer av bruker</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        userid: state.userid,
        username: state.username,
        email: state.email,
        firstname: state.firstname,
        lastname: state.lastname,
        users: state.users,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => dispatch(USERS_REQUEST()),
        onUserClear: () => dispatch(USER_CLEAR()),
        onUsersChange: e => dispatch(SELECT_USER(parseInt(e.target.value))),
        onUsername: e => dispatch(USERNAME_FIELD_MODIFIED(e.target.value)),
        onEmail: e => dispatch(EMAIL_FIELD_MODIFIED(e.target.value)),
        onFirstname: e => dispatch(FIRSTNAME_FIELD_MODIFIED(e.target.value)),
        onLastname: e => dispatch(LASTNAME_FIELD_MODIFIED(e.target.value)),
        onSaveUpdatedUser: () => dispatch(MODIFY_USER_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(UserModify);
