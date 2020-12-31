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
            onFieldChange,
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
                                <input id="permissionname" type="text" value={permission.permissionname} onChange={(event) => onFieldChange({permissionname: event.target.value}, permission)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Permission description</FormLabel>
                            <FormField>
                                <input id="description" type="text" value={permission.description} onChange={(event) => onFieldChange({description: event.target.value}, permission)} />
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
        onFieldChange: (formValue, originalPermission) => {
            const permission = { ...originalPermission, ...formValue };
            dispatch(PERMISSION_UPDATE(permission));
        },
        onAddPermission: (permission) => dispatch(PERMISSION_ADD(permission)),
    };
};

PermissionAdd = connect(mapStateToProps, mapDispatchToProps)(PermissionAdd);

export default PermissionAdd;
