import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetPermissionsQuery, usePostPermissionModifyMutation } from '../api';
import { selectPermission, clearPermission, setPermissionPermissionname, setPermissionDescription } from '../reducers/permissionSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedPermission } from './common';

export default function PermissionModify() {
    const { data: permissions = [] } = useGetPermissionsQuery();
    const permission = useSelector(state => state.permission);
    const dispatch = useDispatch();
    const [ postPermissionModify ] = usePostPermissionModifyMutation();
    const onModifyPermissionClicked = async () => await postPermissionModify(permission);

    useEffect(() => {
        dispatch(clearPermission());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/permissions">
                    Up to permission adminstration
                </StyledLinkLeft><br/>
                <h1>Modify permission information</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="permissions">Select permission</FormLabel>
                        <FormField>
                            <select
                                id="permissions"
                                className="form-control"
                                onChange={e => dispatch(selectPermission(findSelectedPermission(e, permissions)))}
                                value={permission.id}
                            >
                                <option key="-1" value="-1" />
                                {permissions.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="permissionname">Permission name</FormLabel>
                        <FormField>
                            <input
                                id="permissionname"
                                className="form-control"
                                type="text"
                                value={permission.permissionname}
                                onChange={e => dispatch(setPermissionPermissionname(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="description">Permission description</FormLabel>
                        <FormField>
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={permission.description}
                                onChange={e => dispatch(setPermissionDescription(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button
                            className="btn btn-primary form-control"
                            onClick={onModifyPermissionClicked}>
                            Save changes to permission</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
