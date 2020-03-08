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
package org.krysalis.barcode4j;

/**
 * This is the basic interface for logic handlers. This interface usually gets 
 * implemented by classes that want to render a barcode in a specific output
 * format. Due to different barcode types (1D, 2D) there are different 
 * descendants of this interface that define the specifics. See this 
 * interface's descendants for more information.
 * <br/>
 * The purpose of this interface is to enable the separatation of barcode logic
 * and painting/rendering logic.
 *
 * @author Jeremias Maerki
 * @version $Id: BarcodeLogicHandler.java,v 1.3 2004-10-24 11:45:54 jmaerki Exp $
 */
public interface BarcodeLogicHandler {

    /**
     * This is always the first method called. It is called to inform the
     * logic handler that a new barcode is about to be painted.
     * @param msg full message to be encoded
     * @param formattedMsg message as it is to be presented in the 
     *      human-readable part
     */
    void startBarcode(String msg, String formattedMsg);
    
    /**
     * This is always the last method called. It is called to inform the 
     * logic handler that the generation of barcode logic has stopped.
     */
    void endBarcode();
    
}
