import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    SELECT_USER,
    PASSWORD1_FIELD_MODIFIED,
    PASSWORD2_FIELD_MODIFIED,
    CHANGE_PASSWORD_BUTTON_CLICKED,
    USER_CLEAR,
    PASSWORDS_CLEAR,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserChangePasswords(props) {
    const {
        userid,
        users,
        password1,
        password2,
        passwordsNotIdentical,
        onUsersChange,
        onPassword1,
        onPassword2,
        onSaveUpdatedPassword,
        onUsers,
        onUserClear,
        onPasswordsClear,
    } = props;

    useEffect(() => {
        onUsers();
        onUserClear();
        onPasswordsClear();
    },[onUsers, onUserClear, onPasswordsClear]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
                <h1>Change password for user</h1>
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
                        <FormField>
                            <button className="btn btn-primary form-control" onClick={onSaveUpdatedPassword}>Change password</button>
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
        users: state.users,
        password1: state.password1,
        password2: state.password2,
        passwordsNotIdentical: state.passwordsNotIdentical,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => dispatch(USERS_REQUEST()),
        onUserClear: () => dispatch(USER_CLEAR()),
        onPasswordsClear: () => dispatch(PASSWORDS_CLEAR()),
        onUsersChange: e => dispatch(SELECT_USER(parseInt(e.target.value))),
        onPassword1: e => dispatch(PASSWORD1_FIELD_MODIFIED(e.target.value)),
        onPassword2: e => dispatch(PASSWORD2_FIELD_MODIFIED(e.target.value)),
        onSaveUpdatedPassword: () => dispatch(CHANGE_PASSWORD_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(UserChangePasswords);
