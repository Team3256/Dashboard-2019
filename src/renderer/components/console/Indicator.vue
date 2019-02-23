<template>
  <div class="indicator" v-bind:class="{ 'indicator-active': value }">
    <p class="indicator-config">C</p>
    {{ dashboardKey }}
  </div>
</template>

<script>
export default {
  props: ["value", "dashboardKey"]
};
</script>

<style>
.indicator {
  height: 100%;
  width: 100%;
  background-color: red;
  transition: 1s background-color;
}
.indicator-active {
  background-color: green;
}
.indicator-config {
  transition: 0.3s opacity;
  opacity: 0;
  position: absolute;
  top: 10px;
  right: 10px;
}
.indicator-config:hover {
  opacity: 1;
}
</style>
