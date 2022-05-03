import React, { useEffect } from 'react';
import { connect, useDispatch } from 'react-redux';
import {
    ROLE_CLEAR,
    ROLENAME_FIELD_MODIFIED,
    ROLE_DESCRIPTION_FIELD_MODIFIED,
    ADD_ROLE_BUTTON_CLICKED,
} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';

function RoleAdd(props) {
    const {
        rolename,
        description,
    } = props;
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(ROLE_CLEAR());
    },[]);

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
                            onClick={() => dispatch(ADD_ROLE_BUTTON_CLICKED())}>
                            Add new role</button>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        role: state.role,
        rolename: state.rolename,
        description: state.roleDescription,
    };
};

export default connect(mapStateToProps)(RoleAdd);
