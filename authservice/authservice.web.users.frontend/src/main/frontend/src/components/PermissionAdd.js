import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import {
    PERMISSION_CLEAR,
    PERMISSION_DESCRIPTION_FIELD_MODIFIED,
    PERMISSIONNAME_FIELD_MODIFIED,
    ADD_NEW_PERMISSION_BUTTON_CLICKED,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import {FormRow } from './bootstrap/FormRow';
import {FormLabel } from './bootstrap/FormLabel';
import {FormField } from './bootstrap/FormField';

function PermissionAdd(props) {
    const {
        permissionname,
        description,
        onPermissionClear,
        onPermissionname,
        onDescription,
        onAddPermission,
    } = props;

    useEffect(() => {
        onPermissionClear();
    },[onPermissionClear]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/permissions">Up to permission adminstration</StyledLinkLeft><br/>
                <h1>Add permission</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="permissionname">Permission name</FormLabel>
                        <FormField>
                            <input id="permissionname" className="form-control" type="text" value={permissionname} onChange={onPermissionname} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <FormLabel htmlFor="email">Permission description</FormLabel>
                        <FormField>
                            <input id="description" className="form-control" type="text" value={description} onChange={onDescription} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <button className="btn btn-primary form-control" onClick={onAddPermission}>Add new permission</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        permissionname: state.permissionname,
        description: state.permissionDescription,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onPermissionClear: () => dispatch(PERMISSION_CLEAR()),
        onPermissionname: e => dispatch(PERMISSIONNAME_FIELD_MODIFIED(e.target.value)),
        onDescription: e => dispatch(PERMISSION_DESCRIPTION_FIELD_MODIFIED(e.target.value)),
        onAddPermission: () => dispatch(ADD_NEW_PERMISSION_BUTTON_CLICKED()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(PermissionAdd);
