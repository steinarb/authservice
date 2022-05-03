import React, { useEffect } from 'react';
import { connect, useDispatch } from 'react-redux';
import {
    SELECT_PERMISSION,
    PERMISSIONS_REQUEST,
    PERMISSION_CLEAR,
    PERMISSION_DESCRIPTION_FIELD_MODIFIED,
    PERMISSIONNAME_FIELD_MODIFIED,
    MODIFY_PERMISSION_BUTTON_CLICKED,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';

function PermissionModify(props) {
    const {
        permissions,
        permissionid,
        permissionname,
        description,
    } = props;
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(PERMISSIONS_REQUEST());
        dispatch(PERMISSION_CLEAR());
    },[]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/permissions">
                    Up to permission adminstration
                </StyledLinkLeft><br/>
                <h1>Modify permission information</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="permissions">Select permission</FormLabel>
                        <FormField>
                            <select
                                id="permissions"
                                className="form-control"
                                onChange={e => dispatch(SELECT_PERMISSION(parseInt(e.target.value)))}
                                value={permissionid}
                            >
                                <option key="-1" value="-1" />
                                {permissions.map((val) => <option key={val.id} value={val.id}>{val.permissionname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
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
                        <FormLabel htmlFor="description">Permission description</FormLabel>
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
                            onClick={() => dispatch(MODIFY_PERMISSION_BUTTON_CLICKED())}>
                            Save changes to permission</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        permissions: state.permissions,
        permissionid: state.permissionid,
        permissionname: state.permissionname,
        description: state.permissionDescription,
    };
};

export default connect(mapStateToProps)(PermissionModify);
