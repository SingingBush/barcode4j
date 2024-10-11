/*
 * Copyright 2005 Jeremias Maerki and Dietmar Bürkle.
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
package org.krysalis.barcode4j.impl.code128;

import org.jetbrains.annotations.NotNull;

/**
 * @since 2.0
 */
class CheckDigit {

    public final static byte CDNone = 0;
    public final static byte CD31 = 1;
    public final static byte CD11 = 2;

    /*
     * @param msg the full barcode message
     * @param start
     * @param end
     * @param type one of either {@link CheckDigit#CD31} or {@link CheckDigit#CD11}
     * @return The check digit value as a char
     */
    // todo: consider removing this class. It's only used by EAN128LogicImpl and could be a private method. There's no need for it to be publicly available
    static char calcCheckdigit(@NotNull final String msg, int start, int end, byte type) {
        switch (type) {
            case CD31 : return calcCheckdigit(3, 1, msg, start, end);
            case CD11 : return calcCheckdigit(1, 1, msg, start, end);
            default : return '0';
        }
    }

    private static char calcCheckdigit(int oddMult, int evenMult, String msg, int start, int end) {
        int oddSum = 0;
        int evenSum = 0;
        boolean even = false;
        for (int i = end - 1; i >= start; i--) {
            if (even) {
                evenSum += Character.digit(msg.charAt(i), 10);
            } else {
                oddSum += Character.digit(msg.charAt(i), 10);
            }
            even = !even;
        }
        int check = 10 - ((evenMult * evenSum + oddMult * oddSum) % 10);
        if (check >= 10) check = 0;
        return Character.forDigit(check, 10);
    }

}
