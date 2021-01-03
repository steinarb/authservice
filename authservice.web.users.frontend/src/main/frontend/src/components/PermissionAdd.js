import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    PERMISSION_UPDATE,
    PERMISSION_ADD,
} from '../actiontypes';
import { emptyPermission } from '../constants';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class PermissionAdd extends Component {
    render () {
        let {
            permission,
            onPermissionsChange,
            onPermissionname,
            onAddPermission,
        } = this.props;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/permissions">Up to permission adminstration</StyledLinkLeft><br/>
                <Header>
                    <h1>Add permission</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="permissionname">Permission name</FormLabel>
                            <FormField>
                                <input id="permissionname" className="form-control" type="text" value={permission.permissionname} onChange={onPermissionname} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Permission description</FormLabel>
                            <FormField>
                                <input id="description" className="form-control" type="text" value={permission.description} onChange={onDescription} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <button onClick={() => onAddPermission(permission)}>Add new permission</button>
                        </FormRow>
                    </Container>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        permission: state.permission,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onPermissionname: e => dispatch(PERMISSION_UPDATE({ permissionname: e.target.value })),
        onDescription: e => dispatch(PERMISSION_UPDATE({ description: e.target.value })),
        onAddPermission: permission => dispatch(PERMISSION_ADD(permission)),
    };
};

PermissionAdd = connect(mapStateToProps, mapDispatchToProps)(PermissionAdd);

export default PermissionAdd;
