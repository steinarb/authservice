import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { ROLES_RECEIVED, ROLES_ERROR } from '../actiontypes';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

class Roles extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onRoles();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let { roles } = this.state;

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
        onRoles: () => {
            axios
                .get('/authservice/useradmin/api/roles')
                .then(result => dispatch({ type: ROLES_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: ROLES_ERROR, payload: error }));
        },
    };
};

Roles = connect(mapStateToProps, mapDispatchToProps)(Roles);

export default Roles;
