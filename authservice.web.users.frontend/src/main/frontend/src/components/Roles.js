import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
    ROLES_REQUEST,
} from '../actiontypes';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

class Roles extends Component {
    componentDidMount() {
        this.props.onRoles();
    }

    render () {
        let { roles } = this.props;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/">Up to the main page</StyledLinkLeft>
                <Header>
                    <h1>Administrate roles</h1>
                </Header>
                <Container>
                    <StyledLinkRight to="/authservice/useradmin/roles/modify">Modify roles</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/roles/permissions">Change role to permission mappings</StyledLinkRight>
                    <StyledLinkRight to="/authservice/useradmin/roles/add">Add role</StyledLinkRight>
                </Container>
            </div>
        );
    }
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
