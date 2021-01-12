import React from 'react';

export const FormRow = (props) => {
    return (
        <div className="form-group row">
            {props.children}
        </div>
    );
}
