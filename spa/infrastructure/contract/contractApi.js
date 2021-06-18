const resource = '/api'
export default ($axios) => ({
    getContractCategories: () => $axios.get(`${resource}/contract_category`),
    getContractTypes: () => $axios.get(`${resource}/contract_type`),
    getContractProperties: () => $axios.get(`${resource}/contract_property`),
    createContractCategory: (payload) => $axios.post(`${resource}/contract_category`, payload),
    getContractCategoryByID: (id) => $axios.get(`${resource}/contract_category/${id}`),
    updateContractCategory: (payload) => $axios.put(`${resource}/contract_category/${payload.id}`, payload),
    deleteContractCategory: (id) => $axios.delete(`${resource}/contract_category/${id}`),
    createContractType: (payload) => $axios.post(`${resource}/contract_type`, payload),
    getContractTypeByID: (id) => $axios.get(`${resource}/contract_type/${id}`),
    updateContractType: (payload) => $axios.put(`${resource}/contract_type/${payload.id}`, payload),
    deleteContractType: (id) => $axios.delete(`${resource}/contract_type/${id}`),
    createContractProperty: (payload) => $axios.post(`${resource}/contract_property`, payload),
    getContractPropertyByID: (id) => $axios.get(`${resource}/contract_property/${id}`),
    updateContractProperty: (payload) => $axios.put(`${resource}/contract_property/${payload.id}`, payload),
    deleteContractProperty: (id) => $axios.delete(`${resource}/contract_property/${id}`),
});
