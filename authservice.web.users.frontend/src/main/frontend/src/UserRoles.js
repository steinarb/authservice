import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { USERS_RECEIVED, USERS_ERROR, USER_UPDATE, ROLES_RECEIVED, ROLES_ERROR, USERROLES_RECEIVED, USERROLES_ERROR, FORMFIELD_UPDATE } from './actiontypes';
import UserSelect from './components/UserSelect';
import RoleList from './components/RoleList';

class UserRoles extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onUsers();
        this.props.onRoles();
        this.props.onUserRoles();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            users,
            usersMap,
            user,
            userroles,
            roles,
            rolesNotOnUser,
            rolesNotOnUserMap,
            rolesOnUser,
            rolesOnUserMap,
            formfield,
            onUsersFieldChange,
            onRolesNotOnUserChange,
            onAddRole,
            onRolesOnUserChange,
            onRemoveRole,
            onFieldChange,
            onSaveUpdatedUser,
        } = this.state;
        let {
            rolesNotOnUserSelected,
            rolesNotOnUserSelectedNames,
            rolesOnUserSelected,
            rolesOnUserSelectedNames,
        } = formfield;

        return (
            <div>
                <h1>Modify user information</h1>
                <br/>
                <Link to="/authservice/useradmin/users">Up to user adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="users">Select user</label>
                    <UserSelect id="users" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                    <br/>
                    <label htmlFor="username">Roles not on user</label>
                    <RoleList id="rolesnotonuser" roles={rolesNotOnUser} rolesMap={rolesNotOnUserMap} value={rolesNotOnUserSelectedNames} onRolesFieldChange={onRolesNotOnUserChange} />
                    <br/>
                    <button onClick={() => onAddRole(user, rolesOnUser, rolesNotOnUserSelected)}>Add role</button>
                    <br/>
                    <button onClick={() => onRemoveRole(user, rolesOnUserSelected)}>Remove role</button>
                    <br/>
                    <label htmlFor="email">Role on user</label>
                    <RoleList id="rolesnotonuser" roles={rolesOnUser} rolesMap={rolesOnUserMap} value={rolesOnUserSelectedNames} onRolesFieldChange={onRolesOnUserChange} />
                </form>
            </div>
        );
    }
}

function findRolesNotOnUser(user, roles, rolesOnUser) {
    const rolesOnUserRolenames = rolesOnUser.map(role => role.rolename);
    const rolesNotOnUser = roles.filter(role => !rolesOnUserRolenames.includes(role.rolename));
    return rolesNotOnUser;
}

const mapStateToProps = (state) => {
    const rolesOnUser = state.userroles[state.user.username] || [];
    const rolesOnUserMap = new Map(rolesOnUser.map(i => [i.rolename, i]));
    const rolesNotOnUser = findRolesNotOnUser(state.user, state.roles, rolesOnUser);
    const rolesNotOnUserMap = new Map(rolesNotOnUser.map(i => [i.rolename, i]));
    return {
        users: state.users,
        usersMap: new Map(state.users.map(i => [i.firstname + ' ' + i.lastname, i])),
        user: state.user,
        roles: state.roles,
        userroles: state.userroles,
        formfield: state.formfield || {},
        rolesNotOnUser,
        rolesNotOnUserMap,
        rolesOnUser,
        rolesOnUserMap,
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
        onRoles: () => {
            axios
                .get('/authservice/useradmin/api/roles')
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
        },
        onUserRoles: () => {
            axios
                .get('/authservice/useradmin/api/users/roles')
                .then(result => dispatch({ type: USERROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERROLES_ERROR, payload: error }));
        },
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            dispatch({ type: USER_UPDATE, payload: user });
        },
        onRolesNotOnUserChange: (rolesNotOnUserSelectedNames, roleMap) => {
            const rolesNotOnUserSelected = roleMap.get(rolesNotOnUserSelectedNames);
            const payload = { rolesNotOnUserSelected, rolesNotOnUserSelectedNames };
            dispatch({ type: FORMFIELD_UPDATE, payload });
        },
        onAddRole: (user, rolesOnUser, rolesNotOnUserSelected) => {
            const roles = [ rolesNotOnUserSelected ];
            const userwithroles = { user, roles };
            axios
                .post('/authservice/useradmin/api/user/addroles', userwithroles)
                .then(result => dispatch({ type: USERROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERROLES_ERROR, payload: error }));
        },
        onRolesOnUserChange: (rolesOnUserSelectedNames, roleMap) => {
            const rolesOnUserSelected = roleMap.get(rolesOnUserSelectedNames);
            const payload = { rolesOnUserSelected, rolesOnUserSelectedNames };
            dispatch({ type: FORMFIELD_UPDATE, payload });
        },
        onRemoveRole: (user, rolesOnUserSelected) => {
            const roles = [ rolesOnUserSelected ];
            const userwithroles = { user, roles };
            axios
                .post('/authservice/useradmin/api/user/removeroles', userwithroles)
                .then(result => dispatch({ type: USERROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: USERROLES_ERROR, payload: error }));
        },
    };
};

UserRoles = connect(mapStateToProps, mapDispatchToProps)(UserRoles);

export default UserRoles;
