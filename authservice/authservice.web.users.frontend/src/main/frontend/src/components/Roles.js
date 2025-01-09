import React from 'react';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import StyledLinkRight from './bootstrap/StyledLinkRight';

export default function Roles() {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/">Up to the main page</StyledLinkLeft>
                <h1>Administrate roles</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/roles/modify">Modify roles</StyledLinkRight>
                <StyledLinkRight to="/roles/permissions">Change role to permission mappings</StyledLinkRight>
                <StyledLinkRight to="/roles/add">Add role</StyledLinkRight>
            </Container>
        </div>
    );
}
