import React from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    USER_UPDATE,
    USER_ADD_ROLES,
    USER_REMOVE_ROLES,
    ROLES_REQUEST,
    ROLES_RECEIVED,
    USERROLES_REQUEST,
    FORMFIELD_UPDATE,
} from '../actiontypes';
import RoleList from './RoleList';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { ChevronLeft } from './bootstrap/ChevronLeft';
import { ChevronRight } from './bootstrap/ChevronRight';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserRoles(props) {
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
        onUsersChange,
        onRolesNotOnUserChange,
        onAddRole,
        onRolesOnUserChange,
        onRemoveRole,
        onFieldChange,
        onSaveUpdatedUser,
    } = props;
    let {
        rolesNotOnUserSelected,
        rolesNotOnUserSelectedNames,
        rolesOnUserSelected,
        rolesOnUserSelectedNames,
    } = formfield;

    return (
        <div>
            <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft><br/>
            <Header>
                <h1>Modify user to role mappings</h1>
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
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="username">Roles not on user</label>
                            <RoleList id="rolesnotonuser" className="form-control" roles={rolesNotOnUser} rolesMap={rolesNotOnUserMap} value={rolesNotOnUserSelectedNames} onRolesFieldChange={onRolesNotOnUserChange} />
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button className="btn btn-primary form-control" onClick={() => onAddRole(user, rolesOnUser, rolesNotOnUserSelected)}>Add role &nbsp;<ChevronRight/></button>
                            <button className="btn btn-primary form-control" onClick={() => onRemoveRole(user, rolesOnUserSelected)}><ChevronLeft/>&nbsp; Remove role</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="email">Role on user</label>
                            <RoleList id="rolesnotonuser" className="form-control" roles={rolesOnUser} rolesMap={rolesOnUserMap} value={rolesOnUserSelectedNames} onRolesFieldChange={onRolesOnUserChange} />
                        </div>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
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
        onUsers: () => dispatch(USERS_REQUEST()),
        onRoles: () => dispatch(ROLES_REQUEST()),
        onUserRoles: () => dispatch(USERROLES_REQUEST()),
        onUsersChange: (e, users) => {
            const userid = parseInt(e.target.value, 10);
            let user = users.find(u => u.userid === userid);
            dispatch(USER_UPDATE({ ...user }));
        },
        onRolesNotOnUserChange: (rolesNotOnUserSelectedNames, roleMap) => {
            const rolesNotOnUserSelected = roleMap.get(rolesNotOnUserSelectedNames);
            const payload = { rolesNotOnUserSelected, rolesNotOnUserSelectedNames };
            dispatch(FORMFIELD_UPDATE(payload));
        },
        onAddRole: (user, rolesOnUser, rolesNotOnUserSelected) => dispatch(USER_ADD_ROLES({ user, rolesNotOnUserSelected })),
        onRolesOnUserChange: (rolesOnUserSelectedNames, roleMap) => {
            const rolesOnUserSelected = roleMap.get(rolesOnUserSelectedNames);
            const payload = { rolesOnUserSelected, rolesOnUserSelectedNames };
            dispatch(FORMFIELD_UPDATE(payload));
        },
        onRemoveRole: (user, rolesOnUserSelected) => dispatch(USER_REMOVE_ROLES({ user, rolesOnUserSelected })),
    };
};

UserRoles = connect(mapStateToProps, mapDispatchToProps)(UserRoles);

export default UserRoles;
