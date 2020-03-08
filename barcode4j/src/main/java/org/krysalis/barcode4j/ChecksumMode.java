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
 * Enumeration type for checksum policy.
 * 
 * @author Jeremias Maerki
 * @version $Id: ChecksumMode.java,v 1.3 2004-10-02 14:53:22 jmaerki Exp $
 */
public class ChecksumMode {

    /** "auto" chooses the barcode's default checksum behaviour */
    public static final ChecksumMode CP_AUTO   = new ChecksumMode("auto");
    /** "ignore" doesn't check nor add a checksum */
    public static final ChecksumMode CP_IGNORE = new ChecksumMode("ignore");
    /** "add" adds the necessary checksum to the message to be encoded */
    public static final ChecksumMode CP_ADD    = new ChecksumMode("add");
    /** "check" requires the check character to be present in the message. It 
     * will be checked.
     */
    public static final ChecksumMode CP_CHECK  = new ChecksumMode("check");

    private String name;
    
    /**
     * Creates a new ChecksumMode instance.
     * @param name the name of the ChecksumMode
     */
    protected ChecksumMode(String name) {
        this.name = name;
    }
    
    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns a ChecksumMode instance by name.
     * @param name the name of the ChecksumMode
     * @return the requested instance
     */
    public static ChecksumMode byName(String name) {
        if (name.equalsIgnoreCase(ChecksumMode.CP_AUTO.getName())) {
            return ChecksumMode.CP_AUTO;
        } else if (name.equalsIgnoreCase(ChecksumMode.CP_IGNORE.getName())) {
            return ChecksumMode.CP_IGNORE;
        } else if (name.equalsIgnoreCase(ChecksumMode.CP_ADD.getName())) {
            return ChecksumMode.CP_ADD;
        } else if (name.equalsIgnoreCase(ChecksumMode.CP_CHECK.getName())) {
            return ChecksumMode.CP_CHECK;
        } else {
            throw new IllegalArgumentException("Invalid ChecksumMode: " + name);
        }
    }
    
}
