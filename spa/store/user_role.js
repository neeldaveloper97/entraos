import Crud from "@/store/basestore"
const crud = new Crud('/api/user_role')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_userrole({dispatch}) { await dispatch('get', {affixPath:'/', key:"userrole"}) }
}

export const getters = {
    ...crud.getters
}
