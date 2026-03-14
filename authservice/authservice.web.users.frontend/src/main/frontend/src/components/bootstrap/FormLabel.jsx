import React from 'react';

export default function FormLabel(props) {
    return (
        <label htmlFor={props.htmlFor} className="col-form-label col-5">{props.children}</label>
    );
}
