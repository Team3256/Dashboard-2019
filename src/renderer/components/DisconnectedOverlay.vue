<template>
  <div class="disconnected-overlay" v-bind:class="{ 'disconnected-overlay-hidden': !show }">
    <h1>No NetworkTables Connection</h1>
    <div class="loader"></div>
  </div>
</template>

<script>


export default {
  props: ["show"]
}
</script>

<style>
.disconnected-overlay {
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
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.disconnected-overlay-hidden {
  background-color: rgba(0, 0, 0, 0);
  opacity: 0;
  visibility: hidden;
}
.loader,
.loader:after {
  border-radius: 50%;
  width: 10em;
  height: 10em;
}
.loader {
  margin: 60px auto;
  font-size: 10px;
  position: relative;
  text-indent: -9999em;
  border-top: 1.1em solid rgba(255, 255, 255, 0.2);
  border-right: 1.1em solid rgba(255, 255, 255, 0.2);
  border-bottom: 1.1em solid rgba(255, 255, 255, 0.2);
  border-left: 1.1em solid #ffffff;
  -webkit-transform: translateZ(0);
  -ms-transform: translateZ(0);
  transform: translateZ(0);
  -webkit-animation: load8 1.1s infinite linear;
  animation: load8 1.1s infinite linear;
}
@-webkit-keyframes load8 {
  0% {
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
  }
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
@keyframes load8 {
  0% {
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
  }
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
</style>
