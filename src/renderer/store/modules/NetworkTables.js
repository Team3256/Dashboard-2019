const state = {
  connected: false
};

const mutations = {
  UPDATE(state, payload) {
    console.log(state);
    state[payload.key] = {
      type: payload.type,
      value: payload.val
    };
  },
  UPDATE_CONNECTION(state, connected) {
    state.connected = connected;
  },
  RESET_STATE(state) {
    state = {
      connected: state.connected
    };
  }
};

const actions = {
  updateValue({ commit }, data) {
    console.log(data.key);
    commit("UPDATE", data);
  },
  ntConnect({ commit }) {
    commit("UPDATE_CONNECTION", true);
    commit("RESET_STATE");
  },
  ntDisconnect({ commit }) {
    commit("UPDATE_CONNECTION", false);
  }
};

export default {
  state,
  mutations,
  actions
};
