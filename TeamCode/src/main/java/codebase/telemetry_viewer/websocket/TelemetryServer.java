package codebase.telemetry_viewer.websocket;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import codebase.Loop;
import codebase.actions.SequentialAction;
import codebase.geometry.FieldPosition;
import codebase.telemetry_viewer.websocket.packets.TelemetryNewConnectionPacket;
import codebase.telemetry_viewer.websocket.packets.TelemetryUpdatePacket;

public class TelemetryServer extends WebSocketServer implements Loop {

    private final OpMode opMode;
    private final List<Field> telemetryDataFields;
    private final List<Field> robotPositionFields;

    public TelemetryServer(OpMode opMode) {
        super(new InetSocketAddress(51631));
        this.opMode = opMode;
        this.telemetryDataFields = new ArrayList<>();
        this.robotPositionFields = new ArrayList<>();

        for (Field field : opMode.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TelemetryData.class)) {
                this.telemetryDataFields.add(field);
            }
            if (field.isAnnotationPresent(RobotPosition.class)) {
                this.robotPositionFields.add(field);
            }
        }

        this.setReuseAddr(true);
        this.setTcpNoDelay(true);
    }

    @Override
    public void loop() {
        sendUpdatePackets();
    }

    private void sendUpdatePackets() {
        for (Field field : telemetryDataFields) {
            field.setAccessible(true);
            try {
                sendPacketForField(field, false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (Field field : robotPositionFields) {
            field.setAccessible(true);
            try {
                sendPacketForField(field, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendPacketForField(Field field, boolean isRobotPosition) throws IllegalAccessException {
        Object fieldValue = field.get(this.opMode);
        Class<?> fieldType = field.getType();

        TelemetryUpdatePacket packet = new TelemetryUpdatePacket();
        packet.telemetryDataName = field.getName();

        if (isRobotPosition && fieldType == FieldPosition.class) {
            packet.telemetryDataType = TelemetryUpdatePacket.TelemetryDataType.ROBOT_POSITION;
            if (fieldValue != null) {
                packet.telemetryDataValue = new TelemetryUpdatePacket.TelemetryFieldPosition((FieldPosition) fieldValue);
            }
        } else if (fieldType == Double.class || fieldType == double.class || fieldType == Float.class || fieldType == float.class) {
            packet.telemetryDataType = TelemetryUpdatePacket.TelemetryDataType.DOUBLE;
            packet.telemetryDataValue = fieldValue;
        } else if (fieldType == String.class) {
            packet.telemetryDataType = TelemetryUpdatePacket.TelemetryDataType.STRING;
            packet.telemetryDataValue = fieldValue;
        } else if (fieldType == FieldPosition.class) {
            packet.telemetryDataType = TelemetryUpdatePacket.TelemetryDataType.FIELD_POSITION;
            if (fieldValue != null) {
                packet.telemetryDataValue = new TelemetryUpdatePacket.TelemetryFieldPosition((FieldPosition) fieldValue);
            }
        } else if (fieldType == SequentialAction.class) {
            packet.telemetryDataType = TelemetryUpdatePacket.TelemetryDataType.ACTION_QUEUE;
            packet.telemetryDataValue = new TelemetryUpdatePacket.TelemetryActionQueue((SequentialAction) fieldValue);
        } else {
            packet.telemetryDataType = TelemetryUpdatePacket.TelemetryDataType.STRING;
            if (fieldValue != null) {
                packet.telemetryDataValue = fieldValue.toString();
            }
        }

        this.broadcast(packet.toJson());
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocket.send(new TelemetryNewConnectionPacket(this.opMode).toJson());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {}

    @Override
    public void onMessage(WebSocket webSocket, String s) {}

    @Override
    public void onError(WebSocket webSocket, Exception e) {}

    @Override
    public void onStart() {}
}
