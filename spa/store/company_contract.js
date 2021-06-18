export const state = () => ({
    company_contracts: [],
  });
  
  export const mutations = {
    SET_COMPANY_CONTRACTS(state, payload) {
      state.company_contracts = payload
    }
  };
  export const actions = {
    async getCompanyContracts({ dispatch, commit }) {
      return await this.$uow.company.getCompanyContracts().then(res => {
        const { status, data } = res;
        if (status === 200) {
          commit("SET_COMPANY_CONTRACTS", data);
        }
      });
    },
  };
  
  
