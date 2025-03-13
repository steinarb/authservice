import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const api = createApi({
    reducerPath: 'api',
    baseQuery: (...args) => {
        const api = args[1];
        const basename = api.getState().basename;
        return fetchBaseQuery({ baseUrl: basename + '/api' })(...args);
    },
    endpoints: (builder) => ({
        getUsers: builder.query({ query: () => '/users' }),
        getUserRoles: builder.query({ query: () => '/users/roles' }),
        getRoles: builder.query({ query: () => '/roles' }),
        getRolePermissions: builder.query({ query: () => '/roles/permissions' }),
        getPermissions: builder.query({ query: () => '/permissions' }),
        getConfig: builder.query({ query: () => '/config' }),
        postUserModify: builder.mutation({
            query: body => ({ url: '/user/modify', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterUserModify } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterUserModify));
                } catch {}
            },
        }),
        getUserUnlock: builder.mutation({
            query: username => ({ url: '/user/unlock/' + username, method: 'GET' }),
            async onQueryStarted(username, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterUserUnlock } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterUserUnlock));
                } catch {}
            },
        }),
        postPasswordUpdate: builder.mutation({
            query: body => ({ url: '/passwords/update', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterPasswordUpdate } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterPasswordUpdate));
                } catch {}
            },
        }),
        postUserAdd: builder.mutation({
            query: body => ({ url: '/user/add', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: usersAfterUserAdd } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUsers', undefined, () => usersAfterUserAdd));
                } catch {}
            },
        }),
        postUserAddroles: builder.mutation({
            query: body => ({ url: '/user/addroles', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: userRolesAfterUserAddroles } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUserRoles', undefined, () => userRolesAfterUserAddroles));
                } catch {}
            },
        }),
        postUserRemoveroles: builder.mutation({
            query: body => ({ url: '/user/removeroles', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: userRolesAfterUserRemoveroles } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getUserRoles', undefined, () => userRolesAfterUserRemoveroles));
                } catch {}
            },
        }),
        postRoleModify: builder.mutation({
            query: body => ({ url: '/role/modify', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: rolesAfterRoleModify } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getRoles', undefined, () => rolesAfterRoleModify));
                } catch {}
            },
        }),
        postRoleAdd: builder.mutation({
            query: body => ({ url: '/role/add', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: rolesAfterRoleAdd } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getRoles', undefined, () => rolesAfterRoleAdd));
                } catch {}
            },
        }),
        postRoleAddpermissions: builder.mutation({
            query: body => ({ url: '/role/addpermissions', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: rolePermissionsAfterRoleAddpermissions } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getRolePermissions', undefined, () => rolePermissionsAfterRoleAddpermissions));
                } catch {}
            },
        }),
        postRoleRemovepermissions: builder.mutation({
            query: body => ({ url: '/role/removepermissions', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: rolePermissionsAfterRoleRemovepermissions } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getRolePermissions', undefined, () => rolePermissionsAfterRoleRemovepermissions));
                } catch {}
            },
        }),
        postPermissionModify: builder.mutation({
            query: body => ({ url: '/permission/modify', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: permissionsAfterPermissionModify } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getPermissions', undefined, () => permissionsAfterPermissionModify));
                } catch {}
            },
        }),
        postPermissionAdd: builder.mutation({
            query: body => ({ url: '/permission/add', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: permissionsAfterPermissionAdd } = await queryFulfilled;
                    dispatch(api.util.updateQueryData('getPermissions', undefined, () => permissionsAfterPermissionAdd));
                } catch {}
            },
        }),
        postConfigModify: builder.mutation({ query: body => ({ url: '/config', method: 'POST', body }) }),
   }),
});
console.log(api);

export const {
    useGetUsersQuery,
    useGetUserRolesQuery,
    useGetRolesQuery,
    useGetRolePermissionsQuery,
    useGetPermissionsQuery,
    useGetConfigQuery,
    usePostUserModifyMutation,
    useGetUserUnlockMutation,
    usePostPasswordUpdateMutation,
    usePostUserAddMutation,
    usePostUserAddrolesMutation,
    usePostUserRemoverolesMutation,
    usePostRoleModifyMutation,
    usePostRoleAddMutation,
    usePostRoleAddpermissionsMutation,
    usePostRoleRemovepermissionsMutation,
    usePostPermissionModifyMutation,
    usePostPermissionAddMutation,
    usePostConfigModifyMutation,
} = api;
