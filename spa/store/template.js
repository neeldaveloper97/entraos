export const state = () => ({
  templates: [],
  contract_categories: [],
  contract_types: [],
  contract_properties: []
});

export const mutations = {
  SET_TEMPLATES(state, payload) {
    state.templates = payload
  },
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
  getTemplates({ dispatch, commit }) {
    this.$uow.template.getTemplates().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_TEMPLATES", data);
      }
    });
  },
  async getContractCategories({ dispatch, commit }) {
    return await this.$uow.template.getContractCategories().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_CONTRACT_CATEGORIES", data);
      }
    });
  },
  async getContractTypes({ dispatch, commit }) {
    return await this.$uow.template.getContractTypes().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_CONTRACT_TYPES", data);
      }
    });
  },
  async getContractProperties({ dispatch, commit }) {
    return await this.$uow.template.getContractProperties().then(res => {
      const { status, data } = res;
      if (status === 200) {
        commit("SET_CONTRACT_PROPERTIES", data);
      }
    });
  },
  async createTemplate({ commit }, payload) {
    await this.$uow.template.createTemplate(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async updateTemplate({ commit },payload) {
    await this.$uow.template.updateTemplate(payload).then(res => {
      console.log(res)
    }).catch(err => {
      console.log(err)
    });
  },
  async getTemplateByID({ commit },id) {
    return await this.$uow.template.getTemplateByID(id);
  }
};
