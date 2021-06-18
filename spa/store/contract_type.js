import Crud from "@/store/basestore"
const crud = new Crud('/api/contract_type')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_contract_types({dispatch}) { await dispatch('get', {key:"contract_types"}) }
}

export const getters = {
    ...crud.getters
}
