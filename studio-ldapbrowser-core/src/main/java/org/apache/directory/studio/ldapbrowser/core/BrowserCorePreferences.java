/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.ldapbrowser.core;


import java.util.HashSet;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.model.schema.BinaryAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinarySyntax;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.core.runtime.Preferences;


/**
 * This class is used to manage and access the preferences of the Browser Core Plugin
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserCorePreferences
{
    private Set<String> binaryAttributeCache;

    private Set<String> binarySyntaxCache;


    /**
     * Gets the oids and names of the binary attributes
     *
     * @return
     *      the oids and names of the binary attributes
     */
    public Set getBinaryAttributeOidsAndNames()
    {
        if ( binaryAttributeCache == null )
        {
            binaryAttributeCache = new HashSet<String>();
            BinaryAttribute[] binaryAttributes =getBinaryAttributes();
            for ( int i = 0; i < binaryAttributes.length; i++ )
            {
                if ( binaryAttributes[i].getAttributeNumericOidOrName() != null )
                {
                    binaryAttributeCache.add( binaryAttributes[i].getAttributeNumericOidOrName() );
                }
            }
        }
        return binaryAttributeCache;
    }


    /**
     * Gets an array containing the binary attributes
     * 
     * @return
     *      an array containing the binary attributes
     */
    public BinaryAttribute[] getBinaryAttributes()
    {
        BinaryAttribute[] binaryAttributes = ( BinaryAttribute[] ) load( BrowserCoreConstants.PREFERENCE_BINARY_ATTRIBUTES );
        return binaryAttributes;
    }


    /**
     * Sets the binary attributes
     *
     * @param binaryAttributes
     *      the binary attributes to set
     */
    public void setBinaryAttributes( BinaryAttribute[] binaryAttributes )
    {
        store( BrowserCoreConstants.PREFERENCE_BINARY_ATTRIBUTES, binaryAttributes );
        binaryAttributeCache = null;
    }


    /**
     * Gets the default binary attributes
     *
     * @return
     *      the default binary attributes
     */
    public BinaryAttribute[] getDefaultBinaryAttributes()
    {
        BinaryAttribute[] binaryAttributes = ( BinaryAttribute[] ) loadDefault( BrowserCoreConstants.PREFERENCE_BINARY_ATTRIBUTES );
        return binaryAttributes;
    }


    /**
     * Sets the default binary attributes
     *
     * @param defaultBinaryAttributes
     *      the default binary attributes to set
     */
    public void setDefaultBinaryAttributes( BinaryAttribute[] defaultBinaryAttributes )
    {
        storeDefault( BrowserCoreConstants.PREFERENCE_BINARY_ATTRIBUTES, defaultBinaryAttributes );
    }


    /**
     * Gets the binary syntax oids.
     * 
     * @return the binary syntax oids
     */
    public Set<String> getBinarySyntaxOids()
    {
        if ( binarySyntaxCache == null )
        {
            binarySyntaxCache = new HashSet<String>();
            BinarySyntax[] binarySyntaxes = getBinarySyntaxes();
            for ( int i = 0; i < binarySyntaxes.length; i++ )
            {
                if ( binarySyntaxes[i].getSyntaxNumericOid() != null )
                {
                    binarySyntaxCache.add( binarySyntaxes[i].getSyntaxNumericOid() );
                }
            }
        }
        return binarySyntaxCache;
    }


    /**
     * Gets the binary syntaxes
     *
     * @return
     *      the binary syntaxes
     */
    public BinarySyntax[] getBinarySyntaxes()
    {
        BinarySyntax[] binarySyntaxes = ( BinarySyntax[] ) load( BrowserCoreConstants.PREFERENCE_BINARY_SYNTAXES );
        return binarySyntaxes;
    }


    /**
     * Sets the binary syntaxes
     *
     * @param binarySyntaxes
     *      the binary syntaxes to set
     */
    public void setBinarySyntaxes( BinarySyntax[] binarySyntaxes )
    {
        store( BrowserCoreConstants.PREFERENCE_BINARY_SYNTAXES, binarySyntaxes );
        binarySyntaxCache = null;
    }


    /**
     * Gets the default binary syntaxes
     *
     * @return
     *      the default binary syntaxes
     */
    public BinarySyntax[] getDefaultBinarySyntaxes()
    {
        BinarySyntax[] binarySyntaxes = ( BinarySyntax[] ) loadDefault( BrowserCoreConstants.PREFERENCE_BINARY_SYNTAXES );
        return binarySyntaxes;
    }


    /**
     * Sets the default binary syntaxes
     *
     * @param defaultBinarySyntaxes
     *      the default binary syntaxes to set
     */
    public void setDefaultBinarySyntaxes( BinarySyntax[] defaultBinarySyntaxes )
    {
        storeDefault( BrowserCoreConstants.PREFERENCE_BINARY_SYNTAXES, defaultBinarySyntaxes );
    }


    /**
     * Loads the current value of the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @return
     *      the corresponding object
     */
    private static Object load( String key )
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        String s = store.getString( key );
        return Utils.deserialize( s );
    }


    /**
     * Stores the current value of the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @param o
     *      the new current value of the property
     */
    private static void store( String key, Object o )
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        String s = Utils.serialize( o );
        store.setValue( key, s );
        BrowserCorePlugin.getDefault().savePluginPreferences();
    }


    /**
     * Loads the default value for the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @return
     *      the default value of the named property
     */
    private static Object loadDefault( String key )
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        String s = store.getDefaultString( key );
        return Utils.deserialize( s );
    }


    /**
     * Stores the default value for the string-valued property with the given name.
     *
     * @param key
     *      the name of the property
     * @param o
     *      the new default value for the property
     */
    private static void storeDefault( String key, Object o )
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        String s = Utils.serialize( o );
        store.setDefault( key, s );
        BrowserCorePlugin.getDefault().savePluginPreferences();
    }
}
