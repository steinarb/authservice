import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { PERMISSIONS_RECEIVED, PERMISSIONS_ERROR, PERMISSION_UPDATE } from './actiontypes';
import { emptyPermission } from './constants';
import { Header } from './components/bootstrap/Header';
import { Container } from './components/bootstrap/Container';
import { StyledLinkLeft } from './components/bootstrap/StyledLinkLeft';
import {FormRow } from './components/bootstrap/FormRow';
import {FormLabel } from './components/bootstrap/FormLabel';
import {FormField } from './components/bootstrap/FormField';

class PermissionAdd extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            permission,
            onFieldChange,
            onAddPermission,
        } = this.state;

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
            dispatch({ type: PERMISSION_UPDATE, payload: permission });
        },
        onAddPermission: (permission) => {
            axios
                .post('/authservice/useradmin/api/permission/add', permission)
                .then(result => dispatch({ type: PERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: PERMISSIONS_ERROR, payload: error }));
            dispatch({ type: PERMISSION_UPDATE, payload: { ...emptyPermission } });
        },
    };
};

PermissionAdd = connect(mapStateToProps, mapDispatchToProps)(PermissionAdd);

export default PermissionAdd;