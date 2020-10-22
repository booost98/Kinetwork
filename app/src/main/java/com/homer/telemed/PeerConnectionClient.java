package com.homer.telemed;


public class PeerConnectionClient {
    public static class DataChannelParameters {
        public final boolean ordered;
        public final int maxRetransmitTimeMs;
        public final int maxRetransmits;
        public final String protocol;
        public final boolean negotiated;
        public final int id;

        public DataChannelParameters(boolean ordered, int maxRetransmitTimeMs, int maxRetransmits,
                                     String protocol, boolean negotiated, int id) {
            this.ordered = ordered;
            this.maxRetransmitTimeMs = maxRetransmitTimeMs;
            this.maxRetransmits = maxRetransmits;
            this.protocol = protocol;
            this.negotiated = negotiated;
            this.id = id;
        }
    }
    public static class PeerConnectionParameters {
        public final boolean videoCallEnabled;
        public final boolean loopback;
        public final int videoWidth;
        public final int videoHeight;
        public final int videoFps;
        public final int videoStartBitrate;
        public final String videoCodec;
        public final boolean videoCodecHwAcceleration;
        public final int audioStartBitrate;
        public final String audioCodec;
        public final boolean cpuOveruseDetection;
        public PeerConnectionParameters(
                boolean videoCallEnabled, boolean loopback,
                int videoWidth, int videoHeight, int videoFps, int videoStartBitrate,
                String videoCodec, boolean videoCodecHwAcceleration,
                int audioStartBitrate, String audioCodec,
                boolean cpuOveruseDetection) {
            this.videoCallEnabled = videoCallEnabled;
            this.loopback = loopback;
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            this.videoFps = videoFps;
            this.videoStartBitrate = videoStartBitrate;
            this.videoCodec = videoCodec;
            this.videoCodecHwAcceleration = videoCodecHwAcceleration;
            this.audioStartBitrate = audioStartBitrate;
            this.audioCodec = audioCodec;
            this.cpuOveruseDetection = cpuOveruseDetection;
        }
    }
}
