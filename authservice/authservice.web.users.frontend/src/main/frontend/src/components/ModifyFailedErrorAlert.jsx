import React from 'react';
import { useSelector } from 'react-redux';

export default function ModifyFailedErrorAlert() {
    const modifyFailedError = useSelector(state => state.modifyFailedError);

    if (!modifyFailedError) {
        return null;
    }

    return(
        <div className="alert alert-primary" role="alert">{modifyFailedError}</div>
    );
}
