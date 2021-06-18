import Crud from "@/store/basestore"
const crud = new Crud('/api/contract_category')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_contract_categories({dispatch}) { await dispatch('get', {key:"categories"}) }
}

export const getters = {
    ...crud.getters
}
