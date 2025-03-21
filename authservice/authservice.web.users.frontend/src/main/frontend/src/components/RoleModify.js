import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetRolesQuery, usePostRoleModifyMutation } from '../api';
import { selectRole, clearRole, setRoleRolename, setRoleDescription } from '../reducers/roleSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedRole } from './common';

export default function RoleModify() {
    const { data: roles = [] } = useGetRolesQuery();
    const role = useSelector(state => state.role);
    const dispatch = useDispatch();
    const [ postRoleModify ] = usePostRoleModifyMutation();
    const onModifyRoleClicked = async () => await postRoleModify(role);

    useEffect(() => {
        dispatch(clearRole());
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
                                onChange={e => dispatch(selectRole(findSelectedRole(e, roles)))}
                                value={role.id}
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
                                value={role.rolename}
                                onChange={e => dispatch(setRoleRolename(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Role description</FormLabel>
                        <FormField>
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={role.description}
                                onChange={e => dispatch(setRoleDescription(e.target.value))} />
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
