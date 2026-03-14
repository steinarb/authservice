import React from 'react';

export default function Header(props) {
    return (
        <header>
            <div className="pb-2 mt-4 mb-2 border-bottom bg-light">
                {props.children}
            </div>
        </header>
    );
}
