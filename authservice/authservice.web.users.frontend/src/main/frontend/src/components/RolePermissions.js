import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
    SELECT_ROLE,
    ROLE_CLEAR,
    PERMISSIONS_REQUEST,
    ROLEPERMISSIONS_REQUEST,
    SELECT_PERMISSIONS_NOT_ON_ROLE,
    ADD_PERMISSION_TO_ROLE_BUTTON_CLICKED,
    SELECT_PERMISSIONS_ON_ROLE,
    REMOVE_PERMISSION_FROM_ROLE_BUTTON_CLICKED,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { ChevronLeft } from './bootstrap/ChevronLeft';
import { ChevronRight } from './bootstrap/ChevronRight';
import { FormRow } from './bootstrap/FormRow';
import { FormLabel } from './bootstrap/FormLabel';
import { FormField } from './bootstrap/FormField';
import { isUnselected } from '../reducers/common';

function RolePermissions(props) {
    const {
        roles,
        roleid,
        permissionsNotOnRole,
        selectedInPermissionsNotOnRole,
        permissionsOnRole,
        selectedInPermissionsOnRole,
        onRolesChange,
        onPermissionsNotOnRoleSelected,
        onAddPermission,
        onPermissionsOnRoleSelected,
        onRemovePermission,
        onRoles,
        onRoleClear,
        onPermissions,
        onRolePermissions,
    } = props;

    useEffect(() => {
        onRoles();
        onRoleClear();
        onPermissions();
        onRolePermissions();
    },[onRoles, onRoleClear, onPermissions, onRolePermissions]);

    const addPermissionDisabled = isUnselected(selectedInPermissionsNotOnRole);
    const removePermissionDisabled = isUnselected(selectedInPermissionsOnRole);

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
                            <select id="roles" className="form-control" onChange={onRolesChange} value={roleid}>
                                <option key="-1" value="-1" />
                                {roles.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsnotonrole">Permissions not on role</label>
                            <select id="permissionsnotonrole" className="form-control" multiselect="true" size="10" onChange={onPermissionsNotOnRoleSelected} value={selectedInPermissionsNotOnRole}>
                                <option key="-1" value="-1" />
                                {permissionsNotOnRole.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button disabled={addPermissionDisabled} className="btn btn-primary form-control" onClick={onAddPermission}>Add permission &nbsp;<ChevronRight/></button>
                            <button disabled={removePermissionDisabled} className="btn btn-primary form-control" onClick={onRemovePermission}><ChevronLeft/>&nbsp; Remove permission</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsonrole">Permission on role</label>
                            <select id="permissionsonrole" className="form-control" multiselect="true" size="10" onChange={onPermissionsOnRoleSelected} value={selectedInPermissionsOnRole}>
                                <option key="-1" value="-1" />
                                {permissionsOnRole.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </div>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        roles: state.roles,
        roleid: state.roleid,
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
        onRolesChange: e => dispatch(SELECT_ROLE(parseInt(e.target.value))),
        onPermissionsNotOnRoleSelected: e => dispatch(SELECT_PERMISSIONS_NOT_ON_ROLE(parseInt(e.target.value, 10))),
        onAddPermission: () => dispatch(ADD_PERMISSION_TO_ROLE_BUTTON_CLICKED()),
        onPermissionsOnRoleSelected: e => dispatch(SELECT_PERMISSIONS_ON_ROLE(parseInt(e.target.value, 10))),
        onRemovePermission: () => dispatch(REMOVE_PERMISSION_FROM_ROLE_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(RolePermissions);
