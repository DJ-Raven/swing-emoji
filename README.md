# Swing Emoji
Java Swing project that allows you to display emojis within a `JTextPane` using SVG icons in your Java applications. It leverages the power of the Java Swing library, FlatLaf and JSVG for rendering scalable vector graphics.

<img src="https://github.com/DJ-Raven/swing-emoji/assets/58245926/f459d01d-b74a-41d7-9b28-2c83b5246a31" alt="sample dark" width="400"/>
&nbsp;
<img src="https://github.com/DJ-Raven/swing-emoji/assets/58245926/fc545b8c-682e-47f7-81aa-69ce2434ab42" alt="sample light" width="400"/>

## Features

- Display emojis within a `JTextPane` in your Java Swing applications.
- Supports a collection of 1,346 emoji icons. [here](https://github.com/DJ-Raven/swing-emoji/blob/main/src/main/resources/raven/emoji/metadata.json)

## Dependencies and resources use in this project

- [FlatLaf](https://github.com/JFormDesigner/FlatLaf): A modern, open-source look and feel for Swing applications.
- [JSVG](https://github.com/weisJ/jsvg): A library for working with SVG (Scalable Vector Graphics) in Java.
- [MigLayout](https://github.com/mikaelgrev/miglayout): A layout managers for Swing
- [JSON](https://github.com/stleary/JSON-java): A json parse for java object.
- [Microsoft Fluent UI Emoji](https://github.com/microsoft/FluentUI-Emoji): A collection of emojis provided by Microsoft.

## Example

``` java
//  install emoji svg (one time use, it is also loaded automatically on first use)
EmojiIcon.getInstance().installEmojiSvg();

//  create jtextpane with wrap text
JTextPane text = new JTextPane();
text.setEditorKit(new AutoWrapText());

// install this jtextpane to use emoji (call it after setEditorKit, because setEditorKit replaces the document)
EmojiIcon.getInstance().installTextPane(text);

// optional: ctrl+z / ctrl+y undo and redo
EditorUnit.installUndoManager(text);

// optional: emoji chooser panel, click an emoji to insert it at the caret
EmojiPanelChooser chooser = new EmojiPanelChooser();
chooser.setTextPane(text);
```

Any supported emoji typed, pasted or set with `setText` is shown as an icon. See the full demo in [Test.java](src/test/java/test/Test.java).

## How it works

- `EmojiDocument` stores every emoji (with its optional `U+FE0F` variation selector) in its own icon element.
  Removing part of an emoji removes the whole emoji, a surrogate pair sent in two parts (for example by the Windows emoji panel) is joined,
  and one insert or replace is undone in one step.
- `AutoWrapText` wraps lines between emojis, also when there is no space between them,
  moves the caret over a whole emoji and paints the selection with the same height for text and emoji.
- `EmojiPanelChooser` lists the emojis by group and can search them by keyword.

## Build and test

``` shell
mvn test
```
