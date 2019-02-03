import React, { Component } from 'react';
import { Switch, Route, BrowserRouter as Router, NavLink } from 'react-router-dom';
import { Provider } from 'react-redux';
import logo from './logo.svg';
import './App.css';
import Home from './Home';
import Users from './Users';
import UserModify from './UserModify';
import UserRoles from './UserRoles';
import UserChangePasswords from './UserChangePasswords';
import UserAdd from './UserAdd';
import Roles from './Roles';
import RoleModify from './RoleModify';
import RolePermissions from './RolePermissions';
import RoleAdd from './RoleAdd';
import Permissions from './Permissions';
import PermissionModify from './PermissionModify';
import PermissionAdd from './PermissionAdd';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    render() {
        return (
            <Provider store={this.state.store}>
                <Router>
                    <Switch>
                        <Route exact path="/authservice/useradmin/" component={Home} />
                        <Route path="/authservice/useradmin/users/modify" component={UserModify} />
                        <Route path="/authservice/useradmin/users/roles" component={UserRoles} />
                        <Route path="/authservice/useradmin/users/add" component={UserAdd} />
                        <Route path="/authservice/useradmin/users/passwords" component={UserChangePasswords} />
                        <Route path="/authservice/useradmin/users" component={Users} />
                        <Route path="/authservice/useradmin/roles/modify" component={RoleModify} />
                        <Route path="/authservice/useradmin/roles/permissions" component={RolePermissions} />
                        <Route path="/authservice/useradmin/roles/add" component={RoleAdd} />
                        <Route path="/authservice/useradmin/roles" component={Roles} />
                        <Route path="/authservice/useradmin/permissions/modify" component={PermissionModify} />
                        <Route path="/authservice/useradmin/permissions/add" component={PermissionAdd} />
                        <Route path="/authservice/useradmin/permissions" component={Permissions} />
                    </Switch>
                </Router>
            </Provider>
        );
    }
}

export default App;
