import React, { Component } from 'react';
import { Routes, Route, BrowserRouter as Router } from 'react-router-dom';
import './App.css';
import Home from './components/Home';
import Users from './components/Users';
import UserModify from './components/UserModify';
import UserRoles from './components/UserRoles';
import UserChangePasswords from './components/UserChangePasswords';
import UserAdd from './components/UserAdd';
import Roles from './components/Roles';
import RoleModify from './components/RoleModify';
import RolePermissions from './components/RolePermissions';
import RoleAdd from './components/RoleAdd';
import Permissions from './components/Permissions';
import PermissionModify from './components/PermissionModify';
import PermissionAdd from './components/PermissionAdd';

class App extends Component {
    render() {
        return (
            <Router>
                <Routes>
                    <Route exact path="/authservice/useradmin/" element={<Home/>} />
                    <Route path="/authservice/useradmin/users/modify" element={<UserModify/>} />
                    <Route path="/authservice/useradmin/users/roles" element={<UserRoles/>} />
                    <Route path="/authservice/useradmin/users/add" element={<UserAdd/>} />
                    <Route path="/authservice/useradmin/users/passwords" element={<UserChangePasswords/>} />
                    <Route path="/authservice/useradmin/users" element={<Users/>} />
                    <Route path="/authservice/useradmin/roles/modify" element={<RoleModify/>} />
                    <Route path="/authservice/useradmin/roles/permissions" element={<RolePermissions/>} />
                    <Route path="/authservice/useradmin/roles/add" element={<RoleAdd/>} />
                    <Route path="/authservice/useradmin/roles" element={<Roles/>} />
                    <Route path="/authservice/useradmin/permissions/modify" element={<PermissionModify/>} />
                    <Route path="/authservice/useradmin/permissions/add" element={<PermissionAdd/>} />
                    <Route path="/authservice/useradmin/permissions" element={<Permissions/>} />
                </Routes>
            </Router>
        );
    }
}

export default App;
