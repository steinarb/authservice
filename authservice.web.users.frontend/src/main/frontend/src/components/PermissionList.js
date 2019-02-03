import React from 'react';

const PermissionList = ({id, className, permissions, permissionsMap, value, onPermissionsFieldChange }) => (
    <select multiselect="true" size="10" id={id} className={className} onChange={(event) => onPermissionsFieldChange(event.target.value, permissionsMap)} value={value}>
        {permissions.map((val) => <option key={val.id}>{val.permissionname}</option>)}
    </select>
);

export default PermissionList;
