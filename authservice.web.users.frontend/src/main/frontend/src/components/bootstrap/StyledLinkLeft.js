import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeft } from './ChevronLeft';

export const StyledLinkLeft = (props) => {
    return (
        <Link className="btn btn-primary mb-0 left-align-cell" to={props.to} >
            <ChevronLeft/>&nbsp; {props.children}
        </Link>
    );
}
