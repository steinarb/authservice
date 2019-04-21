import React, { Component } from 'react';
import { Header } from './components/bootstrap/Header';
import { Container } from './components/bootstrap/Container';
import { StyledLinkLeft } from './components/bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './components/bootstrap/StyledLinkRight';

class Users extends Component {

    render () {
        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/">Up to the main page</StyledLinkLeft>
                <Header>
                    <h1>Adminstrate users</h1>
                </Header>
                <Container>
                    <StyledLinkRight to="/authservice/useradmin/users/modify">Modify existing users</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/users/roles">Modify users to role mappings</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/users/passwords">Change passwords</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/users/add">Add user</StyledLinkRight>
                </Container>
            </div>
        );
    }
}

export default Users;
