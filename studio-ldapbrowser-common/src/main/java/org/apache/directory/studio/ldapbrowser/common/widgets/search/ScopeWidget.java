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

package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * The ScopeWidget could be used to select the scope of a search. 
 * It is composed of a group with radio buttons.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ScopeWidget extends BrowserWidget
{

    /** The initial scope. */
    private int initialScope;

    /** The scope group. */
    private Group scopeGroup;

    /** The scope object button. */
    private Button scopeObjectButton;

    /** The scope onelevel button. */
    private Button scopeOnelevelButton;

    /** The scope subtree button. */
    private Button scopeSubtreeButton;


    /**
     * Creates a new instance of ScopeWidget with the given
     * initial scope. That must be one of {@link ISearch#SCOPE_OBJECT},
     * {@link ISearch#SCOPE_ONELEVEL} or {@link ISearch#SCOPE_SUBTREE}.
     * 
     * @param initialScope the initial scope
     */
    public ScopeWidget( int initialScope )
    {
        this.initialScope = initialScope;
    }


    /**
     * Creates a new instance of ScopeWidget with initial scope
     * {@link ISearch#SCOPE_OBJECT}.
     */
    public ScopeWidget()
    {
        this.initialScope = 0;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {

        // Scope group
        scopeGroup = new Group( parent, SWT.NONE );
        scopeGroup.setText( "Scope" );
        scopeGroup.setLayout( new GridLayout( 1, false ) );
        scopeGroup.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // Object radio
        scopeObjectButton = new Button( scopeGroup, SWT.RADIO );
        scopeObjectButton.setText( "&Object" );
        scopeObjectButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        // Onelevel radio
        scopeOnelevelButton = new Button( scopeGroup, SWT.RADIO );
        scopeOnelevelButton.setText( "One &Level" );
        scopeOnelevelButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        // subtree button
        scopeSubtreeButton = new Button( scopeGroup, SWT.RADIO );
        scopeSubtreeButton.setText( "&Subtree" );
        scopeSubtreeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        setScope( initialScope );
    }


    /**
     * Sets the scope, must be one of {@link ISearch#SCOPE_OBJECT},
     * {@link ISearch#SCOPE_ONELEVEL} or {@link ISearch#SCOPE_SUBTREE}.
     * 
     * @param scope the scope
     */
    public void setScope( int scope )
    {
        initialScope = scope;
        scopeObjectButton.setSelection( initialScope == ISearch.SCOPE_OBJECT );
        scopeOnelevelButton.setSelection( initialScope == ISearch.SCOPE_ONELEVEL );
        scopeSubtreeButton.setSelection( initialScope == ISearch.SCOPE_SUBTREE );
    }


    /**
     * Gets the scope, one of {@link ISearch#SCOPE_OBJECT},
     * {@link ISearch#SCOPE_ONELEVEL} or {@link ISearch#SCOPE_SUBTREE}.
     * 
     * @return the scope
     */
    public int getScope()
    {
        int scope;

        if ( scopeSubtreeButton.getSelection() )
        {
            scope = ISearch.SCOPE_SUBTREE;
        }
        else if ( scopeOnelevelButton.getSelection() )
        {
            scope = ISearch.SCOPE_ONELEVEL;
        }
        else if ( scopeObjectButton.getSelection() )
        {
            scope = ISearch.SCOPE_OBJECT;
        }
        else
        {
            scope = ISearch.SCOPE_ONELEVEL;
        }

        return scope;
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        scopeGroup.setEnabled( b );
        scopeObjectButton.setEnabled( b );
        scopeOnelevelButton.setEnabled( b );
        scopeSubtreeButton.setEnabled( b );
    }

}
