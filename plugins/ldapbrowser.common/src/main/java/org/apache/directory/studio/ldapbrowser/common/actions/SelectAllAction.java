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

package org.apache.directory.studio.ldapbrowser.common.actions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This class implements the Select All Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SelectAllAction extends BrowserAction
{
    private Viewer viewer;


    /**
     * Creates a new instance of SelectAllAction.
     *
     * @param viewer
     *      the attached viewer
     */
    public SelectAllAction( Viewer viewer )
    {
        this.viewer = viewer;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "SelectAllAction.SelectAll" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.SELECT_ALL;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( getInput() != null && getInput() instanceof IEntry )
        {
            List selectionList = new ArrayList();
            IAttribute[] attributes = ( ( IEntry ) getInput() ).getAttributes();
            if ( attributes != null )
            {
                selectionList.addAll( Arrays.asList( attributes ) );
                for ( int i = 0; i < attributes.length; i++ )
                {
                    selectionList.addAll( Arrays.asList( attributes[i].getValues() ) );
                }
            }
            StructuredSelection selection = new StructuredSelection( selectionList );
            this.viewer.setSelection( selection );
        }
        else if ( getInput() != null && getInput() instanceof ConnectionManager )
        {
            StructuredSelection selection = new StructuredSelection( ( ( ConnectionManager ) getInput() )
                .getConnections() );
            this.viewer.setSelection( selection );
        }
        else if ( getSelectedConnections().length > 0 && viewer.getInput() instanceof ConnectionManager )
        {
            StructuredSelection selection = new StructuredSelection( ( ( ConnectionManager ) viewer.getInput() )
                .getConnections() );
            this.viewer.setSelection( selection );
        }
    }
}
