import React from 'react';
import { Link } from 'react-router-dom';
import ChevronRight from './ChevronRight';

export default function StyledLinkRight(props) {
    return (
        <Link className="btn btn-block btn-primary mb-2 right-align-cell" to={props.to} >
            {props.children} &nbsp;<ChevronRight/>
        </Link>
    );
}
