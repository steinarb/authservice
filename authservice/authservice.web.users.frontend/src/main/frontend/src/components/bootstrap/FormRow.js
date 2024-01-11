import React from 'react';

export default function FormRow(props) {
    return (
        <div className="form-group row mb-2">
            {props.children}
        </div>
    );
}
