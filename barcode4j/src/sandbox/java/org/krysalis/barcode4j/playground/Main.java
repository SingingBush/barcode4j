/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.playground;

/**
 * @version $Id: Main.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
 */
public class Main {

    public static void main(String[] args) {
        try {
            PlaygroundFrame mainframe = new PlaygroundFrame();
            mainframe.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
