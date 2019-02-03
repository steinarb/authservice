import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { ROLES_RECEIVED, ROLES_ERROR, ROLE_UPDATE } from './actiontypes';
import { emptyRole } from './constants';

class RoleAdd extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            role,
            onFieldChange,
            onAddRole,
        } = this.state;

        return (
            <div>
                <h1>Add role</h1>
                <br/>
                <Link to="/authservice/useradmin/roles">Up to role adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="rolename">Role name</label>
                    <input id="rolename" type="text" value={role.rolename} onChange={(event) => onFieldChange({rolename: event.target.value}, role)} />
                    <br/>
                    <label htmlFor="email">Role description</label>
                    <input id="description" type="text" value={role.description} onChange={(event) => onFieldChange({description: event.target.value}, role)} />
                    <br/>
                    <button onClick={() => onAddRole(role)}>Add new role</button>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        role: state.role,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onFieldChange: (formValue, originalRole) => {
            const role = { ...originalRole, ...formValue };
            dispatch({ type: ROLE_UPDATE, payload: role });
        },
        onAddRole: (role) => {
            axios
                .post('/authservice/useradmin/api/role/add', role)
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
            dispatch({ type: ROLE_UPDATE, payload: { ...emptyRole } });
        },
    };
};

RoleAdd = connect(mapStateToProps, mapDispatchToProps)(RoleAdd);

export default RoleAdd;
