package codebase.telemetry_viewer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import codebase.telemetry_viewer.websocket.TelemetryServer;

public abstract class TelemetryOpMode extends OpMode {
    TelemetryServer telemetryServer;

    public TelemetryOpMode(OpMode opMode) {
        this.telemetryServer = new TelemetryServer(opMode);
    }

    @Override
    public void loop() {
        this.telemetryServer.loop();
    }
}
