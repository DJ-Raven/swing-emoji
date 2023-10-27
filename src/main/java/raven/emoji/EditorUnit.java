package raven.emoji;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class EditorUnit {

    public static UndoManager installUndoManager(JTextComponent text) {
        UndoManager undoManager = new UndoManager();
        Action undoAction = new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        };
        Action redoAction = new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        };

        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        text.getInputMap().put(undoKeyStroke, "undo");
        text.getInputMap().put(redoKeyStroke, "redo");
        text.getActionMap().put("undo", undoAction);
        text.getActionMap().put("redo", redoAction);
        text.getDocument().addUndoableEditListener(undoManager);
        return undoManager;
    }
}
