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

package org.apache.directory.studio.ldifparser.model.lines;


import org.apache.directory.studio.ldifparser.LdifParserConstants;


public class LdifChangeTypeLine extends LdifValueLineBase
{

    private static final long serialVersionUID = 8613980677301250589L;


    protected LdifChangeTypeLine()
    {
    }


    public LdifChangeTypeLine( int offset, String rawChangeTypeSpec, String rawValueType, String rawChangeType,
        String rawNewLine )
    {
        super( offset, rawChangeTypeSpec, rawValueType, rawChangeType, rawNewLine );
    }


    public String getRawChangeTypeSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedChangeTypeSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawChangeType()
    {
        return super.getRawValue();
    }


    public String getUnfoldedChangeType()
    {
        return super.getUnfoldedValue();
    }


    public String toRawString()
    {
        return super.toRawString();
    }


    public boolean isAdd()
    {
        return this.getUnfoldedChangeType().equals( "add" ); //$NON-NLS-1$
    }


    public boolean isDelete()
    {
        return this.getUnfoldedChangeType().equals( "delete" ); //$NON-NLS-1$
    }


    public boolean isModify()
    {
        return this.getUnfoldedChangeType().equals( "modify" ); //$NON-NLS-1$
    }


    public boolean isModDn()
    {
        return this.getUnfoldedChangeType().equals( "moddn" ) || this.getUnfoldedChangeType().equals( "modrdn" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    public boolean isValid()
    {
        return super.isValid();
    }


    public String getInvalidString()
    {
        if ( this.getUnfoldedChangeTypeSpec().length() == 0 )
        {
            return "Missing spec 'changetype'";
        }
        else if ( this.getUnfoldedChangeType().length() == 0 )
        {
            return "Missing changetype";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifChangeTypeLine createDelete()
    {
        return new LdifChangeTypeLine( 0, "changetype", ":", "delete", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }


    public static LdifChangeTypeLine createAdd()
    {
        return new LdifChangeTypeLine( 0, "changetype", ":", "add", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }


    public static LdifChangeTypeLine createModify()
    {
        return new LdifChangeTypeLine( 0, "changetype", ":", "modify", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }


    public static LdifChangeTypeLine createModDn()
    {
        return new LdifChangeTypeLine( 0, "changetype", ":", "moddn", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }


    public static LdifChangeTypeLine createModRdn()
    {
        return new LdifChangeTypeLine( 0, "changetype", ":", "modrdn", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
