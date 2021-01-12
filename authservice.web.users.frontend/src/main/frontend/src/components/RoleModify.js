import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
    ROLE_UPDATE,
    ROLE_CLEAR,
    ROLE_MODIFY,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function RoleModify(props) {
    const {
        roles,
        role,
        onRolesChange,
        onRolename,
        onDescription,
        onSaveUpdatedRole,
        onRoles,
        onRoleClear,
    } = props;

    useEffect(() => {
        onRoles();
        onRoleClear();
    },[onRoles, onRoleClear]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/roles">Up to role adminstration</StyledLinkLeft>
                <h1>Modify role information</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="roles">Select role</FormLabel>
                        <FormField>
                            <select id="roles" className="form-control" onChange={e => onRolesChange(e, roles)} value={role.id}>
                                {roles.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="rolename">Role name</FormLabel>
                        <FormField>
                            <input id="rolename" className="form-control" type="text" value={role.rolename} onChange={onRolename} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Role description</FormLabel>
                        <FormField>
                            <input id="description" className="form-control" type="text" value={role.description} onChange={onDescription} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button className="btn btn-primary form-control" onClick={() => onSaveUpdatedRole(role)}>Save changes to role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        roles: state.roles,
        role: state.role,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoles: () => dispatch(ROLES_REQUEST()),
        onRoleClear: () => dispatch(ROLE_CLEAR()),
        onRolesChange: (e, roles) => {
            const id = parseInt(e.target.value, 10);
            const role = roles.find(r => r.id === id);
            dispatch(ROLE_UPDATE(role));
        },
        onRolename: e => dispatch(ROLE_UPDATE({ rolename: e.target.value })),
        onDescription: e => dispatch(ROLE_UPDATE({ description: e.target.value })),
        onSaveUpdatedRole: role => dispatch(ROLE_MODIFY(role)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(RoleModify);
