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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.Control;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.AliasBaseEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;


/**
 * Runnable to initialize the child entries of an entry
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class InitializeChildrenRunnable implements StudioBulkRunnableWithProgress
{

    /** The entries. */
    private IEntry[] entries;


    /**
     * Creates a new instance of InitializeChildrenRunnable.
     * 
     * @param entries the entries
     */
    public InitializeChildrenRunnable( IEntry[] entries )
    {
        this.entries = entries;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        Connection[] connections = new Connection[entries.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entries[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__init_entries_title_subonly;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.addAll( Arrays.asList( entries ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return entries.length == 1 ? BrowserCoreMessages.jobs__init_entries_error_1
            : BrowserCoreMessages.jobs__init_entries_error_n;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", entries.length + 2 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < entries.length && !monitor.isCanceled(); pi++ )
        {
            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
                { this.entries[pi].getDn().getUpName() } ) );
            monitor.worked( 1 );

            if ( entries[pi].getBrowserConnection() != null && entries[pi].isDirectoryEntry() )
            {
                initializeChildren( entries[pi], monitor );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification()
    {
        for ( int pi = 0; pi < entries.length; pi++ )
        {
            IEntry parent = entries[pi];
            if ( parent.getBrowserConnection() != null && parent.isDirectoryEntry() )
            {
                EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( parent ), this );
            }
        }
    }


    /**
     * Initializes the child entries.
     * 
     * @param parent the parent
     * @param monitor the progress monitor
     */
    public static void initializeChildren( IEntry parent, StudioProgressMonitor monitor )
    {
        if ( parent instanceof IRootDSE )
        {
            // special handling for Root DSE
            return;
        }
        else
        {
            monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_sub,
                new String[]
                    { parent.getDn().getUpName() } ) );

            // clear old children
            IEntry[] oldChildren = parent.getChildren();
            for ( int i = 0; oldChildren != null && i < oldChildren.length; i++ )
            {
                if ( oldChildren[i] != null )
                {
                    parent.deleteChild( oldChildren[i] );
                }
            }
            parent.setChildrenInitialized( false );

            // determine alias and referral handling
            SearchScope scope = SearchScope.ONELEVEL;
            AliasDereferencingMethod aliasesDereferencingMethod = parent.getBrowserConnection()
                .getAliasesDereferencingMethod();
            if ( parent.isAlias() )
            {
                aliasesDereferencingMethod = AliasDereferencingMethod.NEVER;
            }
            ReferralHandlingMethod referralsHandlingMethod = parent.getBrowserConnection().getReferralsHandlingMethod();
            if ( parent.isReferral() )
            {
                referralsHandlingMethod = ReferralHandlingMethod.MANAGE;
            }
            Control[] controls = null;

            // get children,
            ISearch search = new Search( null, parent.getBrowserConnection(), parent.getDn(), parent
                .getChildrenFilter(), ISearch.NO_ATTRIBUTES, scope, parent.getBrowserConnection().getCountLimit(),
                parent.getBrowserConnection().getTimeLimit(), aliasesDereferencingMethod, referralsHandlingMethod,
                BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
                    BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ), controls );
            SearchRunnable.searchAndUpdateModel( parent.getBrowserConnection(), search, monitor );
            ISearchResult[] srs = search.getSearchResults();
            monitor.reportProgress( BrowserCoreMessages
                .bind( BrowserCoreMessages.jobs__init_entries_progress_subcount,
                    new String[]
                        { srs == null ? Integer.toString( 0 ) : Integer.toString( srs.length ),
                            parent.getDn().getUpName() } ) );

            // fill children in search result
            if ( srs != null && srs.length > 0 )
            {
                // clearing old children before filling new children is
                // necessary to handle aliases and referrals.
                IEntry[] connChildren = parent.getChildren();
                for ( int i = 0; connChildren != null && i < connChildren.length; i++ )
                {
                    if ( connChildren[i] != null )
                    {
                        parent.deleteChild( connChildren[i] );
                    }
                }
                parent.setChildrenInitialized( false );

                for ( int i = 0; srs != null && i < srs.length; i++ )
                {
                    if ( parent.isAlias() && !( srs[i].getEntry() instanceof AliasBaseEntry ) )
                    {
                        AliasBaseEntry aliasBaseEntry = new AliasBaseEntry( srs[i].getEntry().getBrowserConnection(),
                            srs[i].getEntry().getDn() );
                        parent.addChild( aliasBaseEntry );
                    }
                    else
                    {
                        parent.addChild( srs[i].getEntry() );
                    }
                }
            }
            else
            {
                parent.setHasChildrenHint( false );
            }

            // get sub-entries
            ISearch subSearch = new Search( null, parent.getBrowserConnection(), parent.getDn(), parent
                .getChildrenFilter() != null ? parent.getChildrenFilter() : ISearch.FILTER_SUBENTRY,
                ISearch.NO_ATTRIBUTES, scope, parent.getBrowserConnection().getCountLimit(), parent
                    .getBrowserConnection().getTimeLimit(), aliasesDereferencingMethod, referralsHandlingMethod,
                BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
                    BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ), new Control[]
                    { Control.SUBENTRIES_CONTROL } );
            if ( BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
                BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES ) )
            {
                SearchRunnable.searchAndUpdateModel( parent.getBrowserConnection(), subSearch, monitor );
                ISearchResult[] subSrs = subSearch.getSearchResults();
                monitor.reportProgress( BrowserCoreMessages.bind(
                    BrowserCoreMessages.jobs__init_entries_progress_subcount, new String[]
                        { subSrs == null ? Integer.toString( 0 ) : Integer.toString( subSrs.length ),
                            parent.getDn().getUpName() } ) );

                // fill children in search result
                if ( subSrs != null && subSrs.length > 0 )
                {

                    for ( int i = 0; subSrs != null && i < subSrs.length; i++ )
                    {
                        parent.addChild( subSrs[i].getEntry() );
                    }
                }
            }

            // check exceeded limits / canceled
            parent.setHasMoreChildren( search.isCountLimitExceeded() || subSearch.isCountLimitExceeded()
                || monitor.isCanceled() );

            // set initialized state
            parent.setChildrenInitialized( true );
        }
    }

}