import React from 'react';
import { connect } from 'react-redux';
import {
    PERMISSIONS_REQUEST,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkLeft } from './bootstrap/StyledLinkLeft';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';

function Permissions() {
    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/authservice/useradmin/">Up to the main page</StyledLinkLeft>
                <h1>Administrate permissions</h1>
                <div className="col-sm-2"></div>
            </nav>
            <Container>
                <StyledLinkRight to="/authservice/useradmin/permissions/modify">Modify permissions</StyledLinkRight><br/>
                <StyledLinkRight to="/authservice/useradmin/permissions/add">Add permission</StyledLinkRight><br/>
            </Container>
        </div>
    );
}

const mapStateToProps = (state) => {
    return { ...state };
};

const mapDispatchToProps = dispatch => {
    return {
        onPermissions: () => dispatch(PERMISSIONS_REQUEST()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Permissions);
