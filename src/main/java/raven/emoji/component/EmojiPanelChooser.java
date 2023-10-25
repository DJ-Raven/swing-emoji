package raven.emoji.component;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.emoji.EmojiIcon;
import raven.emoji.data.EmojiData;
import raven.emoji.data.GroupData;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.util.List;

public class EmojiPanelChooser extends JPanel {

    private String selectedGroup = "";
    private JTextPane textPane;

    public EmojiPanelChooser() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill", "fill"));
        panel = new JPanel(new MigLayout("insets 0 0 0 9,wrap 9,gap 4"));
        List<GroupData> group = EmojiIcon.getInstance().getGroups();
        JToolBar toolBar = new JToolBar();
        ButtonGroup buttonGroup = new ButtonGroup();
        for (GroupData g : group) {
            JToggleButton button = new JToggleButton(EmojiIcon.getInstance().getEmojiIcon(g.getKey(), 0.6f));
            button.addActionListener(e -> showEmoji(g.getName()));
            buttonGroup.add(button);
            toolBar.add(button);
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.putClientProperty(FlatClientProperties.STYLE, "" +
                "border: 3,3,3,3,$Component.borderColor,,10");
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:8;" +
                "trackArc:999");
        add(scroll);
        if (!group.isEmpty()) {
            showEmoji(group.get(0).getName());
        }
        add(toolBar, "dock south");
    }

    private void showEmoji(String group) {
        if (!selectedGroup.equals(group)) {
            selectedGroup = group;
            List<EmojiData> list = EmojiIcon.getInstance().filterByGroup(group);
            panel.removeAll();
            for (EmojiData d : list) {
                panel.add(createItem(d));
            }
            panel.repaint();
            panel.revalidate();
        }
    }

    private JButton createItem(EmojiData data) {
        JButton cmd = new JButton(EmojiIcon.getInstance().getEmojiIcon(data.getEmoji(), 0.6f));
        cmd.setFocusable(false);
        cmd.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        cmd.addActionListener(e -> {
            if (textPane != null) {
                insertText(data);
            }
        });
        return cmd;
    }

    private void insertText(EmojiData data) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        try {
            if (start != end) {
                String text = textPane.getSelectedText();
                textPane.getDocument().remove(start, text.length());
            }
            textPane.getDocument().insertString(start, data.getEmoji(), null);
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }

    private JPanel panel;

    public JTextPane getTextPane() {
        return textPane;
    }

    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }
}
