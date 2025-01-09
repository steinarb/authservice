import React from 'react';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import StyledLinkRight from './bootstrap/StyledLinkRight';

export default function Permissions() {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/">Up to the main page</StyledLinkLeft>
                <h1>Administrate permissions</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/permissions/modify">Modify permissions</StyledLinkRight><br/>
                <StyledLinkRight to="/permissions/add">Add permission</StyledLinkRight><br/>
            </Container>
        </div>
    );
}
