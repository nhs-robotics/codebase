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

    private static class FieldTarget {
        final Object owner;
        final Field field;

        FieldTarget(Object owner, Field field) {
            this.owner = owner;
            this.field = field;
        }
    }

    private final OpMode opMode;
    private final List<FieldTarget> telemetryDataFields;
    private final List<FieldTarget> robotPositionFields;

    public TelemetryServer(OpMode opMode) {
        super(new InetSocketAddress(51631));
        this.opMode = opMode;
        this.telemetryDataFields = new ArrayList<>();
        this.robotPositionFields = new ArrayList<>();

        scanObject(opMode);

        this.setReuseAddr(true);
        this.setTcpNoDelay(true);
    }

    private void scanObject(Object obj) {
        if (obj == null) return;

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(TelemetryData.class)) {
                telemetryDataFields.add(new FieldTarget(obj, field));
            }
            if (field.isAnnotationPresent(RobotPosition.class)) {
                robotPositionFields.add(new FieldTarget(obj, field));
            }
            if (field.isAnnotationPresent(TelemetryObject.class)) {
                try {
                    Object nested = field.get(obj);
                    if (nested != null) {
                        scanObject(nested);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void loop() {
        sendUpdatePackets();
    }

    private void sendUpdatePackets() {
        for (FieldTarget target : telemetryDataFields) {
            try {
                sendPacketForField(target, false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (FieldTarget target : robotPositionFields) {
            try {
                sendPacketForField(target, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendPacketForField(FieldTarget target, boolean isRobotPosition) throws IllegalAccessException {
        target.field.setAccessible(true);
        Object fieldValue = target.field.get(target.owner);
        Class<?> fieldType = target.field.getType();

        TelemetryUpdatePacket packet = new TelemetryUpdatePacket();
        packet.telemetryDataName = target.field.getName();

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
