import Crud from "@/store/basestore"
const crud = new Crud('/api/template')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_contract_templates({dispatch}) { await dispatch('get', {key:"contract_templates"}) }
}

export const getters = {
    ...crud.getters
}
