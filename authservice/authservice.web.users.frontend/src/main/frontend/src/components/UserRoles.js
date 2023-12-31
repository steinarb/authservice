import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    USERS_REQUEST,
    USER_CLEAR,
    SELECT_USER,
    ROLES_REQUEST,
    USERROLES_REQUEST,
    SELECT_ROLES_NOT_ON_USER,
    SELECT_ROLES_ON_USER,
    ADD_USER_ROLE_BUTTON_CLICKED,
    REMOVE_USER_ROLE_BUTTON_CLICKED,

} from '../actiontypes';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import ChevronLeft from './bootstrap/ChevronLeft';
import ChevronRight from './bootstrap/ChevronRight';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import { findSelectedUser } from './common';
import { isUnselected } from '../reducers/common';

export default function UserRoles() {
    const users = useSelector(state => state.users);
    const userid = useSelector(state => state.userid);
    const rolesNotOnUser = useSelector(state => state.rolesNotOnUser);
    const selectedInRolesNotOnUser = useSelector(state => state.selectedInRolesNotOnUser);
    const rolesOnUser = useSelector(state => state.rolesOnUser);
    const selectedInRolesOnUser = useSelector(state => state.selectedInRolesOnUser);
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(USERS_REQUEST());
        dispatch(USER_CLEAR());
        dispatch(ROLES_REQUEST());
        dispatch(USERROLES_REQUEST());
    }, []);

    const addRoleDisabled = isUnselected(selectedInRolesNotOnUser);
    const removeRoleDisabled = isUnselected(selectedInRolesOnUser);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/users">Up to user adminstration</StyledLinkLeft><br/>
                <h1>Modify user to role mappings</h1>
                <div className="col-sm-2"></div>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="users">Select user</FormLabel>
                        <FormField>
                            <select
                                id="users"
                                className="form-control"
                                onChange={e => dispatch(SELECT_USER(findSelectedUser(e, users)))} value={userid}>
                                <option key="-1" value="-1" />
                                {users.map((val) => <option key={val.userid} value={val.userid}>{val.firstname} {val.lastname}</option>)}
                            </select>
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesnotonuser">Roles not on user</label>
                            <select
                                id="rolesnotonuser"
                                className="form-control"
                                size="10"
                                onChange={e => dispatch(SELECT_ROLES_NOT_ON_USER(parseInt(e.target.value, 10)))}
                                value={selectedInRolesNotOnUser}>
                                <option key="-1" value="-1" />
                                {rolesNotOnUser.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button
                                disabled={addRoleDisabled}
                                className="btn btn-primary form-control"
                                onClick={() => dispatch(ADD_USER_ROLE_BUTTON_CLICKED())}>
                                Add role &nbsp;<ChevronRight/></button>
                            <button
                                disabled={removeRoleDisabled}
                                className="btn btn-primary form-control"
                                onClick={() => dispatch(REMOVE_USER_ROLE_BUTTON_CLICKED())}>
                                <ChevronLeft/>&nbsp; Remove role</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesonuser">Role on user</label>
                            <select
                                id="rolesonuser"
                                className="form-control"
                                size="10"
                                onChange={e => dispatch(SELECT_ROLES_ON_USER(parseInt(e.target.value, 10)))}
                                value={selectedInRolesOnUser}>
                                <option key="-1" value="-1" />
                                {rolesOnUser.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </div>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
