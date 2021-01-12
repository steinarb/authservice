import React from 'react';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

export default function Roles(props) {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/">Up to the main page</StyledLinkLeft>
                <h1>Administrate roles</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/authservice/useradmin/roles/modify">Modify roles</StyledLinkRight>
                <StyledLinkRight to="/authservice/useradmin/roles/permissions">Change role to permission mappings</StyledLinkRight>
                <StyledLinkRight to="/authservice/useradmin/roles/add">Add role</StyledLinkRight>
            </Container>
        </div>
    );
}
