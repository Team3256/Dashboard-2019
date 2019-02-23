<template>
  <div
    id="wrapper"
    v-bind:class="{ 'is-blue': alliance =='Blue', 'is-red': alliance =='Red', 'is-none': (alliance != 'Blue' && alliance !='Red') }"
  >
    <SetValueModal :visible="modalVisible" @close="modalVisible = false"/>
    <WindowFrame>
      <WindowFrameButton @click="dashClicked">SmartDashboard</WindowFrameButton>
      <WindowFrameButton @click="purePursuitClicked">PurePursuit</WindowFrameButton>
      <WindowFrameButton @click="cameraClicked">Camera</WindowFrameButton>
    </WindowFrame>
    <div class="match-information">
      <h1>Match {{ match }}</h1>
      <h1>{{ regional }}</h1>
    </div>
    <div class="camera-container" v-if="showCameras">
      <div
        class="camera-view"
        v-bind:class="{ 'is-blue-dark': alliance == 'Blue', 'is-red-dark': alliance =='Red', 'is-none-dark': (alliance != 'Blue' && alliance !='Red') }"
      >
        <camera-stream
          :config="{ url: 'ws://10.32.56.50:8188' }"
          :stream="1"
          @status="statusA = $event"
          @mbps="mbpsA = $event"
        />
        <p class="camera-view-status">{{ statusB }}</p>
        <div class="line"/>
      </div>
      <div
        class="camera-view"
        v-bind:class="{ 'is-blue-dark': alliance == 'Blue', 'is-red-dark': alliance =='Red', 'is-none-dark': (alliance != 'Blue' && alliance !='Red') }"
      >
        <camera-stream
          :config="{ url: 'ws://10.32.56.50:8188' }"
          :stream="2"
          @status="statusB = $event"
          @mbps="mbpsB = $event"
        />
        <p class="camera-view-status">{{ statusA }}</p>
        <div class="line"/>
      </div>
    </div>
    <div class="camera-stats-container">
      <p v-bind:class="{'warning': mbpsA >= 2.0}">mbpsA: {{ mbpsA }}</p>
      <p
        v-bind:class="{'warning': mbpsA+mbpsB >= 3.0}"
      >Total mbps: {{ Number(mbpsA + mbpsB).toFixed(3) }}</p>
      <p v-bind:class="{'warning': mbpsB >= 2.0}">mbpsB: {{ mbpsB }}</p>
    </div>
    {{alliance}}
  </div>
</template>

<script>
import Modal from "./Modal.vue";
import CameraStream from "@/components/CameraStream";
import WindowFrame from "@/components/WindowFrame";
import WindowFrameButton from "@/components/WindowFrameButton";
import Button from "@/components/Button";
import SetValueModal from "@/components/SetValueModal";
import { remote, ipcRenderer } from "electron";
import { mapState } from "vuex";

import store from "../store";

export default {
  name: "landing-page",
  components: {
    Modal,
    WindowFrame,
    WindowFrameButton,
    Button,
    CameraStream,
    SetValueModal
  },
  data() {
    return {
      modalVisible: false,
      mbpsA: 0,
      mbpsB: 0,
      statusA: "not found",
      statusB: "not found",
      showCameras: true
    };
  },
  computed: mapState({
    connected: state => state.NetworkTables.connected,
    alliance: state =>
      state.NetworkTables.nt.alliance
        ? state.NetworkTables.nt.alliance.value
        : "",
    regional: state =>
      state.NetworkTables.nt.regional
        ? state.NetworkTables.nt.regional.value
        : "",
    match: state =>
      state.NetworkTables.nt.match ? state.NetworkTables.nt.match.value : -1
  }),
  methods: {
    onClose() {
      this.modalVisible = !this.modalVisible;
    },
    dashClicked() {
      const { Menu, MenuItem } = remote;

      const menu = new Menu();

      // Build menu one item at a time, unlike
      menu.append(
        new MenuItem({
          label: "Open Interactive Console",
          click() {
            ipcRenderer.send("openInteractiveConsole");
          }
        })
      );

      menu.append(
        new MenuItem({
          label: "Set NetworkTables Value",
          click: () => (this.modalVisible = true)
        })
      );

      menu.append(
        new MenuItem({
          label: "Flush Values",
          click() {
            ipcRenderer.send("flushValues");
          }
        })
      );

      menu.popup(remote.getCurrentWindow());
    },
    purePursuitClicked() {
      const { Menu, MenuItem } = remote;

      const menu = new Menu();

      // Build menu one item at a time, unlike
      menu.append(
        new MenuItem({
          label: "Path Creator",
          click() {
            remote.getCurrentWebContents().openDevTools();
          }
        })
      );

      menu.append(
        new MenuItem({
          label: "Configure",
          click() {
            remote.getCurrentWebContents().openDevTools();
          }
        })
      );

      menu.popup(remote.getCurrentWindow());
    },
    cameraClicked() {
      const { Menu, MenuItem } = remote;

      const menu = new Menu();

      // Build menu one item at a time, unlike
      menu.append(
        new MenuItem({
          label: "Reload Cameras",
          click: () => {
            this.showCameras = false;
            setTimeout(() => {
              this.showCameras = true;
            }, 1000);
          }
        })
      );

      menu.popup(remote.getCurrentWindow());
    }
  },
  mounted() {}
};
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

#wrapper {
  flex: 1;
  transition: 1s;
}

.match-information {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.is-none {
  background-color: none;
}

.is-blue {
  background-color: #0000b2;
}

.is-red {
  background-color: #990000;
}

.is-none-dark {
  background-color: var(--light-two);
}

.is-blue-dark {
  background-color: #000066;
}

.is-red-dark {
  background-color: #7f0000;
}

.camera-container {
  margin-top: 25px;
  margin-bottom: 25px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
}

.camera-view {
  transition: 1s;
  width: calc((100vw / 33) * 16);
  height: calc((100vw / 33) * 9);
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
}
.camera-view p {
  font-size: 36px;
}
.camera-view-status {
  font-size: 18px !important;
  position: absolute;
  left: 10px;
  bottom: 5px;
  z-index: 2;
  opacity: 0.8;
}
.camera-mbps {
  font-size: 18px !important;
  position: absolute;
  right: 10px;
  bottom: 5px;
  z-index: 2;
  opacity: 1;
  color: #39ff14;
}
.line {
  height: 100%;
  border: 2px solid #39ff14;
  position: absolute;
}
.warning {
  color: red;
}
.camera-stats-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  font-size: 1.4rem;
}
</style>
