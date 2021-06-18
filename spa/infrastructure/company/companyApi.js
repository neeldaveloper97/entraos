const resource = '/api'
export default ($axios) => ({
    getCompanyContracts: () => $axios.get(`${resource}/company`),
});
