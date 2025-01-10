import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetRolesQuery,
    useGetRolePermissionsQuery,
    useGetPermissionsQuery,
    usePostRoleAddpermissionsMutation,
    usePostRoleRemovepermissionsMutation,
} from '../api';
import {
    SELECT_ROLE,
    ROLE_CLEAR,
    SELECT_PERMISSIONS_NOT_ON_ROLE,
    SELECT_PERMISSIONS_ON_ROLE,
} from '../actiontypes';
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
    const roleid = useSelector(state => state.roleid);
    const rolename = useSelector(state => state.rolename);
    const role = { id: roleid, rolename };
    const { data: rolePermissions = {} } = useGetRolePermissionsQuery();
    const { data: permissions = [] } = useGetPermissionsQuery();
    const permissionsOnRole = rolePermissions[rolename] || [];
    const permissionsNotOnRole = permissions.filter(p => !permissionsOnRole.find(r => r.id === p.id));
    const selectedInPermissionsNotOnRole = useSelector(state => state.selectedInPermissionsNotOnRole);
    const selectedInPermissionsOnRole = useSelector(state => state.selectedInPermissionsOnRole);
    const dispatch = useDispatch();
    const [ postRoleAddpermissions ] = usePostRoleAddpermissionsMutation();
    const onAddPermissionClicked = async () => await postRoleAddpermissions({ role, permissions: permissions.filter(r => r.id===selectedInPermissionsNotOnRole) });
    const [ postRoleRemovepermissions ] = usePostRoleRemovepermissionsMutation();
    const onRemovePermissionClicked = async () => await postRoleRemovepermissions({ role, permissions: permissions.filter(r => r.id===selectedInPermissionsOnRole) });

    useEffect(() => {
        dispatch(ROLE_CLEAR());
    },[]);

    const addPermissionDisabled = isUnselected(selectedInPermissionsNotOnRole);
    const removePermissionDisabled = isUnselected(selectedInPermissionsOnRole);

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
                                onChange={e => dispatch(SELECT_ROLE(findSelectedRole(e, roles)))}
                                value={roleid}
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
                                onChange={e => dispatch(SELECT_PERMISSIONS_NOT_ON_ROLE(parseInt(e.target.value, 10)))}
                                value={selectedInPermissionsNotOnRole}
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
                                onChange={e => dispatch(SELECT_PERMISSIONS_ON_ROLE(parseInt(e.target.value, 10)))}
                                value={selectedInPermissionsOnRole}>
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
