import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    PERMISSIONS_REQUEST,
    PERMISSION_UPDATE,
    PERMISSION_MODIFY,
} from '../actiontypes';
import { emptyPermission } from '../constants';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class PermissionModify extends Component {
    componentDidMount() {
        this.props.onPermissions();
    }

    render () {
        let {
            permissions,
            permission,
            onPermissionsChange,
            onPermissionname,
            onDescription,
            onSaveUpdatedPermission,
        } = this.props;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/permissions">Up to permission adminstration</StyledLinkLeft><br/>
                <Header>
                    <h1>Modify permission information</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="permissions">Select permission</FormLabel>
                            <FormField>
                                <select id="permissions" className="form-control" onChange={e => onPermissionsChange(e, permissions)} value={permission.id}>
                                    {permissions.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                                </select>
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="permissionname">Permission name</FormLabel>
                            <FormField>
                                <input id="permissionname" className="form-control" type="text" value={permission.permissionname} onChange={onPermissionname} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="description">Permission description</FormLabel>
                            <FormField>
                                <input id="description" className="form-control" type="text" value={permission.description} onChange={onDescription} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <button className="btn btn-primary form-control" onClick={() => onSaveUpdatedPermission(permission)}>Save changes to permission</button>
                        </FormRow>
                    </Container>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        permissions: state.permissions,
        permission: state.permission,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onPermissions: () => dispatch(PERMISSIONS_REQUEST()),
        onPermissionsChange: (e, permissions) => {
            const id = parseInt(e.target.value, 10);
            const permission = permissions.find(p => p.id === id);
            dispatch(PERMISSION_UPDATE({ ...permission }));
        },
        onPermissionname: e => dispatch(PERMISSION_UPDATE({ permissionname: e.target.value })),
        onDescription: e => dispatch(PERMISSION_UPDATE({ description: e.target.value })),
        onSaveUpdatedPermission: permission => dispatch(PERMISSION_MODIFY(permission)),
    };
};

PermissionModify = connect(mapStateToProps, mapDispatchToProps)(PermissionModify);

export default PermissionModify;
