/*
 * Copyright 2007 Dimitar Vlasev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: MessagePatternUtil.java,v 1.3 2008-11-29 16:26:52 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

/**
 * Helper class to apply custom message pattern (i.e. message characters grouping) to barcode
 * messages.
 * @author Dimitar Vlasev
 * @version $Id: MessagePatternUtil.java,v 1.3 2008-11-29 16:26:52 jmaerki Exp $
 */
public class MessagePatternUtil {

    /**
     * Defined means to apply custom message pattern (i.e. message characters grouping).
     * If either the message or the pattern are null or empty the "msg" pattern will simply be
     * returned.
     * <p>
     * Example: "\_patterned\_:__/__/____" (Any '_' is placeholder for the next message symbol,
     * all other pattern symbols will be inserted between. The '\' is escape char. If the patterned
     * message is too long you can increase the quite zone length to make it visible)
     * @param msg the original message
     * @param pattern the message pattern to be applied
     * @return the formatted message
     * @author Dimitar Vlasev
     */
    public static String applyCustomMessagePattern(String msg, String pattern) {

        StringBuffer result = new StringBuffer();

        // if there is no pattern then return the original message
        if ((pattern == null) || "".equals(pattern)
                || msg == null || "".equals(msg)) {
            return msg;
        }

        byte[] msgBytes, patternBytes;
        msgBytes = msg.getBytes();
        patternBytes = pattern.getBytes();

        int msgIndex = 0;
        char currentPatternChar;
        boolean escapeCharEncountered = false;

        // iterate trough pattern chars
        boolean msgFinished = false;
        for (int patternIndex = 0; patternIndex < patternBytes.length; patternIndex++) {

            currentPatternChar = (char) patternBytes[patternIndex];

            // if the currentPatternChar is escape character and the
            // escapeCharEncountered flag is down
            // set the escapeCharEncountered flag up and continue to the next
            // pattern symbol
            if ((!escapeCharEncountered) && isEscapeChar(currentPatternChar)) {
                escapeCharEncountered = true;
                continue;
            }

            // if the currentPatternChar is a placeholder and the
            // escapeCharEncountered flag is down
            // append the next message char to the result
            // else
            // append the currentPatternChar to the result and set the
            // escapeCharEncountered flag down
            if ((!msgFinished)
                    && (!escapeCharEncountered)
                    && (isPlaceholder(currentPatternChar) || isDeleteholder(currentPatternChar))) {
                if (!isDeleteholder(currentPatternChar)) {
                    result.append((char) msgBytes[msgIndex]);
                }
                msgIndex++;
                if (msgIndex == msgBytes.length) {
                    msgFinished = true;
                }
            } else {
                if (escapeCharEncountered || !isPlaceholder(currentPatternChar)) {
                    result.append(currentPatternChar);
                }
                escapeCharEncountered = false;
            }
        }

        for (; msgIndex < msgBytes.length; msgIndex++) {
            result.append((char) msgBytes[msgIndex]);
        }

        return result.toString();
    }

    /**
     * Returns true if the imput character is placeholder
     * @param c byte
     * @return boolean
     */
    private static boolean isPlaceholder(char c) {
      boolean result = false;

      char placeholderChar = '_';

      result = (placeholderChar == c);

      return result;
    }

    /**
     * Returns true if the input parameter is escape character
     * @param c char
     * @return boolean
     * @author Dimitar Vlasev
     */
    private static boolean isEscapeChar(char c) {
        boolean result = false;

        char escapeChar = '\\';

        result = (c == escapeChar);

        return result;
    }

    /**
     * Returns true if the input character is deleteholder.
     * @param c the input character
     * @return true if the input character is a position to be deleted
     */
    private static boolean isDeleteholder(char c) {
        boolean result = false;
        char placeholderChar = '#';
        result = (placeholderChar == c);
        return result;
    }

}
