import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { ROLES_RECEIVED, ROLES_ERROR } from './actiontypes';

class Roles extends Component {
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
        let { roles } = this.state;

        return (
            <div>
                <h1>Administrate roles</h1>
                <br/>
                <Link to="/authservice/useradmin/">Up to the main page</Link><br/>
                <Link to="/authservice/useradmin/roles/modify">Modify roles</Link><br/>
                <Link to="/authservice/useradmin/roles/permissions">Change role to permission mappings</Link><br/>
                <Link to="/authservice/useradmin/roles/add">Add role</Link><br/>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return { ...state };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoles: () => {
            axios
                .get('/authservice/useradmin/api/roles')
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
        },
    };
};

Roles = connect(mapStateToProps, mapDispatchToProps)(Roles);

export default Roles;
