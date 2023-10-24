# Swing Emoji
Java Swing project that allows you to display emojis within a `JTextPane` using SVG icons in your Java applications. It leverages the power of the Java Swing library, FlatLaf and JSVG for rendering scalable vector graphics.

<img src="https://github.com/DJ-Raven/swing-emoji/assets/58245926/f459d01d-b74a-41d7-9b28-2c83b5246a31" alt="sample dark" width="400"/>
&nbsp;
<img src="https://github.com/DJ-Raven/swing-emoji/assets/58245926/fc545b8c-682e-47f7-81aa-69ce2434ab42" alt="sample light" width="400"/>

## Features

- Display emojis within a `JTextPane` in your Java Swing applications.
- Supports a collection of 1,514 emoji icons. [here](https://github.com/DJ-Raven/swing-emoji/blob/main/src/main/resources/raven/emoji/metadata.json)

## Dependencies and resources use in this project

- [FlatLaf](https://github.com/JFormDesigner/FlatLaf): A modern, open-source look and feel for Swing applications.
- [JSVG](https://github.com/weisJ/jsvg): A library for working with SVG (Scalable Vector Graphics) in Java.
- [JSON](https://github.com/stleary/JSON-java) Parse json to java object.
- [Microsoft Fluent UI Emoji](https://github.com/microsoft/FluentUI-Emoji): A collection of emojis provided by Microsoft.

## Example

``` java
//  install emoji svg (one time use)
EmojiIcon.getInstance().installEmojiSvg();

//  create jtextpane with wrap text
JTextPane text = new JTextPane();
text.setEditorKit(new AutoWrapText(text));

// install this jtextpane to use emoji
EmojiIcon.getInstance().installTextPane(text);
```
<br/>

> **Warning**
> This project still have errors, and we need your help to fix them. Please contribute to error identification and resolution.
> Feel free to open an issue or submit a pull request. We welcome any assistance and feedback! [issues](https://github.com/DJ-Raven/swing-emoji/issues)
