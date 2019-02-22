import React, { Component } from 'react';

export const ContainerFluid = (props) => {
    return (
        <div className="container-fluid">
            {props.children}
        </div>
    );
}
