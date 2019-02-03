<template>
  <button class="window-frame-button" @click="onClick">
    <slot></slot>
  </button>
</template>

<script>
export default {
  methods: {
    onClick() {
      this.$emit("click");
    }
  }
};
</script>

<style>
.window-frame-button {
  background-color: inherit;
  padding-left: 5px;
  padding-right: 5px;
  height: 100%;
  -webkit-app-region: no-drag;
  z-index: 101;
}
.window-frame-button:hover {
  background-color: var(--light-two);
}
</style>
