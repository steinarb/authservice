import React from 'react';

const RoleSelect = ({id, className, roles, rolesMap, value, onRolesFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onRolesFieldChange(event.target.value, rolesMap)} value={value}>
        {roles.map((val) => <option key={val.id}>{val.rolename}</option>)}
    </select>
);

export default RoleSelect;
