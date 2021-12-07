package org.xbib.graphics.pdfbox.layout.elements;

/**
 * Utility class to create elements that allow the manipulation of the current
 * layout position. Use carefully.
 */
public class PositionControl extends ControlElement {

    /**
     * Add this element to a document to mark the current position.
     *
     * @return the created element
     */
    public static MarkPosition createMarkPosition() {
        return new MarkPosition();
    }

    /**
     * Add this element to a document to reset to the marked position.
     *
     * @return the created element
     */
    public static ResetPosition createResetPosition() {
        return new ResetPosition();
    }

    /**
     * Add this element to a document to manipulate the current layout position.
     * If <code>null</code>, the position won't be changed (useful if you want
     * to change only X or Y).
     *
     * @param newX the new X position.
     * @param newY new new Y position.
     * @return the created element
     */
    public static SetPosition createSetPosition(Float newX, Float newY) {
        return new SetPosition(newX, newY);
    }

    /**
     * Add this element to a document to manipulate the current layout position
     * by a relative amount. If <code>null</code>, the position won't be changed
     * (useful if you want to change only X or Y).
     *
     * @param relativeX the value to change position in X direction.
     * @param relativeY the value to change position in Y direction.
     * @return the created element
     */
    public static MovePosition createMovePosition(float relativeX, float relativeY) {
        return new MovePosition(relativeX, relativeY);
    }

    public static class MarkPosition extends PositionControl {

        private MarkPosition() {
            super("MARK_POSITION");
        }
    }

    public static class ResetPosition extends PositionControl {

        private ResetPosition() {
            super("RESET_POSITION");
        }
    }

    public static class SetPosition extends PositionControl {

        private final Float newX;

        private final Float newY;

        private SetPosition(Float newX, Float newY) {
            super(String.format("SET_POSITION x:%f, y%f", newX, newY));
            this.newX = newX;
            this.newY = newY;
        }

        public Float getX() {
            return newX;
        }

        public Float getY() {
            return newY;
        }
    }

    public static class MovePosition extends PositionControl {

        private final float relativeX;

        private final float relativeY;

        private MovePosition(float relativeX, float relativeY) {
            super(String.format("MOVE_POSITION x:%f, y%f", relativeX, relativeY));
            this.relativeX = relativeX;
            this.relativeY = relativeY;
        }

        public float getX() {
            return relativeX;
        }

        public float getY() {
            return relativeY;
        }
    }

    private PositionControl(String name) {
        super(name);
    }
}
