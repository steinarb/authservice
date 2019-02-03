import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { USERS_RECEIVED, USERS_ERROR, USER_UPDATE } from './actiontypes';
import UserSelect from './components/UserSelect';

class UserModify extends Component {
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
            user,
            onUsersFieldChange,
            onFieldChange,
            onSaveUpdatedUser,
        } = this.state;

        return (
            <div>
                <h1>Modify user information</h1>
                <br/>
                <Link to="/authservice/useradmin/users">Up to user adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="users">Select user</label>
                    <UserSelect id="users" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                    <br/>
                    <label htmlFor="username">Username</label>
                    <input id="username" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="email">Email address</label>
                    <input id="email" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="firstname">First name</label>
                    <input id="firstname" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="lastname">Last name</label>
                    <input id="lastname" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
                    <br/>
                    <button onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        users: state.users,
        usersMap: new Map(state.users.map(i => [i.firstname + ' ' + i.lastname, i])),
        user: state.user,
    };
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
            let user = usersMap.get(selectedValue);
            dispatch({ type: USER_UPDATE, payload: user });
        },
        onFieldChange: (formValue, originalUser) => {
            const user = { ...originalUser, ...formValue };
            dispatch({ type: USER_UPDATE, payload: user });
        },
        onSaveUpdatedUser: (user) => {
            axios
                .post('/authservice/useradmin/api/user/modify', user)
                .then(result => dispatch({ type: USERS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERS_ERROR, payload: error }));
            dispatch({ type: USER_UPDATE, payload: { user: {} } });
        },
    };
};

UserModify = connect(mapStateToProps, mapDispatchToProps)(UserModify);

export default UserModify;
