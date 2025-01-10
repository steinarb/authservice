import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetRolesQuery, usePostRoleModifyMutation } from '../api';
import {
    ROLE_CLEAR,
    SELECT_ROLE,
    ROLENAME_FIELD_MODIFIED,
    ROLE_DESCRIPTION_FIELD_MODIFIED,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedRole } from './common';

export default function RoleModify() {
    const { data: roles = [] } = useGetRolesQuery();
    const roleid = useSelector(state => state.roleid);
    const rolename = useSelector(state => state.rolename);
    const description = useSelector(state => state.roleDescription);
    const dispatch = useDispatch();
    const [ postRoleModify ] = usePostRoleModifyMutation();
    const onModifyRoleClicked = async () => await postRoleModify({ id: roleid, rolename, description });

    useEffect(() => {
        dispatch(ROLE_CLEAR());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/roles">Up to role adminstration</StyledLinkLeft>
                <h1>Modify role information</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="roles">Select role</FormLabel>
                        <FormField>
                            <select
                                id="roles"
                                className="form-control"
                                onChange={e => dispatch(SELECT_ROLE(findSelectedRole(e, roles)))}
                                value={roleid}
                            >
                                <option key="-1" value="-1" />
                                {roles.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="rolename">Role name</FormLabel>
                        <FormField>
                            <input
                                id="rolename"
                                className="form-control"
                                type="text"
                                value={rolename}
                                onChange={e => dispatch(ROLENAME_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Role description</FormLabel>
                        <FormField>
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={description}
                                onChange={e => dispatch(ROLE_DESCRIPTION_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button
                            className="btn btn-primary form-control"
                            onClick={onModifyRoleClicked}>
                            Save changes to role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
