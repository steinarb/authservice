import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    useGetUsersQuery,
    useGetUserRolesQuery,
    useGetRolesQuery,
    usePostUserAddrolesMutation,
    usePostUserRemoverolesMutation,
} from '../api';
import { selectUser, clearUser } from '../reducers/userSlice';
import { clearSelectedInRoles, selectRoleNotOnUser, selectRoleOnUser } from '../reducers/selectedInRolesSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import ChevronLeft from './bootstrap/ChevronLeft';
import ChevronRight from './bootstrap/ChevronRight';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedUser } from './common';
import { isUnselected } from '../reducers/common';

export default function UserRoles() {
    const { data: users = [] } = useGetUsersQuery();
    const user = useSelector(state => state.user);
    const { data: userRoles = {} } = useGetUserRolesQuery();
    const { data: roles = [] } = useGetRolesQuery();
    const rolesOnUser = userRoles[user.username] || [];
    const rolesNotOnUser = roles.filter(r => !rolesOnUser.find(u => u.id === r.id));
    const selectedInRoles = useSelector(state => state.selectedInRoles);
    const dispatch = useDispatch();
    const [ postUserAddroles ] = usePostUserAddrolesMutation();
    const onAddRoleClicked = async () => await postUserAddroles({ user, roles: roles.filter(r => r.id===selectedInRoles.notOnUser) });
    const [ postUserRemoveroles ] = usePostUserRemoverolesMutation();
    const onRemoveRoleClicked = async () => await postUserRemoveroles({ user, roles: roles.filter(r => r.id===selectedInRoles.onUser) });

    useEffect(() => {
        dispatch(clearUser());
        dispatch(clearSelectedInRoles());
    }, []);

    const addRoleDisabled = isUnselected(selectedInRoles.notOnUser);
    const removeRoleDisabled = isUnselected(selectedInRoles.onUser);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/users">Up to user adminstration</StyledLinkLeft><br/>
                <h1>Modify user to role mappings</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="users">Select user</FormLabel>
                        <FormField>
                            <select
                                id="users"
                                className="form-control"
                                onChange={e => dispatch(selectUser(findSelectedUser(e, users)))} value={user.userid}>
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
                                onChange={e => dispatch(selectRoleNotOnUser(parseInt(e.target.value, 10)))}
                                value={selectedInRoles.notOnUser}>
                                <option key="-1" value="-1" />
                                {rolesNotOnUser.map((val) => <option key={val.id} value={val.id}>{val.rolename}</option>)}
                            </select>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <button
                                disabled={addRoleDisabled}
                                className="btn btn-primary form-control"
                                onClick={onAddRoleClicked}>
                                Add role &nbsp;<ChevronRight/></button>
                            <button
                                disabled={removeRoleDisabled}
                                className="btn btn-primary form-control"
                                onClick={onRemoveRoleClicked}>
                                <ChevronLeft/>&nbsp; Remove role</button>
                        </div>
                        <div className="no-gutters col-sm-4">
                            <label htmlFor="rolesonuser">Role on user</label>
                            <select
                                id="rolesonuser"
                                className="form-control"
                                size="10"
                                onChange={e => dispatch(selectRoleOnUser(parseInt(e.target.value, 10)))}
                                value={selectedInRoles.onUser}>
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
