export const emptyUser = {
    userid: -1,
    username: '',
    email: '',
    firstname: '',
    lastname: '',
};

export const emptyUserAndPasswords = {
    user: { ...emptyUser },
    password1: '',
    password2: '',
};

export const emptyRole = {
    id: -1,
    rolename: '',
    description: '',
};

export const emptyPermission = {
    id: -1,
    permissionname: '',
    description: '',
};
