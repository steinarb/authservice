import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
    ROLE_UPDATE,
    ROLE_CLEAR,
    PERMISSIONS_REQUEST,
    ROLEPERMISSIONS_REQUEST,
    ROLE_ADD_PERMISSIONS,
    ROLE_REMOVE_PERMISSIONS,
    FORMFIELD_UPDATE,
} from '../actiontypes';
import PermissionList from './PermissionList';
import { Header } from './bootstrap/Header';
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
        permissionsNotOnRoleMap,
        permissionsOnRole,
        permissionsOnRoleMap,
        formfield,
        onRolesChange,
        onPermissionsNotOnRoleChange,
        onAddPermission,
        onPermissionsOnRoleChange,
        onRemovePermission,
        onFieldChange,
        onSaveUpdatedRole,
    } = props;
    const {
        permissionsNotOnRoleSelected,
        permissionsNotOnRoleSelectedNames,
        permissionsOnRoleSelected,
        permissionsOnRoleSelectedNames,
    } = formfield;

    return (
        <div>
            <StyledLinkLeft to="/authservice/useradmin/roles">Up to role adminstration</StyledLinkLeft><br/>
            <Header>
                <h1>Modify role to permission mappings</h1>
            </Header>
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
                            <PermissionList id="permissionsnotonrole" className="form-control" permissions={permissionsNotOnRole} permissionsMap={permissionsNotOnRoleMap} value={permissionsNotOnRoleSelectedNames} onPermissionsFieldChange={onPermissionsNotOnRoleChange} />
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button className="btn btn-primary form-control" onClick={() => onAddPermission(role, permissionsOnRole, permissionsNotOnRoleSelected)}>Add permission &nbsp;<ChevronRight/></button>
                            <button className="btn btn-primary form-control" onClick={() => onRemovePermission(role, permissionsOnRoleSelected)}><ChevronLeft/>&nbsp; Remove permission</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsonrole">Permission on role</label>
                            <PermissionList id="permissionsonrole" className="form-control" permissions={permissionsOnRole} permissionsMap={permissionsOnRoleMap} value={permissionsOnRoleSelectedNames} onPermissionsFieldChange={onPermissionsOnRoleChange} />
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
    const permissionsOnRole = state.rolepermissions[state.role.rolename] || [];
    const permissionsOnRoleMap = new Map(permissionsOnRole.map(i => [i.permissionname, i]));
    const permissionsNotOnRole = findPermissionsNotOnRole(state.role, state.permissions, permissionsOnRole);
    const permissionsNotOnRoleMap = new Map(permissionsNotOnRole.map(i => [i.permissionname, i]));
    return {
        roles: state.roles,
        rolesMap: new Map(state.roles.map(i => [i.rolename, i])),
        role: state.role,
        permissions: state.permissions,
        rolepermissions: state.rolepermissions,
        formfield: state.formfield || {},
        permissionsNotOnRole,
        permissionsNotOnRoleMap,
        permissionsOnRole,
        permissionsOnRoleMap,
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
        onPermissionsNotOnRoleChange: (permissionsNotOnRoleSelectedNames, permissionMap) => {
            const permissionsNotOnRoleSelected = permissionMap.get(permissionsNotOnRoleSelectedNames);
            const payload = { permissionsNotOnRoleSelected, permissionsNotOnRoleSelectedNames };
            dispatch(FORMFIELD_UPDATE(payload));
        },
        onAddPermission: (role, permissionsOnRole, permissionsNotOnRoleSelected) => dispatch(ROLE_ADD_PERMISSIONS({ role, permissionsNotOnRoleSelected })),
        onPermissionsOnRoleChange: (permissionsOnRoleSelectedNames, permissionMap) => {
            const permissionsOnRoleSelected = permissionMap.get(permissionsOnRoleSelectedNames);
            const payload = { permissionsOnRoleSelected, permissionsOnRoleSelectedNames };
            dispatch(FORMFIELD_UPDATE(payload));
        },
        onRemovePermission: (role, permissionsOnRoleSelected) => dispatch(ROLE_REMOVE_PERMISSIONS({ role, permissionsOnRoleSelected })),
    };
};

RolePermissions = connect(mapStateToProps, mapDispatchToProps)(RolePermissions);

export default RolePermissions;
