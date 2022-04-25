/* 
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;
import javax.swing.border.*;
import javax.swing.event.*;
import srl.tools.struct.*;
import srl.project.SrlProject;
import srl.wordlist.WordList;
import srl.wordlist.WordListSet;

public class AutoCompleteTextField extends JTextArea {

    TreeSet<String> entitiesKeyWords = new TreeSet<String>();
    HashMap<String, TreeSet<String>> entityValues = new HashMap<String, TreeSet<String>>();
    TreeSet<String> wordLists = new TreeSet<String>();
    public static final String[] KEY_WORDS = {"list","strmatches", "words","regex", "ortho","begins","ends","contains","optional"};
    PopUpWindow popupMenu;
    JWindow theMenu;
    boolean ignoreNextSpace = false;
    boolean ignoreNextEnter = false;
    boolean showPopupMenu = false;

    public AutoCompleteTextField() {
        setDocument(new AutoDoc());
        for (String s : KEY_WORDS) {
            entitiesKeyWords.add(s);
        }
        setLineWrap(true);
        setWrapStyleWord(true);
        getCaret().addChangeListener(new ChangeListener() {

            AutoCompleteTextField actf = AutoCompleteTextField.this;

            public void stateChanged(ChangeEvent e) {
                if (!showPopupMenu) {
                    return;
                }
                Point p = getCaret().getMagicCaretPosition();
                if (popupMenu != null && p != null) {
                    Point p2 = actf.getLocationOnScreen();
                    popupMenu.setLocation(new Point(p2.x + p.x, p2.y + actf.getRowHeight()));
                    popupMenu.setVisible(true);
                }
            }
        });
        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (popupMenu.isVisible()) {
                        int selIdx = popupMenu.theList.getSelectedIndex();
                        if (selIdx + 1 >= popupMenu.theList.getModel().getSize()) {
                            selIdx = 0;
                        } else {
                            selIdx++;
                        }
                        popupMenu.theList.setSelectedIndex(selIdx);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (popupMenu.isVisible()) {
                        int selIdx = popupMenu.theList.getSelectedIndex();
                        if (selIdx <= 0) {
                            selIdx = popupMenu.theList.getModel().getSize() - 1;
                        } else {
                            selIdx--;
                        }
                        popupMenu.theList.setSelectedIndex(selIdx);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popupMenu.setVisible(false);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (popupMenu != null && popupMenu.isVisible()) {
                        if (getText().matches(".* ")) {
                            setText(getText().substring(0, getText().length() - 1));
                        }
                        if(popupMenu.theList.getSelectedValue()!= null) {
                            complete((String) popupMenu.theList.getSelectedValue());
                            ignoreNextSpace = true;
                        }
                    }
                } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (popupMenu != null && popupMenu.isVisible()) {
                        if(getText().matches(".* ")) {
                            setText(getText().substring(0, getText().length() - 1));
                        }
                        if(popupMenu.theList.getSelectedValue()!= null) {
                            complete((String) popupMenu.theList.getSelectedValue());
                            ignoreNextEnter = true;
                        }
                    }
                }
            }
        });
        try {
            SrlProject proj = SRLGUIApp.getApplication().proj;
            for (Pair<String, String> ent : proj.entities) {
                entitiesKeyWords.add(ent.first);
                if (!entityValues.containsKey(ent.first)) {
                    entityValues.put(ent.first, new TreeSet<String>());
                }
                entityValues.get(ent.first).add(ent.second);
            }
            proj.entities.addCollectionChangeListener(new CollectionChangeListener<Pair<String, String>>() {

                public void collectionChanged(CollectionChangeEvent<Pair<String, String>> e) {
                    if (e.getOldVal() != null) {
                        entitiesKeyWords.remove(e.getOldVal().first);
                        entityValues.remove(e.getOldVal().first);
                    }
                    if (e.getNewVal() != null) {
                        entitiesKeyWords.add(e.getNewVal().first);
                        if (!entityValues.containsKey(e.getNewVal().first)) {
                            entityValues.put(e.getNewVal().first, new TreeSet<String>());
                        }
                        entityValues.get(e.getNewVal().first).add(e.getNewVal().second);
                    }
                }
            });
            for (WordListSet wl : proj.wordlists) {
                wl.addChangeListener(new WLCCL());
                for (String s : wl.getWordListNames()) {
                    wordLists.add(s);
                }
            }
            proj.wordlists.addCollectionChangeListener(new CollectionChangeListener<WordListSet>() {

                public void collectionChanged(CollectionChangeEvent<WordListSet> e) {
                    if (e.getNewVal() != null) {
                        e.getNewVal().addChangeListener(new WLCCL());
                    }
                }
            });
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private class WLCCL implements CollectionChangeListener<WordList> {

        public void collectionChanged(CollectionChangeEvent<WordList> e) {
            if (e.getOldVal() != null) {
                wordLists.remove(e.getReference());
            }
            if (e.getNewVal() != null) {
                wordLists.add((String) e.getReference());
            }
        }
    }

    private void showMatches(SortedSet<String> matches) {
        if (matches == null || matches.size() == 1 && getText().matches(".*" + matches.first()) ||
                matches.size() == 0) {
            if (popupMenu != null) {
                popupMenu.setVisible(false);
            }
            showPopupMenu = false;
            return; // Already there exactly
        }
        if (popupMenu == null) {
            popupMenu = new PopUpWindow(this);
            Point p2 = getLocationOnScreen();
            popupMenu.setLocation(new Point(p2.x, p2.y + getHeight()));
        }
        popupMenu.setWords(matches);
        popupMenu.setVisible(true);
        showPopupMenu = true;
    }

    private void onTextChange() {
        String text = getText().substring(0, getCaretPosition());
        if (text.matches(".* [a-z][A-Za-z0-9]*")) {
            Matcher m = Pattern.compile(".* ([a-z][A-Za-z0-9]*)").matcher(text);
            m.matches();
            showMatches(entitiesKeyWords.subSet(m.group(1), m.group(1) + "{"));
        } else if (text.matches(".* (strmatches|list|optional|not)\\s*\\(\\s*@\\w*")) {
            Matcher m = Pattern.compile(".* (strmatches|list|optional|not)\\s*\\(\\s*@(\\w*)").matcher(text);
            m.matches();
            if (m.group(2) != null && m.group(2).length() > 0) {
                showMatches(wordLists.subSet(m.group(2), m.group(2) + "{"));
            } else {
                showMatches(wordLists);
            }
        } else if(text.matches(".* (strmatches|list|optional|not)\\s*\\(\\s*%\\w*")) {
            Matcher m = Pattern.compile(".* (strmatches|list|optional|not)\\s*\\(\\s*%(\\w*)").matcher(text);
            m.matches();
            TreeSet<String> setNames = new TreeSet<String>();
            for(WordListSet wls : SRLGUIApp.getApplication().proj.wordlists) {
                setNames.add(wls.name);
            }
            if(m.group(2) != null && m.group(2).length() > 0) {
                showMatches(setNames.subSet(m.group(2), m.group(2) + "{"));
            } else {
                showMatches(setNames);
            }

        } else {
            Matcher m = Pattern.compile(".* ([a-z][A-Za-z0-9]*)\\((\\w*)").matcher(text);
            if (!m.matches() || entityValues.get(m.group(1)) == null) {
                showMatches(null);
                return;
            }
            showMatches(entityValues.get(m.group(1)).subSet(m.group(2), m.group(2) + "{"));
        }
    }

    public TreeSet<String> getEntitiesKeyWords() {
        return entitiesKeyWords;
    }

    public HashMap<String, TreeSet<String>> getEntityValues() {
        return entityValues;
    }

    public TreeSet<String> getWordLists() {
        return wordLists;
    }

    protected void complete(String s) {
        int end = -1;
        String text = getText().substring(0, getCaretPosition());
        for (int i = 0; i <= s.length(); i++) {
            if (text.matches(".*" + s.substring(0, i))) {
                end = i;
            }
        }
        int oldCaret = getCaretPosition();
        setText(text + s.substring(end, s.length()) + getText().substring(getCaretPosition(), getText().length()));
        setCaretPosition(oldCaret + s.length() - end);
        popupMenu.setVisible(false);
    }

    private class PopUpWindow extends JWindow {

        JList theList;
        DefaultListModel theModel;
        Point theRelativePosition;
        AutoCompleteTextField actf;

        public PopUpWindow(AutoCompleteTextField actf) {
            super(SRLGUIApp.getApplication().getMainFrame());
            theModel = new DefaultListModel();
            theRelativePosition = new Point(0, 0);
            loadUIElements();
            setEventManagement();
            this.actf = actf;
        }

        private void loadUIElements() {
            theList = new JList(theModel) {

                public int getVisibleRowCount() {
                    return Math.min(theModel.getSize(), 10);
                }
            };
            theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            theList.setBackground(new Color(235, 244, 254));
            theList.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    actf.requestFocus();
                }
                
            });
            JScrollPane scrollPane = new JScrollPane(theList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(LineBorder.createGrayLineBorder());
            setContentPane(scrollPane);
        }

        private void setEventManagement() {
            theList.addKeyListener(new KeyAdapter() {

                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        complete((String) theList.getSelectedValue());
                    }
                }
            });
        }

        public void setWords(SortedSet<String> someWords) {
            theModel.clear();
            if ((someWords == null) || (someWords.isEmpty())) {
                setVisible(false);
                return;
            }
            for (String word : someWords) {
                theModel.addElement(word);
            }
            theList.setSelectedIndex(0);
            pack();
            pack();
        }
    }

    public List<ActionListener> actionListeners = new LinkedList<ActionListener>();
    
    public void addActionListener(java.awt.event.ActionListener al) {
        actionListeners.add(al);
    }
    
    private class ACTFListener implements ActionListener {

        String s;

        ACTFListener(String s) {
            this.s = s;
        }

        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class AutoDoc extends PlainDocument {

        public void replace(int i, int j, String s, AttributeSet attributeSet)
                throws BadLocationException {
            super.replace(i, j, s, attributeSet);
            onTextChange();
        }

        public void insertString(int i, String s, AttributeSet attributeSet)
                throws BadLocationException {
            if (ignoreNextSpace && s.matches(" ")) {
                ignoreNextSpace = false;
                return;
            }
            if (ignoreNextEnter && s.matches("\n")) {
                ignoreNextEnter = false;
                return;
            } 
            if(s.matches("\n")) {
                for(ActionListener al : actionListeners) {
                    al.actionPerformed(new ActionEvent(this, 0, null));
                }
                return;
            }
            super.insertString(i, s, attributeSet);
            onTextChange();
        }

        public void remove(int i, int j)
                throws BadLocationException {
            super.remove(i, j);
            onTextChange();
        }
    }
}
