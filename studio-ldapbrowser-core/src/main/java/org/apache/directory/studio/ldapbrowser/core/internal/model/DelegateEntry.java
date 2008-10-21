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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;
import org.apache.directory.studio.ldapbrowser.core.model.URL;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Subschema;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class DelegateEntry implements IEntry, EntryUpdateListener
{

    private static final long serialVersionUID = -4488685394817691963L;

    // private IConnection connection;
    private String connectionId;

    private DN dn;

    private boolean entryDoesNotExist;

    private IEntry delegate;


    protected DelegateEntry()
    {
    }


    public DelegateEntry( IBrowserConnection connection, DN dn )
    {
        this.connectionId = connection.getConnection().getId();
        this.dn = dn;
        this.entryDoesNotExist = false;
        this.delegate = null;
        EventRegistry.addEntryUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
    }


    protected IEntry getDelegate()
    {
        if ( this.delegate != null && !this.delegate.getBrowserConnection().getConnection().getJNDIConnectionWrapper().isConnected() )
        {
            this.entryDoesNotExist = false;
            this.delegate = null;
        }
        return delegate;
    }


    protected void setDelegate( IEntry delegate )
    {
        this.delegate = delegate;
    }


    public IBrowserConnection getBrowserConnection()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getBrowserConnection();
        else
            return BrowserCorePlugin.getDefault().getConnectionManager().getBrowserConnectionById( this.connectionId );
        // return connection;
    }


    public DN getDn()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getDn();
        else
            return dn;
    }


    public URL getUrl()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getUrl();
        else
            return new URL( getBrowserConnection(), getDn() );
    }


    public boolean isAttributesInitialized()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isAttributesInitialized();
        else if ( this.entryDoesNotExist )
            return true;
        else
            return false;
    }


    public boolean isChildrenInitialized()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isChildrenInitialized();
        else if ( this.entryDoesNotExist )
            return true;
        else
            return false;
    }


    public boolean isDirectoryEntry()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isDirectoryEntry();
        else
            return true;
    }


    public void addAttribute( IAttribute attributeToAdd ) throws ModelModificationException
    {
        if ( this.getDelegate() != null )
            getDelegate().addAttribute( attributeToAdd );
    }


    public void addChild( IEntry childrenToAdd )
    {
        if ( this.getDelegate() != null )
            getDelegate().addChild( childrenToAdd );
    }


    public void deleteAttribute( IAttribute attributeToDelete ) throws ModelModificationException
    {
        if ( this.getDelegate() != null )
            getDelegate().deleteAttribute( attributeToDelete );
    }


    public void deleteChild( IEntry childrenToDelete )
    {
        if ( this.getDelegate() != null )
            getDelegate().deleteChild( childrenToDelete );
    }


    public IAttribute getAttribute( String attributeDescription )
    {
        if ( this.getDelegate() != null )
            return getDelegate().getAttribute( attributeDescription );
        else
            return null;
    }


    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {
        if ( this.getDelegate() != null )
            return getDelegate().getAttributeWithSubtypes( attributeDescription );
        else
            return null;
    }


    public IAttribute[] getAttributes()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getAttributes();
        else
            return null;
    }


    public IEntry getParententry()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getParententry();
        else
            return null;
    }


    public RDN getRdn()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getRdn();
        else
            return this.dn.getRdn();
    }


    public IEntry[] getChildren()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getChildren();
        else
            return null;
    }


    public int getChildrenCount()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getChildrenCount();
        else
            return -1;
    }


    public String getChildrenFilter()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getChildrenFilter();
        else
            return null;
    }


    public Subschema getSubschema()
    {
        if ( this.getDelegate() != null )
            return getDelegate().getSubschema();
        else
            return null;
    }


    public boolean hasMoreChildren()
    {
        if ( this.getDelegate() != null )
            return getDelegate().hasMoreChildren();
        else
            return false;
    }


    public boolean hasParententry()
    {
        if ( this.getDelegate() != null )
            return getDelegate().hasParententry();
        else
            return false;
    }


    public boolean hasChildren()
    {
        if ( this.getDelegate() != null )
            return getDelegate().hasChildren();
        else
            return true;
    }


    public boolean isConsistent()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isConsistent();
        else
            return true;
    }


    public void setAttributesInitialized( boolean b )
    {

        if ( !b )
        {
            if ( this.getDelegate() != null )
            {
                getDelegate().setAttributesInitialized( b );
            }
            setDelegate( null );
            this.entryDoesNotExist = false;
        }
        else
        /* if(b) */{
            if ( this.getDelegate() == null )
            {
                setDelegate( this.getBrowserConnection().getEntryFromCache( this.dn ) );
                if ( this.getDelegate() == null )
                {
                    // entry doesn't exist!
                    this.entryDoesNotExist = true;
                }
            }
            if ( this.getDelegate() != null )
            {
                getDelegate().setAttributesInitialized( b );
            }
        }
    }


    public void setDirectoryEntry( boolean b )
    {
        if ( this.getDelegate() != null )
            getDelegate().setDirectoryEntry( b );
    }


    public void setHasMoreChildren( boolean b )
    {
        if ( this.getDelegate() != null )
            getDelegate().setHasMoreChildren( b );
    }


    public void setHasChildrenHint( boolean b )
    {
        if ( this.getDelegate() != null )
            getDelegate().setHasChildrenHint( b );
    }


    public void setChildrenFilter( String filter )
    {
        if ( this.getDelegate() != null )
            getDelegate().setChildrenFilter( filter );
    }


    public void setChildrenInitialized( boolean b )
    {

        if ( !b )
        {
            if ( this.getDelegate() != null )
            {
                getDelegate().setChildrenInitialized( b );
            }
            // setDelegate(null);
            this.entryDoesNotExist = false;
        }
        else
        /* if(b) */{
            if ( this.getDelegate() == null )
            {
                setDelegate( this.getBrowserConnection().getEntryFromCache( this.dn ) );
                if ( this.getDelegate() == null )
                {
                    // entry doesn't exist!
                    this.entryDoesNotExist = true;
                }
            }
            if ( this.getDelegate() != null )
            {
                getDelegate().setChildrenInitialized( b );
            }
        }
    }


    public boolean isAlias()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isAlias();
        else
            return false;
    }


    public void setAlias( boolean b )
    {
        if ( this.getDelegate() != null )
            getDelegate().setAlias( b );
    }


    public boolean isReferral()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isReferral();
        else
            return false;
    }


    public void setReferral( boolean b )
    {
        if ( this.getDelegate() != null )
            getDelegate().setReferral( b );
    }


    public boolean isSubentry()
    {
        if ( this.getDelegate() != null )
            return getDelegate().isSubentry();
        else
            return false;
    }


    public void setSubentry( boolean b )
    {
        if ( this.getDelegate() != null )
            getDelegate().setSubentry( b );
    }


    public void entryUpdated( EntryModificationEvent event )
    {

        if ( event.getModifiedEntry() == this.getDelegate() )
        {

            if ( event instanceof AttributeAddedEvent )
            {
                AttributeAddedEvent e = ( AttributeAddedEvent ) event;
                AttributeAddedEvent delegateEvent = new AttributeAddedEvent( e.getConnection(), this, e
                    .getAddedAttribute() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof AttributeDeletedEvent )
            {
                AttributeDeletedEvent e = ( AttributeDeletedEvent ) event;
                AttributeDeletedEvent delegateEvent = new AttributeDeletedEvent( e.getConnection(), this, e
                    .getDeletedAttribute() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof AttributesInitializedEvent )
            {
                AttributesInitializedEvent e = ( AttributesInitializedEvent ) event;
                AttributesInitializedEvent delegateEvent = new AttributesInitializedEvent( this );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof EmptyValueAddedEvent )
            {
                EmptyValueAddedEvent e = ( EmptyValueAddedEvent ) event;
                EmptyValueAddedEvent delegateEvent = new EmptyValueAddedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getAddedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof EmptyValueDeletedEvent )
            {
                EmptyValueDeletedEvent e = ( EmptyValueDeletedEvent ) event;
                EmptyValueDeletedEvent delegateEvent = new EmptyValueDeletedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getDeletedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            // EntryAddedEvent
            // EntryDeletedEvent
            // EntryMovedEvent
            // EntryRenamedEvent
            else if ( event instanceof ChildrenInitializedEvent )
            {
                ChildrenInitializedEvent e = ( ChildrenInitializedEvent ) event;
                ChildrenInitializedEvent delegateEvent = new ChildrenInitializedEvent( this );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueAddedEvent )
            {
                ValueAddedEvent e = ( ValueAddedEvent ) event;
                ValueAddedEvent delegateEvent = new ValueAddedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getAddedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueDeletedEvent )
            {
                ValueDeletedEvent e = ( ValueDeletedEvent ) event;
                ValueDeletedEvent delegateEvent = new ValueDeletedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getDeletedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueModifiedEvent )
            {
                ValueModifiedEvent e = ( ValueModifiedEvent ) event;
                ValueModifiedEvent delegateEvent = new ValueModifiedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getOldValue(), e.getNewValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueRenamedEvent )
            {
                ValueRenamedEvent e = ( ValueRenamedEvent ) event;
                ValueRenamedEvent delegateEvent = new ValueRenamedEvent( e.getConnection(), this, e
                    .getOldValue(), e.getNewValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            // ValuesSetEvent

        }
    }


    public Object getAdapter( Class adapter )
    {

        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IBrowserConnection.class )
        {
            return this.getBrowserConnection();
        }
        if ( adapter == IEntry.class )
        {
            return this;
        }
        return null;
    }

}
