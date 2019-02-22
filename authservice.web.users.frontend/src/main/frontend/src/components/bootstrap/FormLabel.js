import React, { Component } from 'react';

export const FormLabel = (props) => {
    return (
        <label htmlFor={props.htmlFor} className="col-form-label col-5">{props.children}</label>
    );
}
