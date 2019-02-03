import React from 'react';

const PermissionSelect = ({id, className, permissions, permissionsMap, value, onPermissionsFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onPermissionsFieldChange(event.target.value, permissionsMap)} value={value}>
        {permissions.map((val) => <option key={val.id}>{val.permissionname}</option>)}
    </select>
);

export default PermissionSelect;
