<template>
  <div id="wrapper">
    <SetValueModal :visible="modalVisible" @close="modalVisible = false"/>
    <WindowFrame>
      <WindowFrameButton @click="dashClicked">SmartDashboard</WindowFrameButton>
      <WindowFrameButton @click="purePursuitClicked">PurePursuit</WindowFrameButton>
      <WindowFrameButton @click="cameraClicked">Camera</WindowFrameButton>
    </WindowFrame>
    <div class="camera-container" v-if="showCameras">
      <div class="camera-view">
        <camera-stream
          :config="{ url: 'ws://10.32.56.50:8188' }"
          :stream="2"
          @status="statusA = $event"
        />
        <p class="camera-view-status">{{ statusA }}</p>
      </div>
      <div class="camera-view">
        <new-camera-stream server="ws://10.32.56.50:8188" :stream="1" @status="statusB = $event"/>
        <p class="camera-view-status">{{ statusB }}</p>
        <div class="line"/>
      </div>
    </div>
  </div>
</template>

<script>
import Modal from "./Modal.vue";
import NewCameraStream from '@/components/NewCameraStream';
import CameraStream from "@/components/CameraStream";
import WindowFrame from "@/components/WindowFrame";
import WindowFrameButton from "@/components/WindowFrameButton";
import Button from "@/components/Button";
import SetValueModal from "@/components/SetValueModal";
import { remote, ipcRenderer } from "electron";
import { mapState } from "vuex";

export default {
  name: "landing-page",
  components: {
    Modal,
    WindowFrame,
    WindowFrameButton,
    Button,
    NewCameraStream,
    CameraStream,
    SetValueModal
  },
  data() {
    return {
      modalVisible: false,
      statusA: "not found",
      statusB: "not found",
      showCameras: true
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
          label: "Set NetworkTables Value",
          click: () => (this.modalVisible = true)
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
}

.camera-container {
  height: 70%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
}

.camera-view {
  width: calc((100vh / 20) * 16);
  height: calc((100vh / 20) * 9);
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
.line {
  height: 100%;
  border: 2px solid #39ff14;
  position: absolute;
}
</style>
