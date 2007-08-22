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

package org.apache.directory.studio.schemas.view.editors.objectClass;


import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.model.ObjectClass;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * This class is the Input class for the Object Class Editor
 */
public class ObjectClassEditorInput implements IEditorInput
{
    private ObjectClass objectClass = null;


    /**
     * Default constructor.
     * 
     * @param obj
     *      the object class
     */
    public ObjectClassEditorInput( ObjectClass obj )
    {
        super();
        this.objectClass = obj;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        return ( this.objectClass == null );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        return this.objectClass.getNames()[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return this.objectClass.getNames()[0]
            + Messages.getString( "ObjectClassEditorInput.In_the" ) + this.objectClass.getOriginatingSchema().getName() + Messages.getString( "ObjectClassEditorInput.Schema" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !( obj instanceof ObjectClassEditorInput ) )
            return false;
        ObjectClassEditorInput other = ( ObjectClassEditorInput ) obj;
        return other.getObjectClass().getOid().equals( this.objectClass.getOid() );
    }


    /**
     * Returns the input object class
     * 
     * @return
     *      the input object class
     */
    public ObjectClass getObjectClass()
    {
        return this.objectClass;
    }
}
