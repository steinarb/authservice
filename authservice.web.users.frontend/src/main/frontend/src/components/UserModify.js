import React, { Component } from 'react';
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

class UserModify extends Component {
    componentDidMount() {
        this.props.onUsers();
    }

    render () {
        let {
            users,
            user,
            onUsersChange,
            onFieldChange,
            onSaveUpdatedUser,
        } = this.props;

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
                                <select id="users" className="form-control" onChange={e => onUsersChange(e.target.value, users)} value={user.userid}>
                                    {users.map((val) => <option key={val.userid}>{val.firstname} {val.lastname}</option>)}
                                </select>
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="username">Username</FormLabel>
                            <FormField>
                                <input id="username" className="form-control" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Email address</FormLabel>
                            <FormField>
                                <input id="email" className="form-control" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="firstname">First name</FormLabel>
                            <FormField>
                                <input id="firstname" className="form-control" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="lastname">Last name</FormLabel>
                            <FormField>
                                <input id="lastname" className="form-control" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
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
        onUsersChange: (userid, users) => {
            let user = users.find(u => u.userid === userid);
            dispatch(USER_UPDATE({ ...user }));
        },
        onFieldChange: (formValue, originalUser) => {
            const user = { ...originalUser, ...formValue };
            dispatch(USER_UPDATE(user));
        },
        onSaveUpdatedUser: (user) => dispatch(USER_MODIFY(user)),
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
