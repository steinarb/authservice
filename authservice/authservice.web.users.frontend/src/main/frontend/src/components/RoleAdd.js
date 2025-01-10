import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { usePostRoleAddMutation } from '../api';
import {
    ROLE_CLEAR,
    ROLENAME_FIELD_MODIFIED,
    ROLE_DESCRIPTION_FIELD_MODIFIED,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';

export default function RoleAdd() {
    const rolename = useSelector(state => state.rolename);
    const description = useSelector(state => state.roleDescription);
    const dispatch = useDispatch();
    const [ postRoleAdd ] = usePostRoleAddMutation();
    const onAddRoleClicked = async () => await postRoleAdd({ rolename, description });

    useEffect(() => {
        dispatch(ROLE_CLEAR());
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
                                value={rolename}
                                onChange={e => dispatch(ROLENAME_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="description">Role description</FormLabel>
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
                            onClick={onAddRoleClicked}>
                            Add new role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
