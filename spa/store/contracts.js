export const state = () => ({
  contract_categories: [],
  contract_types: [],
  contract_properties: []
});

export const mutations = {
  SET_CONTRACT_CATEGORIES(state, payload) {
    state.contract_categories = payload
  },
  SET_CONTRACT_TYPES(state, payload) {
    state.contract_types = payload
  },
  SET_CONTRACT_PROPERTIES(state, payload) {
    state.contract_properties = payload
  }
};
export const actions = {
  async getContractCategories({ dispatch, commit }) {
    return await this.$uow.contract.getContractCategories().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_CONTRACT_CATEGORIES", data);
      }
    });
  },
  async getContractTypes({ dispatch, commit }) {
    return await this.$uow.contract.getContractTypes().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_CONTRACT_TYPES", data);
      }
    });
  },
  async getContractProperties({ dispatch, commit }) {
    return await this.$uow.contract.getContractProperties().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_CONTRACT_PROPERTIES", data);
      }
    });
  },
  async addContractCategory({ commit }, payload) {
    await this.$uow.contract.createContractCategory(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async getContractCategoryByID({ commit }, id) {
    return await this.$uow.contract.getContractCategoryByID(id);
  },
  async updateContractCategory({ commit }, payload) {
    await this.$uow.contract.updateContractCategory(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async deleteContractCategory({ commit }, id) {
    await this.$uow.contract.deleteContractCategory(id).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async addContractType({ commit }, payload) {
    await this.$uow.contract.createContractType(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async getContractTypeByID({ commit }, id) {
    return await this.$uow.contract.getContractTypeByID(id);
  },
  async updateContractType({ commit }, payload) {
    await this.$uow.contract.updateContractType(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async deleteContractType({ commit }, id) {
    await this.$uow.contract.deleteContractType(id).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async addContractProperty({ commit }, payload) {
    await this.$uow.contract.createContractProperty(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async getContractPropertyByID({ commit }, id) {
    return await this.$uow.contract.getContractPropertyByID(id);
  },
  async updateContractProperty({ commit }, payload) {
    await this.$uow.contract.updateContractProperty(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async deleteContractProperty({ commit }, id) {
    await this.$uow.contract.deleteContractProperty(id).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
};

