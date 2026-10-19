package raven.emoji;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AutoWrapText extends StyledEditorKit {

    private final int space;

    /**
     * The text pane is no longer required, the views use their own container
     */
    public AutoWrapText(JTextPane textPane) {
        this(textPane, 0);
    }

    public AutoWrapText(JTextPane textPane, int space) {
        this.space = space;
    }

    public AutoWrapText() {
        this(null, 0);
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
        public int getBreakWeight(int axis, float pos, float len) {
            if (axis == X_AXIS) {
                //  Allow the line to wrap after any emoji, also between emojis without space
                return len >= getPreferredSpan(axis) ? ExcellentBreakWeight : BadBreakWeight;
            }
            return super.getBreakWeight(axis, pos, len);
        }

        @Override
        public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException {
            //  An emoji is two or more chars, so move over the whole emoji instead of one char
            int start = getStartOffset();
            int end = getEndOffset();
            if (direction == SwingConstants.WEST) {
                biasRet[0] = Position.Bias.Forward;
                if (pos == -1 || pos > start) {
                    return start;
                }
                return -1;
            }
            if (direction == SwingConstants.EAST) {
                biasRet[0] = Position.Bias.Forward;
                if (pos == -1) {
                    return start;
                }
                if (pos < end) {
                    return end;
                }
                return -1;
            }
            return super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
        }
    }

    private class CustomParagraphView extends ParagraphView {

        public CustomParagraphView(Element elem) {
            super(elem);
            //  The document becomes i18n once it contains emoji (surrogate pairs), and the i18n TextLayoutStrategy
            //  breaks the line after almost every icon. The default flow strategy wraps icons properly
            strategy = new FlowStrategy();
        }

        @Override
        protected void paintChild(Graphics g, Rectangle alloc, int index) {
            paintSelection(g, alloc, getView(index));
            super.paintChild(g, alloc, index);
        }

        /**
         * Paint the selection with the full row height, so text and emoji get the same selection height
         */
        private void paintSelection(Graphics g, Rectangle alloc, View row) {
            Container container = getContainer();
            if (!(container instanceof JTextComponent)) {
                return;
            }
            JTextComponent text = (JTextComponent) container;
            Caret caret = text.getCaret();
            if (caret == null || !caret.isSelectionVisible() || text.getSelectionColor() == null) {
                return;
            }
            int start = Math.max(text.getSelectionStart(), row.getStartOffset());
            int end = Math.min(text.getSelectionEnd(), row.getEndOffset());
            if (start >= end) {
                return;
            }
            try {
                Rectangle rec = row.modelToView(start, Position.Bias.Forward, end, Position.Bias.Backward, alloc).getBounds();
                g.setColor(text.getSelectionColor());
                g.fillRect(rec.x, alloc.y, rec.width, alloc.height);
            } catch (BadLocationException e) {
                //  Nothing to paint
            }
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