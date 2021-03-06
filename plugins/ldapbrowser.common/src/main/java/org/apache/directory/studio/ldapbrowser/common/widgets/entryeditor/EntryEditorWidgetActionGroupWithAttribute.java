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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.actions.DeleteAllValuesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.NewAttributeAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.studio.utils.ActionUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * This class manages all the actions of the attribute page of the new entry wizard.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorWidgetActionGroupWithAttribute extends EntryEditorWidgetActionGroup
{

    /** The Constant editAttributeDescriptionAction. */
    private static final String editAttributeDescriptionAction = "editAttributeDescriptionAction"; //$NON-NLS-1$

    /** The Constant newAttributeAction. */
    private static final String newAttributeAction = "newAttributeAction"; //$NON-NLS-1$

    /** The Constant deleteAllValuesAction. */
    private static final String deleteAllValuesAction = "deleteAllValuesAction"; //$NON-NLS-1$


    /**
     * 
     * Creates a new instance of NewEntryAttributesWizardPageActionGroup.
     *
     * @param mainWidget
     * @param configuration
     */
    public EntryEditorWidgetActionGroupWithAttribute( EntryEditorWidget mainWidget,
        EntryEditorWidgetConfiguration configuration )
    {
        super( mainWidget, configuration );
        TreeViewer viewer = mainWidget.getViewer();

        entryEditorActionMap.put( editAttributeDescriptionAction, new EntryEditorActionProxy( viewer,
            new EditAttributeDescriptionAction( viewer ) ) );
        entryEditorActionMap.put( newAttributeAction, new EntryEditorActionProxy( viewer, new NewAttributeAction() ) );
        entryEditorActionMap.put( deleteAllValuesAction, new EntryEditorActionProxy( viewer,
            new DeleteAllValuesAction() ) );
    }


    /**
     * {@inheritDoc}
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( ( IAction ) entryEditorActionMap.get( newValueAction ) );
        toolBarManager.add( ( IAction ) entryEditorActionMap.get( newAttributeAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAction ) );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( deleteAllValuesAction ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.showQuickFilterAction );
        toolBarManager.update( true );
    }


    /**
     * {@inheritDoc}
     */
    protected void contextMenuAboutToShow( IMenuManager menuManager )
    {
        // new
        menuManager.add( ( IAction ) entryEditorActionMap.get( newAttributeAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( newValueAction ) );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) entryEditorActionMap.get( copyAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( pasteAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( deleteAction ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( selectAllAction ) );
        MenuManager copyMenuManager = new MenuManager( Messages
            .getString( "EntryEditorWidgetActionGroupWithAttribute.Advanced" ) ); //$NON-NLS-1$
        copyMenuManager.add( ( IAction ) entryEditorActionMap.get( deleteAllValuesAction ) );
        menuManager.add( copyMenuManager );
        menuManager.add( new Separator() );

        // edit
        menuManager.add( ( IAction ) entryEditorActionMap.get( editAttributeDescriptionAction ) );
        super.addEditMenu( menuManager );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) entryEditorActionMap.get( propertyDialogAction ) );
    }


    /**
     * {@inheritDoc}
     */
    public void activateGlobalActionHandlers()
    {
        super.activateGlobalActionHandlers();

        IAction naa = ( IAction ) entryEditorActionMap.get( newAttributeAction );
        ActionUtils.activateActionHandler( naa );
        IAction eada = ( IAction ) entryEditorActionMap.get( editAttributeDescriptionAction );
        ActionUtils.activateActionHandler( eada );
    }


    /**
     * {@inheritDoc}
     */
    public void deactivateGlobalActionHandlers()
    {
        super.deactivateGlobalActionHandlers();

        IAction naa = ( IAction ) entryEditorActionMap.get( newAttributeAction );
        ActionUtils.deactivateActionHandler( naa );
        IAction eada = ( IAction ) entryEditorActionMap.get( editAttributeDescriptionAction );
        ActionUtils.deactivateActionHandler( eada );
    }

}
