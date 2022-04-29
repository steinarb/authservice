import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
    ROLE_CLEAR,
    SELECT_ROLE,
    ROLENAME_FIELD_MODIFIED,
    ROLE_DESCRIPTION_FIELD_MODIFIED,
    MODIFY_ROLE_BUTTON_CLICKED,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function RoleModify(props) {
    const {
        roles,
        roleid,
        rolename,
        description,
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
                            <select id="roles" className="form-control" onChange={onRolesChange} value={roleid}>
                                <option key="-1" value="-1" />
                                {roles.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="rolename">Role name</FormLabel>
                        <FormField>
                            <input id="rolename" className="form-control" type="text" value={rolename} onChange={onRolename} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Role description</FormLabel>
                        <FormField>
                            <input id="description" className="form-control" type="text" value={description} onChange={onDescription} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button className="btn btn-primary form-control" onClick={onSaveUpdatedRole}>Save changes to role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        roles: state.roles,
        roleid: state.roleid,
        rolename: state.rolename,
        description: state.roleDescription,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoles: () => dispatch(ROLES_REQUEST()),
        onRoleClear: () => dispatch(ROLE_CLEAR()),
        onRolesChange: e => dispatch(SELECT_ROLE(parseInt(e.target.value))),
        onRolename: e => dispatch(ROLENAME_FIELD_MODIFIED(e.target.value)),
        onDescription: e => dispatch(ROLE_DESCRIPTION_FIELD_MODIFIED(e.target.value)),
        onSaveUpdatedRole: () => dispatch(MODIFY_ROLE_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(RoleModify);
