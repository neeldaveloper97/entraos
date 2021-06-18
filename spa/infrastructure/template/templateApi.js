const resource = '/api'
export default ($axios) => ({
    getTemplates: () => $axios.get(`${resource}/template`),
    getContractCategories: () => $axios.get(`${resource}/contract_category`),
    getContractTypes: () => $axios.get(`${resource}/contract_type`),
    getContractProperties: () => $axios.get(`${resource}/contract_property`),
    createTemplate: (data) => $axios.post(`${resource}/template`, data),
    updateTemplate: (payload) => $axios.put(`${resource}/template/${payload.id}`, payload),
    getTemplateByID: (id) => $axios.get(`${resource}/template/${id}`),
});
