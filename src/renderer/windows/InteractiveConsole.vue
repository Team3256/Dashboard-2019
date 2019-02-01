<template>
  <div>
    <WindowFrame>
      <WindowFrameButton @click="widgetsClicked">Widgets</WindowFrameButton>
    </WindowFrame>
  </div>
</template>

<script>
import WindowFrame from '@/components/WindowFrame';
import WindowFrameButton from '@/components/WindowFrameButton';
import { mapState } from 'vuex';
import { remote } from 'electron';
export default {
  components: {
    WindowFrame,
    WindowFrameButton
  },
  data() {
    return {
    }
  },
  computed: mapState({
    connected: state => state.NetworkTables
  }),
  methods: {
    widgetsClicked() {
      const { Menu, MenuItem } = remote;

      const menu = new Menu();

      // Build menu one item at a time, unlike
      menu.append(
        new MenuItem({
          label: "Add Line Graph",
          click() {
            ipcRenderer.send('openInteractiveConsole');
          }
        })
      );

      menu.append(
        new MenuItem({
          label: "Add Indicator",
          click() {
            ipcRenderer.send('reconnectNt');
          }
        })
      );

      menu.append(
        new MenuItem({
          label: "Add Value",
          click() {
            ipcRenderer.send('reconnectNt');
          }
        })
      );

      menu.popup(remote.getCurrentWindow());
    },
  }
}
</script>

<style>
</style>
