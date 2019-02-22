import React, { Component } from 'react';

export const Container = (props) => {
    return (
        <div className="container">
            {props.children}
        </div>
    );
}
