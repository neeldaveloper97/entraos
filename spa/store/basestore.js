import Vue from "vue";

export default class {
  constructor(endpoint) {
    this.state = {
      success: null,
      error: null,
      storage: {}
    };
    this.getters = {
      findById: state => (key, id) => state.storage[key].find(o => o.id === id),
      filterByName: state => (key, name) =>
        state.storage[key].filter(o =>
          o.name.toLowerCase().includes(name.toLowerCase())
        ),
      filterBy: state => (key, property, text) =>
        state.storage[key].filter(o =>
          o[property].toLowerCase().includes(text.toLowerCase())
        )
    };
    this.actions = {
      async get({ commit }, { affixPath = "", key = "" }) {
        try {
          commit("SET_RESET_STATUS");
          const res = await this.$axios.get(`${endpoint}${affixPath}`);
          if (res.status === 200) {
            if (res.data && key !== "") {
              commit("SET_DATA", { payload: res.data, key });
            }
          } else {
            commit("SET_ERROR", res);
          }
        } catch (err) {
          commit("SET_ERROR", err);
        }
      },

      async post({ commit }, { payload, affixPath = "", key = "" }) {
        try {
          commit("SET_RESET_STATUS");
          const res = await this.$axios.post(
            `${endpoint}${affixPath}`,
            payload
          );
          if (res.status === 200 || res.status === 201) {
            if (key !== "") {
              commit("SET_DATA", { payload: res.data || 'OK', key });
            }
          } else {
            commit("SET_ERROR", res);
          }
        } catch (err) {
          commit("SET_ERROR", err);
        }
      },

      async put({ commit }, { affixPath = "", payload, key = "" }) {
        try {
          commit("SET_RESET_STATUS");
          const res = await this.$axios.put(
            `${endpoint}${affixPath}`,
            payload
          );
          if (res.status === 200 || res.status === 204) {
            if (key !== "") {
              commit("SET_DATA", { payload: res.data || 'OK', key });
            }
          } else {
            commit("SET_ERROR", res);
          }
        } catch (err) {
          commit("SET_ERROR", err);
        }
      },

      async delete({ commit }, { affixPath = "", key = "" }) {
        try {
          commit("SET_RESET_STATUS");
          const res = await this.$axios.delete(`${endpoint}${affixPath}`);
          if (res.status === 200 || res.status === 204) {
            if (key !== "") {
              
              commit("SET_DATA", { payload: res.data || 'OK', key });
            }
          } else {
            commit("SET_ERROR", res);
          }
        } catch (err) {
          commit("SET_ERROR", err);
        }
      },

      clearStorage({ commit }) {
        console.log('clear storage called on store module ' + endpoint);
        commit("SET_CLEAR");
      },

      removeKey({ commit }, { key }) {
        commit("SET_REMOVE_KEY", key);
      }
    };

    this.mutations = {
      SET_DATA(state, { payload, key }) {
        Vue.set(state.storage, key, payload);
      },
      SET_ERROR(state, data) {
        state.error = data;
      },
      SET_LIST_OPS_ADD(state, { key, payload }) {
        if (state.storage[key]) {
          state.storage[key].push(payload);
        }
      },
      SET_LIST_OPS_UPDATE(state, { key, id, payload }) {
        if (state.storage[key]) {
          const index = state.storage[key].findIndex(function(item, i) {
            return item.id === id;
          });

          if (index !== -1) {
            Object.assign(state.storage[key][index], payload);
          }
        }
      },
      SET_LIST_OPS_UPDATE_OR_ADD(state, { key, id, payload }) {
        if (state.storage[key]) {
          const index = state.storage[key].findIndex(function(item, i) {
            return item.id === id;
          });
          if (index !== -1) {
            Object.assign(state.storage[key][index], payload);
          } else {
            state.storage[key].push(payload);
          }
        }
      },
      SET_SUCCESS(state, data) {
        state.success = data;
      },
      SET_LIST_OPS_DETETE(state, { key, id }) {
        if (state.storage[key]) {
          state.storage[key].splice(
            state.storage[key].findIndex(function(i) {
              return i.id === id;
            }),
            1
          );
        }
      },
      SET_REMOVE_KEY(state, key) {
        delete state.storage[key];
      },
      SET_CLEAR(state) {
        state.error = null;
        state.success = null;
        Object.keys(state.storage).forEach(k => delete state.storage[k]);
      },
      SET_RESET_STATUS(state) {
        state.error = null;
        state.success = null;
      }
    };
  }
}
