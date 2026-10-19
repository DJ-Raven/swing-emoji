package raven.emoji;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmojiDocumentTest {

    private static final String SMILE = "😀";
    private static final String EARTH = "🌍";

    private static List<Element> characterElements(StyledDocument doc) {
        List<Element> list = new ArrayList<>();
        Element root = doc.getDefaultRootElement();
        for (int i = 0; i < root.getElementCount(); i++) {
            Element paragraph = root.getElement(i);
            for (int j = 0; j < paragraph.getElementCount(); j++) {
                list.add(paragraph.getElement(j));
            }
        }
        return list;
    }

    private static long countEmojiElements(StyledDocument doc) {
        return characterElements(doc).stream().filter(EmojiDocument::isEmojiElement).count();
    }

    @Test
    void emojiBecomesIconElement() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        doc.insertString(0, "a" + SMILE + SMILE + "b", null);
        assertEquals("a" + SMILE + SMILE + "b", doc.getText(0, doc.getLength()));
        //  Adjacent emojis must stay separate elements
        assertEquals(2, countEmojiElements(doc));
        Element first = doc.getCharacterElement(1);
        assertTrue(EmojiDocument.isEmojiElement(first));
        assertEquals(1, first.getStartOffset());
        assertEquals(3, first.getEndOffset());
        assertFalse(EmojiDocument.isEmojiElement(doc.getCharacterElement(0)));
        assertFalse(EmojiDocument.isEmojiElement(doc.getCharacterElement(5)));
    }

    @Test
    void variationSelectorIsPartOfEmoji() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        doc.insertString(0, EARTH + "️x", null);
        Element e = doc.getCharacterElement(0);
        assertTrue(EmojiDocument.isEmojiElement(e));
        assertEquals(3, e.getEndOffset());

        //  Variation selector inserted on its own after an emoji
        doc.insertString(4, SMILE, null);
        doc.insertString(6, "️", null);
        Element smile = doc.getCharacterElement(4);
        assertTrue(EmojiDocument.isEmojiElement(smile));
        assertEquals(4, smile.getStartOffset());
        assertEquals(7, smile.getEndOffset());
    }

    @Test
    void splitSurrogatePairIsJoined() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        doc.insertString(0, "a", null);
        doc.insertString(1, SMILE.substring(0, 1), null);
        doc.insertString(2, SMILE.substring(1), null);
        assertEquals("a" + SMILE, doc.getText(0, doc.getLength()));
        Element e = doc.getCharacterElement(1);
        assertTrue(EmojiDocument.isEmojiElement(e));
        assertEquals(1, e.getStartOffset());
        assertEquals(3, e.getEndOffset());
    }

    @Test
    void removeNeverLeavesHalfEmoji() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        doc.insertString(0, "a" + SMILE + "b", null);
        //  Removing only the low surrogate removes the whole emoji
        doc.remove(2, 1);
        assertEquals("ab", doc.getText(0, doc.getLength()));
        assertEquals(0, countEmojiElements(doc));
    }

    @Test
    void insertInsideEmojiMovesToEnd() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        doc.insertString(0, SMILE, null);
        doc.insertString(1, "x", null);
        assertEquals(SMILE + "x", doc.getText(0, doc.getLength()));
    }

    @Test
    void insertIsUndoneAtOnce() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        UndoManager undoManager = new UndoManager();
        doc.addUndoableEditListener(undoManager);
        doc.insertString(0, "hello " + SMILE + " world " + EARTH, null);
        undoManager.undo();
        assertEquals(0, doc.getLength());
        assertFalse(undoManager.canUndo());
        undoManager.redo();
        assertEquals("hello " + SMILE + " world " + EARTH, doc.getText(0, doc.getLength()));
        assertEquals(2, countEmojiElements(doc));
    }

    @Test
    void textAfterEmojiIsNotIcon() throws Exception {
        EmojiDocument doc = new EmojiDocument();
        doc.insertString(0, SMILE, null);
        doc.insertString(2, "abc", doc.getCharacterElement(0).getAttributes());
        assertEquals(1, countEmojiElements(doc));
        assertFalse(EmojiDocument.isEmojiElement(doc.getCharacterElement(3)));
    }

    private static JTextPane createTextPane(String text, int width) throws Exception {
        JTextPane[] result = new JTextPane[1];
        SwingUtilities.invokeAndWait(() -> {
            JTextPane textPane = new JTextPane();
            textPane.setEditorKit(new AutoWrapText());
            EmojiIcon.getInstance().installTextPane(textPane);
            textPane.setText(text);
            textPane.setSize(new Dimension(width, 1000));
            textPane.setSize(new Dimension(width, textPane.getPreferredSize().height));
            //  The text ui only handles caret navigation after the first paint
            BufferedImage image = new BufferedImage(width, textPane.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            textPane.paint(g);
            g.dispose();
            result[0] = textPane;
        });
        return result[0];
    }

    @Test
    void emojisWithoutSpaceFillTheLine() throws Exception {
        String text = "Hello world " + SMILE.repeat(30) + " end";
        JTextPane textPane = createTextPane(text, 200);
        View paragraph = textPane.getUI().getRootView(textPane).getView(0).getView(0);
        int iconWidth = EmojiIcon.getInstance().getEmojiIcon(SMILE).getIconWidth();
        int perRow = 200 / iconWidth - 2;
        int rowsWithManyIcons = 0;
        for (int i = 0; i < paragraph.getViewCount(); i++) {
            View row = paragraph.getView(i);
            assertTrue(row.getPreferredSpan(View.X_AXIS) <= 200, "row is wider than the text pane");
            if (row.getViewCount() >= perRow) {
                rowsWithManyIcons++;
            }
        }
        //  30 emojis need two full rows, before the fix every emoji got its own row
        assertTrue(rowsWithManyIcons >= 2, "emojis are not wrapped properly, rows: " + paragraph.getViewCount());
        assertTrue(paragraph.getViewCount() <= 5, "too many rows: " + paragraph.getViewCount());
    }

    @Test
    void caretMovesOverWholeEmoji() throws Exception {
        String text = "a" + SMILE + SMILE + EARTH + "️b";
        JTextPane textPane = createTextPane(text, 300);
        Position.Bias[] bias = new Position.Bias[1];
        List<Integer> east = new ArrayList<>();
        int pos = 0;
        while (pos < text.length() && east.size() < 20) {
            pos = textPane.getUI().getNextVisualPositionFrom(textPane, pos, Position.Bias.Forward, SwingConstants.EAST, bias);
            east.add(pos);
        }
        assertEquals(List.of(1, 3, 5, 8, 9), east);

        List<Integer> west = new ArrayList<>();
        pos = text.length();
        while (pos > 0 && west.size() < 20) {
            pos = textPane.getUI().getNextVisualPositionFrom(textPane, pos, Position.Bias.Forward, SwingConstants.WEST, bias);
            west.add(pos);
        }
        assertEquals(List.of(8, 5, 3, 1, 0), west);
    }
}
