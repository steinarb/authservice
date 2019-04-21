import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { PERMISSIONS_RECEIVED, PERMISSIONS_ERROR } from '../actiontypes';
import { Header } from './bootstrap/Header';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

class Permissions extends Component {
    constructor(props) {
        super(props);
        this.state = { ...props };
    }

    componentDidMount() {
        this.props.onPermissions();
    }

    componentWillReceiveProps(props) {
        this.setState({ ...props });
    }

    render () {
        let { permissions } = this.state;

        return (
            <div>
                <StyledLinkLeft to="/authservice/useradmin/">Up to the main page</StyledLinkLeft>
                <Header>
                    <h1>Administrate permissions</h1>
                </Header>
                <Container>
                    <StyledLinkRight to="/authservice/useradmin/permissions/modify">Modify permissions</StyledLinkRight><br/>
                    <StyledLinkRight to="/authservice/useradmin/permissions/add">Add permission</StyledLinkRight><br/>
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
        onPermissions: () => {
            axios
                .get('/authservice/useradmin/api/permissions')
                .then(result => dispatch({ type: PERMISSIONS_RECEIVED, payload: result.data }))
                .catch(error => dispatch({ type: PERMISSIONS_ERROR, payload: error }));
        },
    };
};

Permissions = connect(mapStateToProps, mapDispatchToProps)(Permissions);

export default Permissions;
