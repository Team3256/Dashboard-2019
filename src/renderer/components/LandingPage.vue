<template>
  <div id="wrapper">
    <Modal :visible="modalVisible"/>
    <WindowFrame>
      <WindowFrameButton @click="dashClicked">SmartDashboard</WindowFrameButton>
      <WindowFrameButton @click="purePursuitClicked">PurePursuit</WindowFrameButton>
    </WindowFrame>
    <div class="camera-container">
      <div class="camera-view">
        <camera-stream
          :config="{ url: 'ws://192.168.7.247:8188' }"
          :stream="2"
          @status="statusA = $event"
        />
        <p class="camera-view-status">{{ statusA }}</p>
      </div>
      <div class="camera-view">
        <camera-stream
          :config="{ url: 'ws://192.168.7.247:8188' }"
          :stream="2"
          @status="statusB = $event"
        />
        <p class="camera-view-status">{{ statusB }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import Modal from "./Modal.vue";
import CameraStream from "@/components/CameraStream";
import WindowFrame from "@/components/WindowFrame";
import WindowFrameButton from "@/components/WindowFrameButton";
import { remote, ipcRenderer } from "electron";
import { mapState } from "vuex";

export default {
  name: "landing-page",
  components: {
    Modal,
    WindowFrame,
    WindowFrameButton,
    CameraStream
  },
  data() {
    return {
      modalVisible: false,
      statusA: "null",
      statusB: "null"
    };
  },
  computed: mapState({
    connected: state => state.NetworkTables.connected
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
          label: "Manual Reconnect",
          click() {
            ipcRenderer.send("reconnectNt");
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
    }
  },
  mounted() {}
};
</script>

<style>
@import url("https://fonts.googleapis.com/css?family=Source+Sans+Pro");

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

#wrapper {
  flex: 1;
}

.camera-container {
  height: 80%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
}

.camera-view {
  width: calc(3.5em * 16);
  height: calc(3.5em * 9);
  background-color: var(--light-two);
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
  opacity: 0.25;
}
</style>
