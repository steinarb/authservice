import React, { Component } from 'react';
import { Routes, Route, BrowserRouter as Router } from 'react-router';
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

export default function App(props) {
    const { basename } = props;

    return (
        <Router basename={basename}>
            <Routes>
                <Route exact path="/" element={<Home/>} />
                <Route path="/users/modify" element={<UserModify/>} />
                <Route path="/users/roles" element={<UserRoles/>} />
                <Route path="/users/add" element={<UserAdd/>} />
                <Route path="/users/passwords" element={<UserChangePasswords/>} />
                <Route path="/users" element={<Users/>} />
                <Route path="/roles/modify" element={<RoleModify/>} />
                <Route path="/roles/permissions" element={<RolePermissions/>} />
                <Route path="/roles/add" element={<RoleAdd/>} />
                <Route path="/roles" element={<Roles/>} />
                <Route path="/permissions/modify" element={<PermissionModify/>} />
                <Route path="/permissions/add" element={<PermissionAdd/>} />
                <Route path="/permissions" element={<Permissions/>} />
            </Routes>
        </Router>
    );
}
