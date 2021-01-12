import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    USER_UPDATE,
    USER_CLEAR,
    PASSWORDS_UPDATE,
    PASSWORDS_CLEAR,
    PASSWORDS_MODIFY,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserChangePasswords(props) {
    const {
        user,
        users,
        passwords,
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
                            <select id="users" className="form-control" onChange={e => onUsersChange(e, users)} value={user.userid}>
                                {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                            </select>
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
                        <FormField>
                            <button className="btn btn-primary form-control" onClick={() => onSaveUpdatedPassword(passwords, user)}>Change password</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
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
    const { password1, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password1 !== password2;
};

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => dispatch(USERS_REQUEST()),
        onUserClear: () => dispatch(USER_CLEAR()),
        onPasswordsClear: () => dispatch(PASSWORDS_CLEAR()),
        onUsersChange: (e, users) => {
            const userid = parseInt(e.target.value, 10);
            const user = users.find(u => u.userid === userid);
            dispatch(USER_UPDATE({ ...user }));
        },
        onPassword1: e => dispatch(PASSWORDS_UPDATE({ password1: e.target.value })),
        onPassword2: e => dispatch(PASSWORDS_UPDATE({ password2: e.target.value })),
        onSaveUpdatedPassword: (passwords, user) => dispatch(PASSWORDS_MODIFY({ ...passwords, user })),
    };
};

UserChangePasswords = connect(mapStateToProps, mapDispatchToProps)(UserChangePasswords);

export default UserChangePasswords;
