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
package org.apache.directory.studio.apacheds.properties;


import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.model.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * This class implements the Info property page for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{
    /**
     * Creates a new instance of ServerPropertyPage.
     */
    public ServerPropertyPage()
    {
        super();

        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Name
        Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setText( Messages.getString( "ServerPropertyPage.Name" ) ); //$NON-NLS-1$
        Text nameText = new Text( composite, SWT.NONE );
        nameText.setEditable( false );
        nameText.setBackground( parent.getBackground() );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Version
        Label versionLabel = new Label( composite, SWT.NONE );
        versionLabel.setText( Messages.getString( "ServerPropertyPage.Version" ) ); //$NON-NLS-1$
        Text versionText = new Text( composite, SWT.NONE );
        versionText.setEditable( false );
        versionText.setBackground( parent.getBackground() );
        versionText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Location
        Label locationLabel = new Label( composite, SWT.NONE );
        locationLabel.setText( Messages.getString( "ServerPropertyPage.Location" ) ); //$NON-NLS-1$
        Text locationText = new Text( composite, SWT.WRAP );
        locationText.setEditable( false );
        locationText.setBackground( parent.getBackground() );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.widthHint = 300;
        locationText.setLayoutData( gd );

        // Getting the server
        Server server = ( Server ) getElement();
        if ( server != null )
        {
            nameText.setText( server.getName() );
            versionText.setText( server.getVersion().toString() );
            locationText.setText( ApacheDsPluginUtils.getApacheDsServersFolder().append( server.getId() ).toOSString() );
        }

        return parent;
    }
}
