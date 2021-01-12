import React from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function Roles(props) {
    const { roles } = props;

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

const mapStateToProps = (state) => {
    return { ...state };
};

const mapDispatchToProps = dispatch => {
    return {
        onRoles: () => dispatch(ROLES_REQUEST())
    };
};

Roles = connect(mapStateToProps, mapDispatchToProps)(Roles);

export default Roles;
