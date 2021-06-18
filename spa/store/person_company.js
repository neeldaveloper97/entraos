import Crud from "@/store/basestore"
const crud = new Crud('/api/person_company')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_person_company_mapping_by_companyid({dispatch}, {company_id}) { await dispatch('get', {affixPath:`/${company_id}`, key:"personcompanymapping"}) },
    async add_person({dispatch, commit, state}, {company_id, person_ref, callbackfunc}) { 
        await dispatch('post', {affixPath:`/${company_id}/${person_ref}`, key:"add_person_result"})
        if (state.storage.add_person_result) {
            commit("SET_SUCCESS", "Person added")
            commit("SET_LIST_OPS_ADD", {
                key: "personcompanymapping",
                payload: state.storage.add_person_result
            })
            if (callbackfunc){
                callbackfunc();
            }
           
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "add_person_result")
        }
    },
    async remove_person({dispatch, commit, state}, {company_id, person_ref, id}) {
        await dispatch('delete', {affixPath:`/${company_id}/${person_ref}`, key:"remove_person_result"})
        if (state.storage.remove_person_result) {
            
            commit("SET_SUCCESS", "Person removed")
            commit("SET_LIST_OPS_DETETE", {
                key: "personcompanymapping",
                id
            })
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "remove_person_result")
        }
    }
}

export const getters = {
    ...crud.getters
}
