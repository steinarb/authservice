import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { ROLES_RECEIVED, ROLES_ERROR, ROLE_UPDATE, PERMISSIONS_RECEIVED, PERMISSIONS_ERROR, ROLEPERMISSIONS_RECEIVED, ROLEPERMISSIONS_ERROR, FORMFIELD_UPDATE } from './actiontypes';
import RoleSelect from './components/RoleSelect';
import PermissionList from './components/PermissionList';

class RolePermissions extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onRoles();
        this.props.onPermissions();
        this.props.onRolePermissions();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            roles,
            rolesMap,
            role,
            rolepermissions,
            permissions,
            permissionsNotOnRole,
            permissionsNotOnRoleMap,
            permissionsOnRole,
            permissionsOnRoleMap,
            formfield,
            onRolesFieldChange,
            onPermissionsNotOnRoleChange,
            onAddPermission,
            onPermissionsOnRoleChange,
            onRemovePermission,
            onFieldChange,
            onSaveUpdatedRole,
        } = this.state;
        let {
            permissionsNotOnRoleSelected,
            permissionsNotOnRoleSelectedNames,
            permissionsOnRoleSelected,
            permissionsOnRoleSelectedNames,
        } = formfield;

        return (
            <div>
                <h1>Modify role information</h1>
                <br/>
                <Link to="/authservice/roleadmin/roles">Up to role adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="roles">Select role</label>
                    <RoleSelect id="roles" roles={roles} rolesMap={rolesMap} value={role.fullname} onRolesFieldChange={onRolesFieldChange} />
                    <br/>
                    <label htmlFor="rolename">Permissions not on role</label>
                    <PermissionList id="permissionsnotonrole" permissions={permissionsNotOnRole} permissionsMap={permissionsNotOnRoleMap} value={permissionsNotOnRoleSelectedNames} onPermissionsFieldChange={onPermissionsNotOnRoleChange} />
                    <br/>
                    <button onClick={() => onAddPermission(role, permissionsOnRole, permissionsNotOnRoleSelected)}>Add permission</button>
                    <br/>
                    <button onClick={() => onRemovePermission(role, permissionsOnRoleSelected)}>Remove permission</button>
                    <br/>
                    <label htmlFor="email">Permission on role</label>
                    <PermissionList id="permissionsnotonrole" permissions={permissionsOnRole} permissionsMap={permissionsOnRoleMap} value={permissionsOnRoleSelectedNames} onPermissionsFieldChange={onPermissionsOnRoleChange} />
                </form>
            </div>
        );
    }
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
        onRoles: () => {
            axios
                .get('/authservice/useradmin/api/roles')
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
        },
        onPermissions: () => {
            axios
                .get('/authservice/useradmin/api/permissions')
                .then(result => dispatch({ type: PERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: PERMISSIONS_ERROR, payload: error }));
        },
        onRolePermissions: () => {
            axios
                .get('/authservice/useradmin/api/roles/permissions')
                .then(result => dispatch({ type: ROLEPERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLEPERMISSIONS_ERROR, payload: error }));
        },
        onRolesFieldChange: (selectedValue, rolesMap) => {
            let role = rolesMap.get(selectedValue);
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onPermissionsNotOnRoleChange: (permissionsNotOnRoleSelectedNames, permissionMap) => {
            const permissionsNotOnRoleSelected = permissionMap.get(permissionsNotOnRoleSelectedNames);
            const payload = { permissionsNotOnRoleSelected, permissionsNotOnRoleSelectedNames };
            dispatch({ type: FORMFIELD_UPDATE, payload });
        },
        onAddPermission: (role, permissionsOnRole, permissionsNotOnRoleSelected) => {
            const permissions = [ permissionsNotOnRoleSelected ];
            const rolewithpermissions = { role, permissions };
            axios
                .post('/authservice/useradmin/api/role/addpermissions', rolewithpermissions)
                .then(result => dispatch({ type: ROLEPERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLEPERMISSIONS_ERROR, payload: error }));
        },
        onPermissionsOnRoleChange: (permissionsOnRoleSelectedNames, permissionMap) => {
            const permissionsOnRoleSelected = permissionMap.get(permissionsOnRoleSelectedNames);
            const payload = { permissionsOnRoleSelected, permissionsOnRoleSelectedNames };
            dispatch({ type: FORMFIELD_UPDATE, payload });
        },
        onRemovePermission: (role, permissionsOnRoleSelected) => {
            const permissions = [ permissionsOnRoleSelected ];
            const rolewithpermissions = { role, permissions };
            axios
                .post('/authservice/useradmin/api/role/removepermissions', rolewithpermissions)
                .then(result => dispatch({ type: ROLEPERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLEPERMISSIONS_ERROR, payload: error }));
        },
    };
};

RolePermissions = connect(mapStateToProps, mapDispatchToProps)(RolePermissions);

export default RolePermissions;
