import Crud from "@/store/basestore"
const crud = new Crud('/api/company')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_companies_by_ids({dispatch}, {ids}) { await dispatch('get', {affixPath:`/find_by_ids/${ids}`, key:"companies"}) },
    async get_company_by_id({dispatch}, {id}) { await dispatch('get', {affixPath:`/${id}`, key:"company"}) },
    async get_companies({dispatch}) { await dispatch('get', {affixPath:`/`, key:"companies"}) },
    async create_company({dispatch, commit, state}, {payload}) { 
        await dispatch('post', {key:"create_company_result", payload})
        if (state.storage.create_company_result) {
            commit("SET_SUCCESS", "Company created")
            commit("SET_LIST_OPS_ADD", {
                key: "companies",
                payload: state.storage.create_company_result
            })
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "create_company_result")
        }
    },
    async update_company({dispatch, commit, state}, {id, payload}) { 
        await dispatch('put', {affixPath:`/${id}`, key:"update_company_result", payload})
        if (state.storage.update_company_result) {
            commit("SET_SUCCESS", "Company updated")
            commit("SET_LIST_OPS_UPDATE", {
                key: "companies",
                id,
                payload: state.storage.update_company_result
            })
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "update_company_result")
        }
    },
    //search person
    async search({dispatch}, {company_id, keyword}) {
        await dispatch('get', {affixPath:`/search/${company_id}/${keyword}`, key:"search_result"})
    },
    async clear_searchresult({commit}){
        await commit("SET_REMOVE_KEY", "search_result")
    }
}

export const getters = {
    ...crud.getters
}
