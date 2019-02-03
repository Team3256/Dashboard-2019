<template>
  <div class="draggable-container">
    <WindowFrame>
      <WindowFrameButton @click="widgetsClicked">Widgets</WindowFrameButton>
    </WindowFrame>
    <div class="draggable-parent">
      <vue-draggable-resizable
        class="draggable"
        :min-width="600"
        :w="600"
        :max-height="465"
        :min-height="465"
        :h="465"
        :grid="[40,40]"
        :parent="true"
        v-for="(chart, i) in lineCharts"
        v-bind:key="i"
      >
        <line-chart
          :chartData="{
                      labels: chart.labels,
                      datasets: [
                          {
                            label: chart.key,
                            backgroundColor: '#f87979',
                            data: chart.data
                          }
                        ]
                      }"
        />
        <div class="button-container">
          <Button @click="onStartChart(i)" v-if="chart.listener == null">Start</Button>
          <Button @click="onStopChart(i)" v-if="chart.listener != null">Stop</Button>
          <Button @click="onResetChart(i)">Reset</Button>
        </div>
      </vue-draggable-resizable>
    </div>
  </div>
</template>

<script>
import WindowFrame from "@/components/WindowFrame";
import WindowFrameButton from "@/components/WindowFrameButton";
import LineChart from "@/components/console/LineChart.vue";
import Button from "@/components/Button";
import VueDraggableResizable from "vue-draggable-resizable";
import "vue-draggable-resizable/dist/VueDraggableResizable.css";
import { mapState } from "vuex";
import { remote } from "electron";
import { setInterval, clearInterval } from "timers";
export default {
  components: {
    WindowFrame,
    WindowFrameButton,
    LineChart,
    VueDraggableResizable,
    Button
  },
  data() {
    return {
      lineCharts: [],
      indicators: [],
      values: []
    };
  },
  computed: mapState({
    networktables: state => state.NetworkTables
  }),
  methods: {
    addLineChart() {
      this.lineCharts.push({
        key: "/SmartDashboard/robotTime",
        listener: null,
        interval: 1000,
        data: [],
        labels: []
      });
    },
    onStartChart(i) {
      var label = 0;
      this.lineCharts[i].listener = setInterval(() => {
        this.lineCharts[i].labels.push(label + "ms");
        this.lineCharts[i].data.push(
          this.networktables[this.lineCharts[i].key].value
        );
        label += this.lineCharts[i].interval;
      }, this.lineCharts[i].interval);
    },
    onStopChart(i) {
      clearInterval(this.lineCharts[i].listener);
      this.lineCharts[i].listener = null;
    },
    onResetChart(i) {
      this.lineCharts[i].data = [];
      this.lineCharts[i].labels = [];
    },
    widgetsClicked() {
      const { Menu, MenuItem } = remote;

      const menu = new Menu();

      // Build menu one item at a time, unlike
      menu.append(
        new MenuItem({
          label: "Add Line Graph",
          click: () => this.addLineChart()
        })
      );

      menu.append(
        new MenuItem({
          label: "Add Indicator",
          click() {
            ipcRenderer.send("reconnectNt");
          }
        })
      );

      menu.append(
        new MenuItem({
          label: "Add Value",
          click() {
            ipcRenderer.send("reconnectNt");
          }
        })
      );

      menu.popup(remote.getCurrentWindow());
    }
  }
};
</script>

<style>
.draggable-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.draggable-parent {
  flex: 1;
  overflow: none;
}
.draggable {
  padding: 10px;
  border-radius: 4px;
  background-color: var(--dark-one);
  z-index: 0;
}
.button-container {
  display: flex;
}
.button-container button {
  flex: 1;
  margin: 5px;
}
</style>
