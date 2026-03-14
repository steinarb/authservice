import React from 'react';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import StyledLinkRight from './bootstrap/StyledLinkRight';

function Users() {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/">Up to the main page</StyledLinkLeft>
                <h1>Adminstrate users</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/users/modify">Modify existing users</StyledLinkRight>
                <StyledLinkRight to="/users/roles">Modify users to role mappings</StyledLinkRight>
                <StyledLinkRight to="/users/passwords">Change passwords</StyledLinkRight>
                <StyledLinkRight to="/users/add">Add user</StyledLinkRight>
            </Container>
        </div>
    );
}

export default Users;
