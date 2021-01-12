import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
    ROLE_UPDATE,
    ROLE_CLEAR,
    PERMISSIONS_REQUEST,
    ROLEPERMISSIONS_REQUEST,
    PERMISSIONS_NOT_ON_ROLE_SELECTED,
    ROLE_ADD_PERMISSIONS,
    PERMISSIONS_ON_ROLE_SELECTED,
    ROLE_REMOVE_PERMISSIONS,
} from '../actiontypes';
import { emptyPermission } from '../constants';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { ChevronLeft } from './bootstrap/ChevronLeft';
import { ChevronRight } from './bootstrap/ChevronRight';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function RolePermissions(props) {
    useEffect(() => {
        props.onRoles();
        props.onRoleClear();
        props.onPermissions();
        props.onRolePermissions();
    },[]);

    const {
        roles,
        role,
        rolepermissions,
        permissions,
        permissionsNotOnRole,
        selectedInPermissionsNotOnRole,
        permissionsOnRole,
        selectedInPermissionsOnRole,
        onRolesChange,
        onPermissionsNotOnRoleSelected,
        onAddPermission,
        onPermissionsOnRoleSelected,
        onRemovePermission,
    } = props;
    const addPermissionDisabled = selectedInPermissionsNotOnRole === emptyPermission.id;
    const removePermissionDisabled = selectedInPermissionsOnRole === emptyPermission.id;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/roles">Up to role adminstration</StyledLinkLeft><br/>
                <h1>Modify role to permission mappings</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="roles">Select role</FormLabel>
                        <FormField>
                            <select id="roles" className="form-control" onChange={e => onRolesChange(e, roles)} value={role.id}>
                                {roles.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsnotonrole">Permissions not on role</label>
                            <select id="permissionsnotonrole" className="form-control" multiselect="true" size="10" onChange={onPermissionsNotOnRoleSelected} value={selectedInPermissionsNotOnRole}>
                                {permissionsNotOnRole.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button disabled={addPermissionDisabled} className="btn btn-primary form-control" onClick={() => onAddPermission(selectedInPermissionsNotOnRole)}>Add permission &nbsp;<ChevronRight/></button>
                            <button disabled={removePermissionDisabled} className="btn btn-primary form-control" onClick={() => onRemovePermission(selectedInPermissionsOnRole)}><ChevronLeft/>&nbsp; Remove permission</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsonrole">Permission on role</label>
                            <select id="permissionsonrole" className="form-control" multiselect="true" size="10" onChange={onPermissionsOnRoleSelected} value={selectedInPermissionsOnRole}>
                                {permissionsOnRole.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </div>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

function findPermissionsNotOnRole(role, permissions, permissionsOnRole) {
    const permissionsOnRolePermissionnames = permissionsOnRole.map(permission => permission.permissionname);
    const permissionsNotOnRole = permissions.filter(permission => !permissionsOnRolePermissionnames.includes(permission.permissionname));
    return permissionsNotOnRole;
}

const mapStateToProps = (state) => {
    return {
        roles: state.roles,
        role: state.role,
        permissions: state.permissions,
        rolepermissions: state.rolepermissions,
        permissionsNotOnRole: state.permissionsNotOnRole,
        selectedInPermissionsNotOnRole: state.selectedInPermissionsNotOnRole,
        permissionsOnRole: state.permissionsOnRole,
        selectedInPermissionsOnRole: state.selectedInPermissionsOnRole,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoles: () => dispatch(ROLES_REQUEST()),
        onRoleClear: () => dispatch(ROLE_CLEAR()),
        onPermissions: () => dispatch(PERMISSIONS_REQUEST()),
        onRolePermissions: () => dispatch(ROLEPERMISSIONS_REQUEST()),
        onRolesChange: (e, roles) => {
            const id = parseInt(e.target.value, 10);
            const role = roles.find(r => r.id === id);
            dispatch(ROLE_UPDATE(role));
        },
        onPermissionsNotOnRoleSelected: e => dispatch(PERMISSIONS_NOT_ON_ROLE_SELECTED(parseInt(e.target.value, 10))),
        onAddPermission: permissionid => dispatch(ROLE_ADD_PERMISSIONS(permissionid)),
        onPermissionsOnRoleSelected: e => dispatch(PERMISSIONS_ON_ROLE_SELECTED(parseInt(e.target.value, 10))),
        onRemovePermission: permissionid => dispatch(ROLE_REMOVE_PERMISSIONS(permissionid)),
    };
};

RolePermissions = connect(mapStateToProps, mapDispatchToProps)(RolePermissions);

export default RolePermissions;
