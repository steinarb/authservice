import React from 'react';

const UserSelect = ({id, className, users, usersMap, value, onUsersFieldChange }) => (
    <select id={id} className={className} onChange={(event) => onUsersFieldChange(event.target.value, usersMap)} value={value}>
        {users.map((val) => <option key={val.userid}>{val.firstname} {val.lastname}</option>)}
    </select>
);

export default UserSelect;
