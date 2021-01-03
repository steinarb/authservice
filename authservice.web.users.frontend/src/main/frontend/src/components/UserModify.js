import React from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    USER_UPDATE,
    USER_MODIFY,
} from '../actiontypes';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserModify(props) {
    const {
        users,
        user,
        onUsersChange,
        onUsername,
        onEmail,
        onFirstname,
        onLastname,
        onSaveUpdatedUser,
    } = props;

    return (
        <div>
            <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft>
            <Header>
                <h1>Modify user information</h1>
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
                        <div className="col-5"/>
                        <FormField>
                            <button className="btn btn-primary form-control" onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        users: state.users,
        user: state.user,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => dispatch(USERS_REQUEST()),
        onUsersChange: (e, users) => {
            const userid = parseInt(e.target.value, 10);
            const user = users.find(u => u.userid === userid);
            dispatch(USER_UPDATE({ ...user }));
        },
        onUsername: e => dispatch(USER_UPDATE({ username: e.target.value })),
        onEmail: e => dispatch(USER_UPDATE({ email: e.target.value })),
        onFirstname: e => dispatch(USER_UPDATE({ firstname: e.target.value })),
        onLastname: e => dispatch(USER_UPDATE({ lastname: e.target.value })),
        onSaveUpdatedUser: (user) => dispatch(USER_MODIFY(user)),
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
