<template>
  <div class="window-frame" v-bind:class="{ 'window-draggable': draggable }">
    <WindowFrameButton class="window-frame-button" @click="fileClicked">File</WindowFrameButton>
    <slot></slot>
    <div style="flex: 1"/>
    <button class="window-frame-button close-button" @click="onClose">
      <CloseIcon/>
    </button>
  </div>
</template>

<script>
import WindowFrameButton from "@/components/WindowFrameButton";
import CloseIcon from "../assets/close.svg";
import { remote } from "electron";
export default {
  components: {
    CloseIcon,
    WindowFrameButton
  },
  props: ["draggable"],
  methods: {
    fileClicked() {
      const { Menu, MenuItem } = remote;

      const menu = new Menu();

      // Build menu one item at a time, unlike
      menu.append(
        new MenuItem({
          label: "Open Debug Menu",
          click() {
            remote.getCurrentWebContents().openDevTools();
          }
        })
      );

      menu.append(new MenuItem({ type: "separator" }));
      menu.append(
        new MenuItem({
          label: "Exit",
          click() {
            remote.getCurrentWindow().close();
          }
        })
      );

      menu.popup(remote.getCurrentWindow());
    },
    onClose() {
      remote.getCurrentWindow().close();
    }
  }
};
</script>

<style>
.window-frame {
  width: 100vw;
  height: 25px;
  padding-left: 5px;
  padding-right: 5px;
  background-color: var(--dark-two);
  display: flex;
  z-index: 6;
}
.window-draggable {
  -webkit-app-region: drag;
}
.close-button {
  display: flex;
}
.close-button svg {
  fill: white;
  height: 70%;
}
</style>
