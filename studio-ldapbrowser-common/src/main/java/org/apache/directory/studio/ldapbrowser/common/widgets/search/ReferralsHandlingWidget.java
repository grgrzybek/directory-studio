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


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * The ReferralsHandlingWidget could be used to select the
 * referrals handling method. It is composed of a group with 
 * two radio buttons.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReferralsHandlingWidget extends BrowserWidget
{

    /** The initial referrals handling method. */
    private int initialReferralsHandlingMethod;

    /** The group. */
    private Group group;

    /** The ignore button. */
    private Button ignoreButton;

    /** The follow button. */
    private Button followButton;


    /**
     * Creates a new instance of ReferralsHandlingWidget with the given
     * referrals handling method. This must be one of
     * {@link IBrowserConnection#HANDLE_REFERRALS_IGNORE} or
     * {@link IBrowserConnection#HANDLE_REFERRALS_FOLLOW}.  
     * 
     * @param initialReferralsHandlingMethod the initial referrals handling method
     */
    public ReferralsHandlingWidget( int initialReferralsHandlingMethod )
    {
        this.initialReferralsHandlingMethod = initialReferralsHandlingMethod;
    }


    /**
     * Creates a new instance of ReferralsHandlingWidget with initial 
     * referrals handling method {@link IBrowserConnection#HANDLE_REFERRALS_IGNORE}.
     */
    public ReferralsHandlingWidget()
    {
        this.initialReferralsHandlingMethod = IBrowserConnection.HANDLE_REFERRALS_IGNORE;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {

        group = BaseWidgetUtils.createGroup( parent, "Referrals Handling", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        ignoreButton = BaseWidgetUtils.createRadiobutton( groupComposite, "Ignore", 1 );
        ignoreButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        followButton = BaseWidgetUtils.createRadiobutton( groupComposite, "Follow", 1 );
        followButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        setReferralsHandlingMethod( initialReferralsHandlingMethod );
    }


    /**
     * Sets the referrals handling method must be one of
     *  {@link IBrowserConnection#HANDLE_REFERRALS_IGNORE} or
     * {@link IBrowserConnection#HANDLE_REFERRALS_FOLLOW}. 
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        initialReferralsHandlingMethod = referralsHandlingMethod;
        ignoreButton.setSelection( initialReferralsHandlingMethod == IBrowserConnection.HANDLE_REFERRALS_IGNORE );
        followButton.setSelection( initialReferralsHandlingMethod == IBrowserConnection.HANDLE_REFERRALS_FOLLOW );
    }


    /**
     * Gets the referrals handling method, one of
     * {@link IBrowserConnection#HANDLE_REFERRALS_IGNORE} or
     * {@link IBrowserConnection#HANDLE_REFERRALS_FOLLOW}.
     * 
     * @return the referrals handling method
     */
    public int getReferralsHandlingMethod()
    {
        if ( ignoreButton.getSelection() )
        {
            return IBrowserConnection.HANDLE_REFERRALS_IGNORE;
        }
        else
        {
            return IBrowserConnection.HANDLE_REFERRALS_FOLLOW;
        }
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        group.setEnabled( b );
        ignoreButton.setEnabled( b );
        followButton.setEnabled( b );
    }

}
