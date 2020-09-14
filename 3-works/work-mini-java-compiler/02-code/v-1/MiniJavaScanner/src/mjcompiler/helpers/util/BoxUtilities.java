/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mjcompiler.helpers.util;

import java.awt.Color;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author carvalho
 */
public class BoxUtilities {
    public static final String WORDS_RESERVERS = "boolean|class|else|extends|false|if|int|length|main|new|public|return|static|String|System.out.println|this|true|void|while";
    public static int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    public static int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }
    // Printar em cores diferentes as palavras reservadas
    public static Document getDocumentKeyReserves(){
        
        // final StyleContext cont = StyleContext.getDefaultStyleContext();
        final StyleContext cont =  StyleContext.getDefaultStyleContext();
        final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
        final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
        
        DefaultStyledDocument doc = new DefaultStyledDocument() {
            @Override
            public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = BoxUtilities.findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = BoxUtilities.findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches("(\\W)*(" + WORDS_RESERVERS + ")"))
                            setCharacterAttributes(wordL, wordR - wordL, attr, false);
                        else
                            setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                        wordL = wordR;
                    }
                    wordR++;
                }
            }
            @Override
            public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = BoxUtilities.findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = BoxUtilities.findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches("(\\W)*(" + WORDS_RESERVERS + ")")) {
                    setCharacterAttributes(before, after - before, attr, false);
                } else {
                    setCharacterAttributes(before, after - before, attrBlack, false);
                }
            }
        };
        
        return doc;
    }
}
