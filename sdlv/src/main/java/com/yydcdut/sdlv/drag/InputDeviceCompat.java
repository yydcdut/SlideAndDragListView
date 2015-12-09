package com.yydcdut.sdlv.drag;

/**
 * Helper class for accessing values in {@link android.view.InputDevice}.
 */
public class InputDeviceCompat {

    /**
     * The input source has no class.
     * <p>
     * It is up to the application to determine how to handle the device based on the device type.
     */
    public static final int SOURCE_CLASS_NONE = 0x00000000;

    /**
     * The input source has buttons or keys.
     * Examples: {@link #//SOURCE_KEYBOARD}, {@link #//SOURCE_DPAD}.
     * <p>
     * A {@link android.view.KeyEvent} should be interpreted as a button or key press.
     */
    public static final int SOURCE_CLASS_BUTTON = 0x00000001;

    /**
     * The input source is a pointing device associated with a display.
     * Examples: {@link #//SOURCE_TOUCHSCREEN}, {@link #//SOURCE_MOUSE}.
     * <p>
     * A {@link android.view.MotionEvent} should be interpreted as absolute coordinates in
     * display units according to the {@link android.view.View} hierarchy.  Pointer down/up
     * indicated when
     * the finger touches the display or when the selection button is pressed/released.
     * <p>
     * Use {@link android.view.InputDevice#getMotionRange} to query the range of the pointing
     * device.  Some devices permit
     * touches outside the display area so the effective range may be somewhat smaller or larger
     * than the actual display size.
     */
    public static final int SOURCE_CLASS_POINTER = 0x00000002;

    /**
     * The input source is a trackball navigation device.
     * Examples: {@link #//SOURCE_TRACKBALL}.
     * <p>
     * A {@link android.view.MotionEvent} should be interpreted as relative movements in
     * device-specific
     * units used for navigation purposes.  Pointer down/up indicates when the selection button
     * is pressed/released.
     * <p>
     * Use {@link android.view.InputDevice#getMotionRange} to query the range of motion.
     */
    public static final int SOURCE_CLASS_TRACKBALL = 0x00000004;

    /**
     * The input source is an absolute positioning device not associated with a display
     * (unlike {@link #SOURCE_CLASS_POINTER}).
     * <p>
     * A {@link android.view.MotionEvent} should be interpreted as absolute coordinates in
     * device-specific surface units.
     * <p>
     * Use {@link android.view.InputDevice#getMotionRange} to query the range of positions.
     */
    public static final int SOURCE_CLASS_POSITION = 0x00000008;

    /**
     * The input source is a joystick.
     * <p>
     * A {@link android.view.MotionEvent} should be interpreted as absolute joystick movements.
     * <p>
     * Use {@link android.view.InputDevice#getMotionRange} to query the range of positions.
     */
    public static final int SOURCE_CLASS_JOYSTICK = 0x00000010;

    /**
     * The input source is unknown.
     */
    public static final int SOURCE_UNKNOWN = 0x00000000;

}