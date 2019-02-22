import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { ROLES_RECEIVED, ROLES_ERROR, ROLE_UPDATE } from './actiontypes';
import RoleSelect from './components/RoleSelect';
import { emptyRole } from './constants';
import { Header } from './components/bootstrap/Header';
import { Container } from './components/bootstrap/Container';
import { StyledLinkLeft } from './components/bootstrap/StyledLinkLeft';
import {FormRow } from './components/bootstrap/FormRow';
import {FormLabel } from './components/bootstrap/FormLabel';
import {FormField } from './components/bootstrap/FormField';

class RoleModify extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onRoles();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            roles,
            rolesMap,
            role,
            onRolesFieldChange,
            onFieldChange,
            onSaveUpdatedRole,
        } = this.state;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/roles">Up to role adminstration</StyledLinkLeft>
                <Header>
                    <h1>Modify role information</h1>
                </Header>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <Container>
                        <FormRow>
                            <FormLabel htmlFor="roles">Select role</FormLabel>
                            <FormField>
                                <RoleSelect id="roles" className="form-control" roles={roles} rolesMap={rolesMap} value={role.rolename} onRolesFieldChange={onRolesFieldChange} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="rolename">Role name</FormLabel>
                            <FormField>
                                <input id="rolename" className="form-control" type="text" value={role.rolename} onChange={(event) => onFieldChange({rolename: event.target.value}, role)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <FormLabel htmlFor="email">Role description</FormLabel>
                            <FormField>
                                <input id="description" className="form-control" type="text" value={role.description} onChange={(event) => onFieldChange({description: event.target.value}, role)} />
                            </FormField>
                        </FormRow>
                        <FormRow>
                            <button className="form-control" onClick={() => onSaveUpdatedRole(role)}>Save changes to role</button>
                        </FormRow>
                    </Container>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    var roles = state.roles;
    roles.unshift(emptyRole);
    return {
        roles,
        rolesMap: new Map(state.roles.map(i => [i.rolename, i])),
        role: state.role,
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
        onRolesFieldChange: (selectedValue, rolesMap) => {
            let role = rolesMap.get(selectedValue);
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onFieldChange: (formValue, originalRole) => {
            const role = { ...originalRole, ...formValue };
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onSaveUpdatedRole: (role) => {
            axios
                .post('/authservice/useradmin/api/role/modify', role)
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
            dispatch({ type: ROLE_UPDATE, payload: { ...emptyRole } });
        },
    };
};

RoleModify = connect(mapStateToProps, mapDispatchToProps)(RoleModify);

export default RoleModify;
