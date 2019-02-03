import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { ROLES_RECEIVED, ROLES_ERROR, ROLE_UPDATE } from './actiontypes';
import RoleSelect from './components/RoleSelect';
import { emptyRole } from './constants';

class RoleModify extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onRoles();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            roles,
            rolesMap,
            role,
            onRolesFieldChange,
            onFieldChange,
            onSaveUpdatedRole,
        } = this.state;

        return (
            <div>
                <h1>Modify role information</h1>
                <br/>
                <Link to="/authservice/useradmin/roles">Up to role adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="roles">Select role</label>
                    <RoleSelect id="roles" roles={roles} rolesMap={rolesMap} value={role.rolename} onRolesFieldChange={onRolesFieldChange} />
                    <br/>
                    <label htmlFor="rolename">Role name</label>
                    <input id="rolename" type="text" value={role.rolename} onChange={(event) => onFieldChange({rolename: event.target.value}, role)} />
                    <br/>
                    <label htmlFor="email">Role description</label>
                    <input id="description" type="text" value={role.description} onChange={(event) => onFieldChange({description: event.target.value}, role)} />
                    <br/>
                    <button onClick={() => onSaveUpdatedRole(role)}>Save changes to role</button>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    var roles = state.roles;
    roles.unshift(emptyRole);
    return {
        roles,
        rolesMap: new Map(state.roles.map(i => [i.rolename, i])),
        role: state.role,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoles: () => {
            axios
                .get('/authservice/useradmin/api/roles')
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
        },
        onRolesFieldChange: (selectedValue, rolesMap) => {
            let role = rolesMap.get(selectedValue);
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onFieldChange: (formValue, originalRole) => {
            const role = { ...originalRole, ...formValue };
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onSaveUpdatedRole: (role) => {
            axios
                .post('/authservice/useradmin/api/role/modify', role)
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
            dispatch({ type: ROLE_UPDATE, payload: { ...emptyRole } });
        },
    };
};

RoleModify = connect(mapStateToProps, mapDispatchToProps)(RoleModify);

export default RoleModify;
