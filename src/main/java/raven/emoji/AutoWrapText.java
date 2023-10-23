package raven.emoji;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class AutoWrapText extends StyledEditorKit {

    private final int space;
    private JTextPane textPane;

    public AutoWrapText(JTextPane textPane) {
        this(textPane, 0);
    }

    public AutoWrapText(JTextPane textPane, int space) {
        this.textPane = textPane;
        this.space = space;
    }

    @Override
    public ViewFactory getViewFactory() {
        return new AutoWrapText.WarpColumnFactory();
    }

    private class WarpColumnFactory implements ViewFactory {

        @Override
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new CustomParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new CustomIconView(elem);
                }
            }
            return new LabelView(elem);
        }
    }

    private class CustomIconView extends IconView {

        public CustomIconView(Element elem) {
            super(elem);
        }

        @Override
        public float getAlignment(int axis) {
            if (axis == X_AXIS) {
                return super.getAlignment(axis);
            } else {
                //  Set Icon alignment to top
                return 0.8f;
            }
        }

        @Override
        public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException {
            int next = super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
            if (direction == SwingConstants.WEST && (pos == -1 || pos > getStartOffset())) {
                //  Still have issues when press arrow key to move cursor back
                return getStartOffset();
            }
            if (direction == SwingConstants.EAST && pos != -1) {
                return getEndOffset();
            }
            return next;
        }

        private boolean isSelected() {
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            if (start == end) {
                return false;
            }
            if (start <= getStartOffset() && end >= getEndOffset()) {
                return true;
            }
            return false;
        }

        @Override
        public void paint(Graphics g, Shape a) {
            if (isSelected()) {
                //  For test not yet fix
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(textPane.getSelectionColor());
                Rectangle2D rectangle = a.getBounds2D();
                float y = UIScale.scale(0.8f);
                float h = UIScale.scale(1.4f);
                g2.fill(new Rectangle2D.Double(rectangle.getX(), rectangle.getY() + y, rectangle.getWidth(), rectangle.getHeight() - h));
                g2.dispose();
            }
            super.paint(g, a);
        }
    }

    private class CustomParagraphView extends ParagraphView {

        public CustomParagraphView(Element elem) {
            super(elem);
        }

        @Override
        protected short getBottomInset() {
            short inset = super.getBottomInset();
            if (space > 0) {
                int index = getViewCount() - 1;
                if (index >= 0) {
                    float size = getFlowSpan(index) - getView(index).getPreferredSpan(X_AXIS);
                    if (size <= UIScale.scale(space)) {
                        int v = (inset + UIScale.scale(20));
                        return (short) v;
                    }
                }
                super.setInsets((short) 1, (short) 1, (short) 1, (short) 1);
            }
            return inset;
        }

        @Override
        public float getMinimumSpan(int axis) {
            if (axis == X_AXIS) {
                return 0;
            }
            return super.getMinimumSpan(axis);
        }
    }
}