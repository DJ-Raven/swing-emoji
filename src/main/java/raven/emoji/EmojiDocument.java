package raven.emoji;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.List;

/**
 * Styled document that replaces every supported emoji with an icon element.
 * <p>
 * Each emoji (plus an optional trailing variation selector U+FE0F) is stored in its own
 * icon element, so caret movement, selection and removal always treat it as one unit.
 */
public class EmojiDocument extends DefaultStyledDocument {

    private static final char VARIATION_SELECTOR = '️';

    private List<UndoableEdit> pendingEdits;
    private int editDepth;

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null || str.isEmpty()) {
            return;
        }
        beginEdit();
        try {
            //  Some input methods (like the windows emoji panel) send a surrogate pair as two separate inserts
            if (offs > 0 && Character.isLowSurrogate(str.charAt(0))) {
                String prev = getText(offs - 1, 1);
                if (Character.isHighSurrogate(prev.charAt(0)) && !isEmojiElement(getCharacterElement(offs - 1))) {
                    super.remove(offs - 1, 1);
                    offs--;
                    str = prev + str;
                }
            }
            //  Never insert inside an emoji, move to the end of it instead
            Element elem = getCharacterElement(offs);
            if (isEmojiElement(elem) && offs > elem.getStartOffset()) {
                offs = elem.getEndOffset();
            }
            //  Variation selector typed right after an emoji belongs to that emoji
            if (str.charAt(0) == VARIATION_SELECTOR && offs > 0) {
                Element prev = getCharacterElement(offs - 1);
                if (isEmojiElement(prev) && prev.getEndOffset() == offs) {
                    super.insertString(offs, String.valueOf(VARIATION_SELECTOR), prev.getAttributes());
                    offs++;
                    str = str.substring(1);
                }
            }
            insertSegments(offs, str, a);
        } finally {
            endEdit();
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        if (len > 0) {
            //  Expand the range so an emoji is never removed partially
            int start = offs;
            int end = offs + len;
            Element first = getCharacterElement(start);
            if (isEmojiElement(first)) {
                start = Math.min(start, first.getStartOffset());
            }
            Element last = getCharacterElement(end - 1);
            if (isEmojiElement(last)) {
                end = Math.min(Math.max(end, last.getEndOffset()), getLength());
            }
            offs = start;
            len = end - start;
        }
        super.remove(offs, len);
    }

    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        beginEdit();
        try {
            super.replace(offset, length, text, attrs);
        } finally {
            endEdit();
        }
    }

    private void insertSegments(int offs, String str, AttributeSet a) throws BadLocationException {
        AttributeSet textAttr = removeIcon(a);
        StringBuilder text = new StringBuilder();
        int index = 0;
        while (index < str.length()) {
            int codePoint = str.codePointAt(index);
            int length = Character.charCount(codePoint);
            Icon icon = EmojiIcon.getInstance().getEmojiIcon(new String(Character.toChars(codePoint)));
            if (icon != null) {
                if (index + length < str.length() && str.charAt(index + length) == VARIATION_SELECTOR) {
                    length++;
                }
                offs = flushText(offs, text, textAttr);
                //  A new icon instance per emoji keeps the attributes unique, so adjacent emojis are never merged into one element
                SimpleAttributeSet emojiAttr = new SimpleAttributeSet(textAttr);
                StyleConstants.setIcon(emojiAttr, icon);
                super.insertString(offs, str.substring(index, index + length), emojiAttr);
                offs += length;
            } else {
                text.append(str, index, index + length);
            }
            index += length;
        }
        flushText(offs, text, textAttr);
    }

    private int flushText(int offs, StringBuilder text, AttributeSet attr) throws BadLocationException {
        if (text.length() == 0) {
            return offs;
        }
        super.insertString(offs, text.toString(), attr);
        offs += text.length();
        text.setLength(0);
        return offs;
    }

    private AttributeSet removeIcon(AttributeSet a) {
        if (a == null) {
            return SimpleAttributeSet.EMPTY;
        }
        if (a.getAttribute(StyleConstants.IconAttribute) == null) {
            return a;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet(a);
        attr.removeAttribute(StyleConstants.IconAttribute);
        attr.removeAttribute(AbstractDocument.ElementNameAttribute);
        return attr;
    }

    public static boolean isEmojiElement(Element elem) {
        return elem != null && StyleConstants.getIcon(elem.getAttributes()) != null;
    }

    //  Group the edits of one insert or replace into a single undoable edit

    private void beginEdit() {
        if (editDepth++ == 0) {
            pendingEdits = new ArrayList<>();
        }
    }

    private void endEdit() {
        if (--editDepth == 0) {
            List<UndoableEdit> edits = pendingEdits;
            pendingEdits = null;
            if (edits.size() == 1) {
                super.fireUndoableEditUpdate(new UndoableEditEvent(this, edits.get(0)));
            } else if (edits.size() > 1) {
                CompoundEdit compoundEdit = new CompoundEdit();
                for (UndoableEdit edit : edits) {
                    compoundEdit.addEdit(edit);
                }
                compoundEdit.end();
                super.fireUndoableEditUpdate(new UndoableEditEvent(this, compoundEdit));
            }
        }
    }

    @Override
    protected void fireUndoableEditUpdate(UndoableEditEvent e) {
        if (pendingEdits != null) {
            pendingEdits.add(e.getEdit());
        } else {
            super.fireUndoableEditUpdate(e);
        }
    }
}
