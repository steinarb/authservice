import React from 'react';

const RoleList = ({id, className, roles, rolesMap, value, onRolesFieldChange }) => (
    <select multiselect="true" size="10" id={id} className={className} onChange={(event) => onRolesFieldChange(event.target.value, rolesMap)} value={value}>
        {roles.map((val) => <option key={val.id}>{val.rolename}</option>)}
    </select>
);

export default RoleList;
