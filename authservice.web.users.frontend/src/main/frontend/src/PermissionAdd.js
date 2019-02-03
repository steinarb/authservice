import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { PERMISSIONS_RECEIVED, PERMISSIONS_ERROR, PERMISSION_UPDATE } from './actiontypes';
import { emptyPermission } from './constants';

class PermissionAdd extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let {
            permission,
            onFieldChange,
            onAddPermission,
        } = this.state;

        return (
            <div>
                <h1>Add permission</h1>
                <br/>
                <Link to="/authservice/useradmin/permissions">Up to permission adminstration</Link><br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="permissionname">Permission name</label>
                    <input id="permissionname" type="text" value={permission.permissionname} onChange={(event) => onFieldChange({permissionname: event.target.value}, permission)} />
                    <br/>
                    <label htmlFor="email">Permission description</label>
                    <input id="description" type="text" value={permission.description} onChange={(event) => onFieldChange({description: event.target.value}, permission)} />
                    <br/>
                    <button onClick={() => onAddPermission(permission)}>Add new permission</button>
                </form>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        permission: state.permission,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onFieldChange: (formValue, originalPermission) => {
            const permission = { ...originalPermission, ...formValue };
            dispatch({ type: PERMISSION_UPDATE, payload: permission });
        },
        onAddPermission: (permission) => {
            axios
                .post('/authservice/useradmin/api/permission/add', permission)
                .then(result => dispatch({ type: PERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: PERMISSIONS_ERROR, payload: error }));
            dispatch({ type: PERMISSION_UPDATE, payload: { ...emptyPermission } });
        },
    };
};

PermissionAdd = connect(mapStateToProps, mapDispatchToProps)(PermissionAdd);

export default PermissionAdd;
