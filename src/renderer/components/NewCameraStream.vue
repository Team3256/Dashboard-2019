<template>
  <video class="janus-stream" ref="videoElement"></video>
</template>

<script>
import Janus from '../assets/Janus.js';
export default {
  props: ['server', 'stream'],
  data() {
    return {
      janus: null,
      streaming: null,
      bitrateTimer: null
    }
  },
  methods: {
    log(text) {
      console.log(`[JANUS-${this.stream}] ${text}`);
      this.$emit('status', text);
    }
  },
  mounted() {
    var opaqueId = "streamingtest-"+Janus.randomString(12);
    Janus.init({
      console: console,
      debug: "all",
      callback: () => {
        this.log('extension initialized');
        this.janus = new Janus({
          destroyed: function() {
						window.location.reload();
					},
          error: () => {
            this.log("error connecting janus");
          },
          success: () => {
            this.janus.attach({
              opaqueId: opaqueId,
              plugin: 'janus.plugin.streaming',
              server: this.server,
              onremotestream: (stream) => {
                this.log('remote stream responded');
                Janus.addVideoStream(this.$refs.videoElement);
                var videoTracks = stream.getVideoTracks();
                if (videoTracks === null || videoTracks === undefined || videoTracks.length === 0) {
                  this.log('no data found in stream');
                }
                if (videoTracks && videoTracks.length) {
                  this.bitrateTimer = setInterval(() => {
                    // Display updated bitrate, if supported
                    var bitrate = streaming.getBitrate();
                    this.$emit('status', 'bitrate: ' + bitrate);
                  }, 1000);
                }
              },
              success: (pluginHandle) => {
                this.streaming = pluginHandle;
                this.log("Plugin attached! (" + streaming.getPlugin() + ", id=" + streaming.getId() + ")");
                var body = { "request": "watch", id: this.stream };
                streaming.send({ "message": body });
              },
              error: () => {
                this.log("error attaching plugin");
              }
            });
          }
        });
      },
      error: () => {
        this.log("error initializing extension");
      }
    });
  }
}
</script>

<style>
.janus-stream {
  width: 100%;
  height: 100%;
}
</style>
