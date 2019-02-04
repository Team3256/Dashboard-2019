<template>
  <Modal
    :visible="visible"
    :showClose="true"
    @close="$emit('close')"
    title="Set NetworkTable Value"
  >
    <template slot="content">
      <h2>Warning!</h2>
      <p>Changing unknown NetworkTable values could cause robot instability.
        <br>Use at your own risk and only change values that you know.
      </p>
      <br>
      <h3>Key</h3>
      <select v-model="selectedKey">
        <option v-for="key in keys" v-bind:key="key">{{ key }}</option>
      </select>
      <br>
      <br>
      <div v-if="nt[selectedKey]">
        <h3>Set Value</h3>
        <input type="text" v-if="nt[selectedKey].type == 'String'">
        <input type="number" v-if="nt[selectedKey].type == 'Number'">
        <input type="checkbox" v-if="nt[selectedKey].type == 'Boolean'">
        <p v-if="nt[selectedKey].type == 'StringArray'">String Array is not supported yet.</p>
        <br>
        <p>Current: {{ nt[selectedKey].value}}</p>
      </div>
    </template>
    <template slot="actions">
      <Button>Set</Button>
    </template>
  </Modal>
</template>

<script>
import Button from "@/components/Button";
import Modal from "@/components/Modal";
import { mapState } from "vuex";
export default {
  components: {
    Button,
    Modal
  },
  props: ["visible"],
  data() {
    return {
      selectedKey: ""
    };
  },
  computed: mapState({
    keys: state => Object.keys(state.NetworkTables.nt),
    nt: state => state.NetworkTables.nt
  })
};
</script>

<style>
</style>
