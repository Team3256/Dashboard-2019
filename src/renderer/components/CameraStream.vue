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
    clearInterval(this.bufferingInterval);
  },
  methods: {
    connect() {
      console.log('connect')
      if (this.streaming) {
        this.streaming.destroy();
        clearInterval(this.bufferingInterval);
      }

      this.janus = this.janus = new Janus(this.config, console);
      this.streaming = new StreamingJanusPlugin(console, false);
      this.peerConnection = new RTCPeerConnection();

      this.$emit("status", "init");

      console.log(this.janus)

      const { janus, streaming, peerConnection } = this;

      janus
        .connect()
        .then(() => {
          janus.addPlugin(streaming).then(() => {
            console.info("plugin added", janus);

            console.log(streaming);

            peerConnection.onicecandidate = event => {
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
                let startBuffer = 0;
                let lastPosition = 0;

                var lastBytesReceived = 0;
                if (this.bufferingInterval == null) {
                  this.bufferingInterval = setInterval(() => {
                    peerConnection.getStats().then((stats) => {
                      for (let stat of stats.entries()) {
                        if (stat[0] == "RTCTransport_video_1") {
                          const diff = stat[1].bytesReceived - lastBytesReceived;
                          const mbps = (diff * 8) / (1024 * 1024);
                          //mbps.toFixed(3);
                          lastBytesReceived = stat[1].bytesReceived;
                          console.log('stream', this.stream);
                          console.log('diff', diff);
                          console.log('mbps', mbps);
                          this.$emit('mbps', Number(mbps.toFixed(3)));
                        }
                      }
                    })
                    if (
                      lastPosition == videoElement.currentTime &&
                      !videoElement.paused
                    ) {
                      startBuffer += 1000;
                      this.$emit("status", "buffering");
                      if (startBuffer === 1000 * 2) {
                        startBuffer = 0;
                        this.$emit("status", "reconnecting");
                        this.connect();
                      }
                    } else {
                      this.$emit("status", "playing");
                    }

                    lastPosition = videoElement.currentTime;
                  }, 1000);
                }
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
                      offerToReceiveAudio: false,
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
          this.$emit("status", err.message);
          setTimeout(() => {
            this.connect();
          }, 1000);
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
