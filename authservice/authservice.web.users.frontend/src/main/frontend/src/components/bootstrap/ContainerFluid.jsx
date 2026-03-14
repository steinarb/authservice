import React from 'react';

export default function ContainerFluid(props) {
    return (
        <div className="container-fluid">
            {props.children}
        </div>
    );
}
