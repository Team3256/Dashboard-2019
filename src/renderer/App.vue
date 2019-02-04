<template>
  <div id="app">
    <DisconnectedOverlay :show="!connected"/>
    <router-view></router-view>
  </div>
</template>

<script>
import DisconnectedOverlay from "@/components/DisconnectedOverlay";
import { mapState } from "vuex";
export default {
  name: "dashboard-2019",
  components: {
    DisconnectedOverlay
  },
  computed: mapState({
    connected: state => state.NetworkTables.connected
  })
};
</script>

<style>
/* CSS */
:root {
  --light-one: #4c555c;
  --light-two: #32383d;
  --dark-one: #202529;
  --dark-two: #181317;
}
body {
  color: white;
  user-select: none;
  display: flex;
  height: 100vh;
  max-height: 100vh;
  width: 100%;
  font-family: sans-serif;
  background-color: var(--light-one);
  overflow: hidden;
  user-select: none;
}
#app {
  flex: 1;
  display: flex;
  flex-direction: column;
}

* {
  cursor: default;
}

button {
  border: none;
  margin: 0;
  text-decoration: none;
  color: #ffffff;
  font-size: 1rem;
  line-height: 1;
  text-align: center;
  background-color: inherit;
}

button:focus {
  outline: 0;
}
</style>
