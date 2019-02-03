<template>
  <video class="janus-stream"></video>
</template>

<script>
/* Note: you may need to add `externals: { ws: "WebSocket"},` to Your webpack configuration for this to work properly */
import { Janus, StreamingJanusPlugin } from "@techteamer/janus-api";

export default {
  props: ["config", "stream"],
  data() {
    return {
      janus: null,
      streaming: null,
      peerConnection: null
    };
  },
  beforeDestroy() {
    clearInterval(this.bitrateInterval);
    clearInterval(this.bufferingInterval);
  },
  methods: {
    connect() {
      this.janus = this.janus = new Janus(this.config, console);
      this.streaming = new StreamingJanusPlugin(console, false);
      this.peerConnection = new RTCPeerConnection();

      this.$emit("connecting", "init");

      this.bitrateInterval = setInterval(() => {
        // @TODO
        peerConnection.getStats().then(stats => {
          console.info(Array.from(stats.entries()));
        });
        this.$emit("bitrate", 0);
      }, 1000);

      const { janus, streaming, peerConnection } = this;

      janus
        .connect()
        .then(() => {
          janus.addPlugin(streaming).then(() => {
            console.info("plugin added", janus);

            peerConnection.onicecandidate = event => {
              console.log("@onicecandidate", event);
              if (!event.candidate || !event.candidate.candidate) {
                streaming.candidate({ completed: true });
              } else {
                streaming.candidate({
                  candidate: event.candidate.candidate,
                  sdpMid: event.candidate.sdpMid,
                  sdpMLineIndex: event.candidate.sdpMLineIndex
                });
              }
            };

            peerConnection.onaddstream = mediaStreamEvent => {
              console.log("@onaddstream", mediaStreamEvent);

              let videoElement = this.$el;
              videoElement.srcObject = mediaStreamEvent.stream;
              videoElement.play();

              if (this.bufferingInterval == undefined) {
                let lastPosition = 0;
                this.bufferintInterval = setInterval(() => {
                  if (
                    lastPosition == videoElement.currentTime &&
                    !videoElement.paused
                  ) {
                    this.$emit("status", "buffering");
                  } else {
                    this.$emit("status", "playing");
                  }

                  lastPosition = videoElement.currentTime;
                }, 500);
              }
            };

            streaming.on("webrtcState", (a, b) => {
              console.log("webrtcState", a, b);
            });
            streaming.on("mediaState", (a, b) => {
              console.log("mediaState", a, b);
            });
            streaming.on("statusChange", status => {
              console.log("statusChange", status);
              this.$emit("status", status);
            });

            streaming.watch(this.stream).then(jsep => {
              peerConnection
                .setRemoteDescription(new RTCSessionDescription(jsep))
                .then(() => {
                  peerConnection
                    .createAnswer({
                      offerToReceiveAudio: true,
                      offerToReceiveVideo: true
                    })
                    .then(answer => {
                      peerConnection.setLocalDescription(answer).then(() => {
                        streaming.start(answer).then(({ body, json }) => {
                          this.$emit("status", "connected");
                        });
                      });
                    });
                });
            });
          });
        })
        .catch(err => {
          console.log("error", err);
          this.$emit("status", "searching");
          setTimeout(() => {
            this.connect();
          }, 2500);
        });
    }
  },
  mounted() {
    this.connect();
  }
};
</script>

<style>
.janus-stream {
  width: 100%;
  height: 100%;
}
</style>
