import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    ROLE_CLEAR,
    ROLE_UPDATE,
    ROLE_ADD,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function RoleAdd(props) {
    const {
        role,
        onRolename,
        onDescription,
        onAddRole,
        onRoleClear,
    } = props;

    useEffect(() => {
        onRoleClear();
    },[onRoleClear]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/roles">Up to role adminstration</StyledLinkLeft>
                <h1>Add role</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="rolename">Role name</FormLabel>
                        <FormField>
                            <input id="rolename" className="form-control" type="text" value={role.rolename} onChange={onRolename} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="description">Role description</FormLabel>
                        <FormField>
                            <input id="description" className="form-control" type="text" value={role.description} onChange={onDescription} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button className="btn btn-primary form-control" onClick={() => onAddRole(role)}>Add new role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        role: state.role,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoleClear: () => dispatch(ROLE_CLEAR()),
        onRolename: e => dispatch(ROLE_UPDATE({ rolename: e.target.value })),
        onDescription: e => dispatch(ROLE_UPDATE({ description: e.target.value })),
        onAddRole: (role) => dispatch(ROLE_ADD(role)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(RoleAdd);
