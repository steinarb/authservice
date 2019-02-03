import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';

class Home extends Component {

    render () {
        return (
            <div>
                <h1>User administration</h1>
                <br/>
                <Link to="/authservice/useradmin/users">Adminstrate users</Link><br/>
                <Link to="/authservice/useradmin/roles">Administrate roles</Link><br/>
                <Link to="/authservice/useradmin/permissions">Administrate permissions</Link><br/>
                <br/>
                <a href="/authservice/">Up to authservice top</a><br/>
            </div>
        );
    }
}

export default Home;
