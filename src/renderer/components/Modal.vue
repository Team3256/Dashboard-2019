<template>
  <div class="modal-container" v-bind:class="{ 'modal-container-hidden': !visible }">
    <div class="modal-body" v-bind:class="{ 'modal-body-hidden': !visible }">
      <div class="modal-title">
        <h2>3256 Dashboard Beta</h2>
        <button v-if="close" @click="close" class="modal-close">
          <CloseIcon/>
        </button>
      </div>
      <div class="modal-content">
        <p>this dashboard is in beta and will break a lot</p>
      </div>
      <div class="modal-actions">
        <Button @click="visible = false">Close</Button>
      </div>
    </div>
  </div>
</template>

<script>
import CloseIcon from "../assets/close.svg";
import Button from "./Button.vue";
export default {
  props: ["visible", "close"],
  components: {
    CloseIcon,
    Button
  }
};
</script>

<style>
.modal-container {
  position: absolute;
  z-index: 100;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  transition: visibility 0.4s, opacity 0.4s linear, background-color 0.2s linear;
  visibility: visible;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-container-hidden {
  background-color: rgba(0, 0, 0, 0);
  opacity: 0;
  visibility: hidden;
}

.modal-body {
  width: auto;
  height: auto;
  border-radius: 8px;
  transition: transform 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  overflow: hidden;
}

.modal-body-hidden {
  transform: translateY(20px);
}

.modal-title {
  padding: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  background-color: var(--dark-one);
}

.modal-title h2 {
  flex: 1;
}

.modal-close {
  background: none;
  width: 25px;
  height: 25px;
  margin-left: 20px;
  float: right;
  right: 0px;
}

.modal-close svg {
  fill: white;
  height: 100%;
}

.modal-content {
  background-color: var(--light-two);
  padding: 20px;
}

.modal-actions {
  background-color: var(--dark-one);
  padding: 20px;
  padding-bottom: 10px;
  padding-top: 10px;
}
</style>
