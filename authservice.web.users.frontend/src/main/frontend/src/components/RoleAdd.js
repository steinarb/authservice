import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { ROLES_RECEIVED, ROLES_ERROR, ROLE_UPDATE } from '../actiontypes';
import { emptyRole } from '../constants';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

class RoleAdd extends Component {
    render () {
        let {
            role,
            onFieldChange,
            onAddRole,
        } = this.props;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/roles">Up to role adminstration</StyledLinkLeft>
                <Header>
                    <h1>Add role</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="rolename">Role name</FormLabel>
                            <FormField>
                                <input id="rolename" className="form-control" type="text" value={role.rolename} onChange={(event) => onFieldChange({rolename: event.target.value}, role)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="description">Role description</FormLabel>
                            <FormField>
                                <input id="description" className="form-control" type="text" value={role.description} onChange={(event) => onFieldChange({description: event.target.value}, role)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <button className="form-control" onClick={() => onAddRole(role)}>Add new role</button>
                        </FormRow>
                    </Container>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        role: state.role,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onFieldChange: (formValue, originalRole) => {
            const role = { ...originalRole, ...formValue };
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onAddRole: (role) => {
            axios
                .post('/authservice/useradmin/api/role/add', role)
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
            dispatch({ type: ROLE_UPDATE, payload: { ...emptyRole } });
        },
    };
};

RoleAdd = connect(mapStateToProps, mapDispatchToProps)(RoleAdd);

export default RoleAdd;
