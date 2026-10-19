package raven.emoji.component;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.emoji.EmojiIcon;
import raven.emoji.data.EmojiData;
import raven.emoji.data.GroupData;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

public class EmojiPanelChooser extends JPanel {

    private String selectedGroup = "";
    private JTextPane textPane;

    public EmojiPanelChooser() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill", "fill", "[][fill,grow][]"));
        panel = new JPanel(new MigLayout("insets 0 0 0 10,wrap 9,gap 4"));
        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search emoji");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });
        List<GroupData> group = EmojiIcon.getInstance().getGroups();
        JToolBar toolBar = new JToolBar();
        ButtonGroup buttonGroup = new ButtonGroup();
        for (GroupData g : group) {
            JToggleButton button = new JToggleButton(EmojiIcon.getInstance().getEmojiIcon(g.getKey(), 0.6f));
            button.setToolTipText(g.getName());
            button.addActionListener(e -> {
                searchField.setText("");
                showEmoji(g.getName());
            });
            buttonGroup.add(button);
            toolBar.add(button);
            if (buttonGroup.getSelection() == null) {
                button.setSelected(true);
            }
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.putClientProperty(FlatClientProperties.STYLE, "" +
                "border: 3,3,3,3,$Component.borderColor,,10");
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:8;" +
                "trackArc:999");
        add(searchField, "wrap");
        add(scroll, "wrap");
        if (!group.isEmpty()) {
            showEmoji(group.get(0).getName());
        }
        add(toolBar);
    }

    private void showEmoji(String group) {
        if (!selectedGroup.equals(group)) {
            selectedGroup = group;
            showItems(EmojiIcon.getInstance().filterByGroup(group));
        }
    }

    private void search() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            //  Show the selected group again
            String group = selectedGroup;
            selectedGroup = "";
            showEmoji(group);
        } else {
            showItems(EmojiIcon.getInstance().filterByKeywords(text));
        }
    }

    private void showItems(List<EmojiData> list) {
        panel.removeAll();
        for (EmojiData d : list) {
            panel.add(createItem(d));
        }
        panel.revalidate();
        panel.repaint();
    }

    private JButton createItem(EmojiData data) {
        JButton cmd = new JButton(EmojiIcon.getInstance().getEmojiIcon(data.getEmoji(), 0.6f));
        cmd.setFocusable(false);
        if (data.getKeywords().length > 0) {
            cmd.setToolTipText(data.getKeywords()[0]);
        }
        cmd.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        cmd.addActionListener(e -> {
            if (textPane != null) {
                insertText(data);
            }
        });
        return cmd;
    }

    private void insertText(EmojiData data) {
        //  Replace the selected text, or insert at the caret
        textPane.replaceSelection(data.getEmoji());
        textPane.requestFocusInWindow();
    }

    private JPanel panel;
    private JTextField searchField;

    public JTextPane getTextPane() {
        return textPane;
    }

    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }
}
