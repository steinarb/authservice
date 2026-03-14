import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetRolesQuery,
    useGetRolePermissionsQuery,
    useGetPermissionsQuery,
    usePostRoleAddpermissionsMutation,
    usePostRoleRemovepermissionsMutation,
} from '../api';
import { selectRole, clearRole } from '../reducers/roleSlice';
import { clearSelectedInPermissions, selectPermissionNotOnRole, selectPermissionOnRole } from '../reducers/selectedInPermissionsSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import ChevronLeft from './bootstrap/ChevronLeft';
import ChevronRight from './bootstrap/ChevronRight';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { isUnselected } from '../reducers/common';
import { findSelectedRole } from './common';

export default function RolePermissions() {
    const { data: roles = [] } = useGetRolesQuery();
    const role = useSelector(state => state.role);
    const { data: rolePermissions = {} } = useGetRolePermissionsQuery();
    const { data: permissions = [] } = useGetPermissionsQuery();
    const permissionsOnRole = rolePermissions[role.rolename] || [];
    const permissionsNotOnRole = permissions.filter(p => !permissionsOnRole.find(r => r.id === p.id));
    const selectedInPermissions = useSelector(state => state.selectedInPermissions);
    const dispatch = useDispatch();
    const [ postRoleAddpermissions ] = usePostRoleAddpermissionsMutation();
    const onAddPermissionClicked = async () => await postRoleAddpermissions({ role, permissions: permissions.filter(r => r.id===selectedInPermissions.notOnRole) });
    const [ postRoleRemovepermissions ] = usePostRoleRemovepermissionsMutation();
    const onRemovePermissionClicked = async () => await postRoleRemovepermissions({ role, permissions: permissions.filter(r => r.id===selectedInPermissions.onRole) });

    useEffect(() => {
        dispatch(clearRole());
        dispatch(clearSelectedInPermissions());
    },[]);

    const addPermissionDisabled = isUnselected(selectedInPermissions.notOnRole);
    const removePermissionDisabled = isUnselected(selectedInPermissions.onRole);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/roles">Up to role adminstration</StyledLinkLeft><br/>
                <h1>Modify role to permission mappings</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="roles">Select role</FormLabel>
                        <FormField>
                            <select
                                id="roles"
                                className="form-control"
                                onChange={e => dispatch(selectRole(findSelectedRole(e, roles)))}
                                value={role.id}
                            >
                                <option key="-1" value="-1" />
                                {roles.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsnotonrole">Permissions not on role</label>
                            <select
                                id="permissionsnotonrole"
                                className="form-control"
                                size="10"
                                onChange={e => dispatch(selectPermissionNotOnRole(parseInt(e.target.value, 10)))}
                                value={selectedInPermissions.notOnRole}
                            >
                                <option key="-1" value="-1" />
                                {permissionsNotOnRole.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button
                                disabled={addPermissionDisabled}
                                className="btn btn-primary form-control"
                                onClick={onAddPermissionClicked}>
                                Add permission &nbsp;<ChevronRight/></button>
                            <button
                                disabled={removePermissionDisabled}
                                className="btn btn-primary form-control"
                                onClick={onRemovePermissionClicked}>
                                <ChevronLeft/>&nbsp; Remove permission</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="permissionsonrole">Permission on role</label>
                            <select
                                id="permissionsonrole"
                                className="form-control"
                                size="10"
                                onChange={e => dispatch(selectPermissionOnRole(parseInt(e.target.value, 10)))}
                                value={selectedInPermissions.onRole}>
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
