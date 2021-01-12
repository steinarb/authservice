import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    USER_UPDATE,
    USER_CLEAR,
    ROLES_REQUEST,
    USERROLES_REQUEST,
    ROLES_NOT_ON_USER_SELECTED,
    ROLES_ON_USER_SELECTED,
    USER_ADD_ROLES,
    USER_REMOVE_ROLES,
} from '../actiontypes';
import { emptyRole } from '../constants';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { ChevronLeft } from './bootstrap/ChevronLeft';
import { ChevronRight } from './bootstrap/ChevronRight';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function UserRoles(props) {
    const {
        users,
        user,
        rolesNotOnUser,
        selectedInRolesNotOnUser,
        rolesOnUser,
        selectedInRolesOnUser,
        onUsersChange,
        onRolesNotOnUserSelected,
        onAddRole,
        onRolesOnUserSelected,
        onRemoveRole,
        onUsers,
        onEmptyUser,
        onRoles,
        onUserRoles,
    } = props;

    useEffect(() => {
        onUsers();
        onEmptyUser();
        onRoles();
        onUserRoles();
    }, [onUsers, onEmptyUser, onRoles, onUserRoles]);

    const addRoleDisabled = selectedInRolesNotOnUser === emptyRole.id;
    const removeRoleDisabled = selectedInRolesOnUser === emptyRole.id;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft><br/>
                <h1>Modify user to role mappings</h1>
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
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesnotonuser">Roles not on user</label>
                            <select id="rolesnotonuser" className="form-control" multiselect="true" size="10" onChange={onRolesNotOnUserSelected} value={selectedInRolesNotOnUser}>
                                {rolesNotOnUser.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button disabled={addRoleDisabled} className="btn btn-primary form-control" onClick={() => onAddRole(selectedInRolesNotOnUser)}>Add role &nbsp;<ChevronRight/></button>
                            <button disabled={removeRoleDisabled} className="btn btn-primary form-control" onClick={() => onRemoveRole(selectedInRolesOnUser)}><ChevronLeft/>&nbsp; Remove role</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesonuser">Role on user</label>
                            <select id="rolesonuser" className="form-control" multiselect="true" size="10" onChange={onRolesOnUserSelected} value={selectedInRolesOnUser}>
                                {rolesOnUser.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </div>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        users: state.users,
        user: state.user,
        rolesNotOnUser: state.rolesNotOnUser,
        selectedInRolesNotOnUser: state.selectedInRolesNotOnUser,
        rolesOnUser: state.rolesOnUser,
        selectedInRolesOnUser: state.selectedInRolesOnUser,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => dispatch(USERS_REQUEST()),
        onEmptyUser: () => dispatch(USER_CLEAR()),
        onRoles: () => dispatch(ROLES_REQUEST()),
        onUserRoles: () => dispatch(USERROLES_REQUEST()),
        onUsersChange: (e, users) => {
            const userid = parseInt(e.target.value, 10);
            const user = users.find(u => u.userid === userid);
            dispatch(USER_UPDATE({ ...user }));
        },
        onRolesNotOnUserSelected: e => dispatch(ROLES_NOT_ON_USER_SELECTED(parseInt(e.target.value, 10))),
        onAddRole: roleid => dispatch(USER_ADD_ROLES(roleid)),
        onRolesOnUserSelected: e => dispatch(ROLES_ON_USER_SELECTED(parseInt(e.target.value, 10))),
        onRemoveRole: roleid => dispatch(USER_REMOVE_ROLES(roleid)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(UserRoles);
