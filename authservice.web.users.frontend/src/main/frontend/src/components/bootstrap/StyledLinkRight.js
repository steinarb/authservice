import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight } from './ChevronRight';

export const StyledLinkRight = (props) => {
    return (
        <Link className="btn btn-block btn-primary mb-0 right-align-cell" to={props.to} >
            {props.children} &nbsp;<ChevronRight/>
        </Link>
    );
}
