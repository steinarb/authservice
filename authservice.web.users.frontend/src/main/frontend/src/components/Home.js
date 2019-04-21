import React, { Component } from 'react';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

class Home extends Component {

    render () {
        return (
            <div>
                <Header>
                    <h1>User administration</h1>
                </Header>
                <Container>
                    <StyledLinkRight to="/authservice/useradmin/users">Adminstrate users</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/roles">Administrate roles</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/permissions">Administrate permissions</StyledLinkRight>
                </Container>
                <Container>
                    <a href="/authservice/">Up to authservice top</a>
                </Container>
            </div>
        );
    }
}

export default Home;
