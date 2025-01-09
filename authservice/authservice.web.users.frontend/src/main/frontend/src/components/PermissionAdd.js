import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    PERMISSION_CLEAR,
    PERMISSION_DESCRIPTION_FIELD_MODIFIED,
    PERMISSIONNAME_FIELD_MODIFIED,
    ADD_NEW_PERMISSION_BUTTON_CLICKED,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';

export default function PermissionAdd() {
    const permissionname = useSelector(state => state.permissionname);
    const description = useSelector(state => state.permissionDescription);
    const dispatch = useDispatch();

    useEffect(() => {
       dispatch(PERMISSION_CLEAR());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/permissions">
                    Up to permission adminstration
                </StyledLinkLeft><br/>
                <h1>Add permission</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="permissionname">Permission name</FormLabel>
                        <FormField>
                            <input
                                id="permissionname"
                                className="form-control"
                                type="text"
                                value={permissionname}
                                onChange={e => dispatch(PERMISSIONNAME_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Permission description</FormLabel>
                        <FormField>
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={description}
                                onChange={e => dispatch(PERMISSION_DESCRIPTION_FIELD_MODIFIED(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button
                            className="btn btn-primary form-control"
                            onClick={() => dispatch(ADD_NEW_PERMISSION_BUTTON_CLICKED())}>
                            Add new permission</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
