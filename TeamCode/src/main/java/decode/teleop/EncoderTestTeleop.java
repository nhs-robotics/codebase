package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.hardware.Motor;

@TeleOp(name="Encoder Test Teleop")
public class EncoderTestTeleop extends OpMode {

    private Motor motor;

    private Telemetry.Item encoderDisplay;

    @Override
    public void init() {
        motor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_5203_2402_TICKS_PER_ROTATION);

        encoderDisplay = telemetry.addData("encoder position", 0);
    }

    @Override
    public void loop() {
        encoderDisplay.setValue(motor.getMotorEncoder().getPosition());
    }
}
