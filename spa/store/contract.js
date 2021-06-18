import Crud from "@/store/basestore"
const crud = new Crud('/api/contract')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_contracts_by_companyid({dispatch}, {company_id}) { await dispatch('get', {affixPath:`/company/${company_id}`, key:"contracts"}) },
    async get_contract_by_id({dispatch}, {id}) { await dispatch('get', {affixPath:`/${id}`, key:"contract"}) },
    async create_contract({dispatch, commit, state}, {payload, callbackfunc}) { 
        await dispatch('post', {key:"create_contract_result", payload})
        if (state.storage.create_contract_result) {
            commit("SET_SUCCESS", "Contract created")
            commit("SET_LIST_OPS_ADD", {
                key: "contracts",
                payload: state.storage.create_contract_result
            })
            if (callbackfunc) {
                callbackfunc();
            }
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "create_contract_result")
        }
    },
    async remove_contract({dispatch, commit, state}, {contract_id, callbackfunc}) { 
        await dispatch('delete', {affixPath:`/${contract_id}`, key:"remove_contract_result", contract_id})
        if (state.storage.remove_contract_result) {
            commit("SET_SUCCESS", "Contract removed")
            commit("SET_LIST_OPS_DETETE", {
                key: "contracts",
                id: contract_id
            })
            if (callbackfunc) {
                callbackfunc();
            }
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "remove_contract_result")
        }
    },
    async update_contract({dispatch, commit, state}, {id, payload, callbackfunc}) { 
        await dispatch('put', {affixPath:`/${id}`, key:"update_contract_result", payload})
        if (state.storage.update_contract_result) {
            commit("SET_SUCCESS", "Contract updated")
            commit("SET_LIST_OPS_UPDATE", {
                key: "contracts",
                id,
                payload: state.storage.update_contract_result
            })
            if (callbackfunc) {
                callbackfunc();
            }
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "update_contract_result")
        }
    },
}

export const getters = {
    ...crud.getters
}
