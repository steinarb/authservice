import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { usePostRoleAddMutation } from '../api';
import { clearRole, setRoleRolename, setRoleDescription } from '../reducers/roleSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';

export default function RoleAdd() {
    const role = useSelector(state => state.role);
    const dispatch = useDispatch();
    const [ postRoleAdd ] = usePostRoleAddMutation();
    const onAddRoleClicked = async () => await postRoleAdd(role);

    useEffect(() => {
        dispatch(clearRole());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/roles">Up to role adminstration</StyledLinkLeft>
                <h1>Add role</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
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
                        <FormLabel htmlFor="description">Role description</FormLabel>
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
                            onClick={onAddRoleClicked}>
                            Add new role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
