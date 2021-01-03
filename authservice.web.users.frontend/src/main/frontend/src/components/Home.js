import React from 'react';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function Home(props) {
    return (
        <div>
            <a className="btn btn-block btn-primary left-align-cell" href="/authservice/"><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Up to authservice top</a>
            <Header>
                <h1>User administration</h1>
            </Header>
            <Container>
                <StyledLinkRight to="/authservice/useradmin/users">Adminstrate users</StyledLinkRight>
                <StyledLinkRight to="/authservice/useradmin/roles">Administrate roles</StyledLinkRight>
                <StyledLinkRight to="/authservice/useradmin/permissions">Administrate permissions</StyledLinkRight>
            </Container>
        </div>
    );
}

export default Home;
