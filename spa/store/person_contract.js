import Crud from "@/store/basestore"
const crud = new Crud('/api/person_contract')
export const state = () => ({
    ...crud.state
})

export const mutations = {
    ...crud.mutations
}

export const actions = {
    ...crud.actions,
    async get_person_contract_mapping_by_companyid({dispatch}, {company_id}) { await dispatch('get', {affixPath:`/company/${company_id}`, key:"personcontractmapping"}) },
    async get_person_contract_mapping_by_companyid_and_personref({dispatch, commit, state}, {company_id, person_ref}) { 
        await dispatch('get', {affixPath:`/company/${company_id}/${person_ref}`, key:"personcontractmapping_by_companyid_personref"})
        if (state.storage.personcontractmapping_by_companyid_personref) {
            if (state.storage.personcontractmapping) {
                state.storage.personcontractmapping_by_companyid_personref.forEach(i => {
                    const found = state.storage.personcontractmapping.find(el => el.id === i.id)
                    if (!found) {
                        commit("SET_LIST_OPS_ADD", {
                            key: "personcontractmapping",
                            payload: i
                        })
                    }

                });
            }
           
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "personcontractmapping_by_companyid_personref")
        }

    },
    async assign_contract({dispatch, commit, state}, {company_id, person_ref, contract_id}) {
        const payload = {company_id, person_ref, contract_id } 
        await dispatch('post', {key:"assign_contract_result", payload})
        if (state.storage.assign_contract_result) {
            
            commit("SET_SUCCESS", "Contract assigned")
            commit("SET_LIST_OPS_ADD", {
                key: "personcontractmapping",
                payload: state.storage.assign_contract_result
            })
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "assign_contract_result")
        }
    },
    async withdraw_contract({dispatch, commit, state}, {company_id, person_ref, contract_id}) {
        await dispatch('delete', {affixPath:`/company/${company_id}/${person_ref}/${contract_id}`, key:"remove_contract_result"})
        if (state.storage.remove_contract_result) {
            
            if (state.storage.personcontractmapping) {
                
               const mapping = state.storage.personcontractmapping.find(i => i.company_id === company_id && i.person_ref === person_ref && i.contract_id === contract_id)
               commit("SET_LIST_OPS_DETETE", {
                    key: "personcontractmapping",
                    id: mapping.id
                })
            }
            commit("SET_SUCCESS", "Contract has been withdrawed")            
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "remove_contract_result")
        }
    },
    async remove_person_contract_by_contract_id({dispatch, commit, state}, {contract_id, callbackfunc}) {
        await dispatch('delete', {affixPath:`/contract/${contract_id}`, key:"remove_contract_by_contract_id_result"})
        if (state.storage.remove_contract_by_contract_id_result) {
            
            if (state.storage.personcontractmapping) {
                
               const mapping = state.storage.personcontractmapping.filter(i => i.contract_id === contract_id)
               mapping.forEach(i => {
                commit("SET_LIST_OPS_DETETE", {
                    key: "personcontractmapping",
                    id: i.id
                })
               });
            }
            if (callbackfunc) {
                callbackfunc();
            }
            
            commit("SET_SUCCESS", "Contract has been withdrawed for all persons")            
            //remove this item having this key, don't need anymore
            commit("SET_REMOVE_KEY", "remove_contract_by_contract_id_result")
        }
    }

}

export const getters = {
    ...crud.getters
}
