import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

class Users extends Component {

    render () {
        return (
            <div>
                <h1>Adminstrate users</h1>
                <br/>
                <Link to="/authservice/useradmin/">Up to the main page</Link><br/>
                <Link to="/authservice/useradmin/users/modify">Modify existing users</Link><br/>
                <Link to="/authservice/useradmin/users/roles">Modify users to role mappings</Link><br/>
                <Link to="/authservice/useradmin/users/passwords">Change passwords</Link><br/>
                <Link to="/authservice/useradmin/users/add">Add user</Link><br/>
            </div>
        );
    }
}

export default Users;
