import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { PERMISSIONS_RECEIVED, PERMISSIONS_ERROR } from './actiontypes';

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
                <h1>Administrer tilganger</h1>
                <br/>
                <Link to="/authservice/useradmin/">Opp til toppen</Link><br/>
                <Link to="/authservice/useradmin/permissions/modify">Modify permissions</Link><br/>
                <Link to="/authservice/useradmin/permissions/add">Add permission</Link><br/>
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
