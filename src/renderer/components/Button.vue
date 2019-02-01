<template>
  <button class="wb-button" @click="onClick">
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
.wb-button {
  background-color: var(--light-one);
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 10px;
  padding-bottom: 10px;
  border-radius: 4px;
  transition: 0.3s;
}
.wb-button:hover {
  background-color: var(--light-two);
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 10px;
  padding-bottom: 10px;
  border-radius: 4px;
}
</style>
