import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    USERS_REQUEST,
    USER_CLEAR,
    SELECT_USER,
    ROLES_REQUEST,
    USERROLES_REQUEST,
    SELECT_ROLES_NOT_ON_USER,
    SELECT_ROLES_ON_USER,
    ADD_USER_ROLE_BUTTON_CLICKED,
    REMOVE_USER_ROLE_BUTTON_CLICKED,

} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { ChevronLeft } from './bootstrap/ChevronLeft';
import { ChevronRight } from './bootstrap/ChevronRight';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';
import { isUnselected } from '../reducers/common';

function UserRoles(props) {
    const {
        users,
        userid,
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

    const addRoleDisabled = isUnselected(selectedInRolesNotOnUser);
    const removeRoleDisabled = isUnselected(selectedInRolesOnUser);

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
                            <select id="users" className="form-control" onChange={onUsersChange} value={userid}>
                                <option key="-1" value="-1" />
                                {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesnotonuser">Roles not on user</label>
                            <select id="rolesnotonuser" className="form-control" multiselect="true" size="10" onChange={onRolesNotOnUserSelected} value={selectedInRolesNotOnUser}>
                                <option key="-1" value="-1" />
                                {rolesNotOnUser.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button disabled={addRoleDisabled} className="btn btn-primary form-control" onClick={onAddRole}>Add role &nbsp;<ChevronRight/></button>
                            <button disabled={removeRoleDisabled} className="btn btn-primary form-control" onClick={onRemoveRole}><ChevronLeft/>&nbsp; Remove role</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesonuser">Role on user</label>
                            <select id="rolesonuser" className="form-control" multiselect="true" size="10" onChange={onRolesOnUserSelected} value={selectedInRolesOnUser}>
                                <option key="-1" value="-1" />
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
        userid: state.userid,
        rolesNotOnUser: state.rolesNotOnUser,
        selectedInRolesNotOnUser: state.selectedInRolesNotOnUser,
        rolesOnUser: state.rolesOnUser,
        selectedInRolesOnUser: state.selectedInRolesOnUser,
    };
}

const mapDispatchToProps = dispatch => {
    return {
        onUsers: () => dispatch(USERS_REQUEST()),
        onEmptyUser: () => dispatch(USER_CLEAR()),
        onRoles: () => dispatch(ROLES_REQUEST()),
        onUserRoles: () => dispatch(USERROLES_REQUEST()),
        onUsersChange: e => dispatch(SELECT_USER(parseInt(e.target.value))),
        onRolesNotOnUserSelected: e => dispatch(SELECT_ROLES_NOT_ON_USER(parseInt(e.target.value, 10))),
        onAddRole: () => dispatch(ADD_USER_ROLE_BUTTON_CLICKED()),
        onRolesOnUserSelected: e => dispatch(SELECT_ROLES_ON_USER(parseInt(e.target.value, 10))),
        onRemoveRole: () => dispatch(REMOVE_USER_ROLE_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(UserRoles);
