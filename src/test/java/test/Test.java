package test;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.emoji.AutoWrapText;
import raven.emoji.EmojiIcon;
import raven.emoji.component.EmojiPanelChooser;

import javax.swing.*;
import java.awt.*;


public class Test extends JFrame {

    public Test() {
        EmojiIcon.getInstance().installEmojiSvg();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIScale.scale(new Dimension(500, 500)));
        setLocationRelativeTo(null);
        setLayout(new MigLayout("fill,center,wrap", "[::350,center]", "center"));
        JPanel panel = new JPanel(new MigLayout("fill", "[fill]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");
        JTextPane text = new JTextPane();
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");

        text.setEditorKit(new AutoWrapText(text));
        EmojiIcon.getInstance().installTextPane(text);
        text.setText("\uD83C\uDF38 Spring has arrived, and the \uD83C\uDF1E sun is shining brightly. \uD83C\uDF31 Flowers are blooming everywhere, filling the air with their sweet fragrance. \uD83D\uDC26 Birds sing merrily, and it's the perfect time for a relaxing picnic \uD83C\uDF0D️ with delicious \uD83C\uDF53 fruits in the park. Let's embrace this season of renewal and joy! \uD83C\uDF3C\uD83C\uDF37\uD83C\uDF3F\uD83C\uDF1E\uD83C\uDF33\uD83C\uDF79\uD83C\uDF52\uD83D\uDC24\uD83C\uDF43\uD83C\uDF47\uD83D\uDE04\uD83C\uDF4A\uD83C\uDF49");
        panel.add(text);
        add(panel);
        EmojiPanelChooser ch = new EmojiPanelChooser();
        ch.setTextPane(text);
        add(ch);
    }

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new Test().setVisible(true));
    }
}