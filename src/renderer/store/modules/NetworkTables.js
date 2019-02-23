const state = {
  connected: false,
  nt: {}
};

const mutations = {
  UPDATE(state, payload) {
    console.log(state.nt);
    state.nt[payload.key.replace("/SmartDashboard/", "")] = {
      type: payload.type,
      value: payload.val
    };
  },
  UPDATE_CONNECTION(state, connected) {
    state.connected = connected;
  },
  RESET_STATE(state) {
    //state.nt = {};
    console.log(state);
  }
};

const actions = {
  updateValue({ commit }, data) {
    console.log(data.key);
    commit("UPDATE", data);
  },
  ntReset({ commit }) {
    console.log("oof");
    commit("RESET_STATE");
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
