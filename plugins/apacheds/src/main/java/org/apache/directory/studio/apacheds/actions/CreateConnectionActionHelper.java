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
package org.apache.directory.studio.apacheds.actions;


import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerConfigurationV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerConfigurationV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerConfigurationV156;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements a helper class of the create connection action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CreateConnectionActionHelper
{
    /**
     * Creates a connection in the LDAP Browser plugin.
     *
     * @param serverName
     *      the name of the server
     * @param serverConfiguration
     *      the server configuration
     */
    public static void createLdapBrowserConnection( String serverName, ServerConfiguration serverConfiguration )
    {
        //Creating the connection parameter object
        ConnectionParameter connectionParameter = new ConnectionParameter();

        // Auth method
        connectionParameter.setAuthMethod( AuthenticationMethod.SIMPLE );

        // Encryption method and port
        if ( serverConfiguration instanceof ServerConfigurationV156 )
        {
            ServerConfigurationV156 serverConfigurationV156 = ( ServerConfigurationV156 ) serverConfiguration;
            if ( serverConfigurationV156.isEnableLdap() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
                connectionParameter.setPort( serverConfigurationV156.getLdapPort() );
            }
            else if ( serverConfigurationV156.isEnableLdaps() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.LDAPS );
                connectionParameter.setPort( serverConfigurationV156.getLdapsPort() );
            }
        }
        else if ( serverConfiguration instanceof ServerConfigurationV155 )
        {
            ServerConfigurationV155 serverConfiguration155 = ( ServerConfigurationV155 ) serverConfiguration;
            if ( serverConfiguration155.isEnableLdap() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
                connectionParameter.setPort( serverConfiguration155.getLdapPort() );
            }
            else if ( serverConfiguration155.isEnableLdaps() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.LDAPS );
                connectionParameter.setPort( serverConfiguration155.getLdapsPort() );
            }
        }
        else if ( serverConfiguration instanceof ServerConfigurationV154 )
        {
            ServerConfigurationV154 serverConfiguration154 = ( ServerConfigurationV154 ) serverConfiguration;
            if ( serverConfiguration154.isEnableLdap() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
                connectionParameter.setPort( serverConfiguration154.getLdapPort() );
            }
            else if ( serverConfiguration154.isEnableLdaps() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.LDAPS );
                connectionParameter.setPort( serverConfiguration154.getLdapsPort() );
            }
        }
        else if ( serverConfiguration instanceof ServerConfigurationV153 )
        {
            ServerConfigurationV153 serverConfiguration153 = ( ServerConfigurationV153 ) serverConfiguration;
            if ( serverConfiguration153.isEnableLdap() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.NONE );
                connectionParameter.setPort( serverConfiguration153.getLdapPort() );
            }
            else if ( serverConfiguration153.isEnableLdaps() )
            {
                connectionParameter.setEncryptionMethod( EncryptionMethod.LDAPS );
                connectionParameter.setPort( serverConfiguration153.getLdapsPort() );
            }
        }

        // Bind password
        connectionParameter.setBindPassword( "secret" ); //$NON-NLS-1$

        // Bind principal
        connectionParameter.setBindPrincipal( "uid=admin,ou=system" ); //$NON-NLS-1$

        // Host
        connectionParameter.setHost( "localhost" ); //$NON-NLS-1$

        // Name
        connectionParameter.setName( serverName );
        
        // Network Provider
        connectionParameter.setNetworkProvider( ConnectionCorePlugin.getDefault().getDefaultNetworkProvider() );
        
        // Creating the connection
        Connection connection = new Connection( connectionParameter );

        // Adding the connection to the connection manager
        ConnectionCorePlugin.getDefault().getConnectionManager().addConnection( connection );

        // Adding the connection to the root connection folder
        ConnectionCorePlugin.getDefault().getConnectionFolderManager().getRootConnectionFolder().addConnectionId(
            connection.getId() );

        // Getting the window, LDAP perspective and current perspective
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IPerspectiveDescriptor ldapPerspective = getLdapPerspective();
        IPerspectiveDescriptor currentPerspective = window.getActivePage().getPerspective();

        // Checking if we are already in the LDAP perspective
        if ( ( ldapPerspective != null ) && ( ldapPerspective.equals( currentPerspective ) ) )
        {
            // As we're already in the LDAP perspective, we only indicate to the user 
            // the name of the connection that has been created
            MessageDialog dialog = new MessageDialog(
                window.getShell(),
                Messages.getString( "CreateConnectionActionHelper.ConnectionCreated" ), null, //$NON-NLS-1$
                NLS
                    .bind(
                        Messages.getString( "CreateConnectionActionHelper.ConnectionCalledCreated" ), new String[] { connection.getName() } ), MessageDialog.INFORMATION, //$NON-NLS-1$
                new String[]
                    { IDialogConstants.OK_LABEL }, MessageDialog.OK );
            dialog.open();
        }
        else
        {
            // We're not already in the LDAP perspective, we indicate to the user
            // the name of the connection that has been created and we ask him
            // if we wants to switch to the LDAP perspective
            MessageDialog dialog = new MessageDialog(
                window.getShell(),
                Messages.getString( "CreateConnectionActionHelper.ConnectionCreated" ), null, //$NON-NLS-1$
                NLS
                    .bind(
                        Messages.getString( "CreateConnectionActionHelper.ConnectionCalledCreatedSwitch" ), new String[] { connection.getName() } ), //$NON-NLS-1$
                MessageDialog.INFORMATION, new String[]
                    { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, MessageDialog.OK );
            if ( dialog.open() == MessageDialog.OK )
            {
                // Switching to the LDAP perspective
                window.getActivePage().setPerspective( ldapPerspective );
            }
        }
    }


    /**
     * Get the LDAP perspective.
     *
     * @return
     *      the LDAP perspective
     */
    private static IPerspectiveDescriptor getLdapPerspective()
    {
        for ( IPerspectiveDescriptor perspective : PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives() )
        {
            if ( ApacheDsPlugin.getDefault().getPluginProperties().getString( "Perspective_LdapBrowserPerspective_id" ) //$NON-NLS-1$
                .equalsIgnoreCase( perspective.getId() ) )
            {
                return perspective;
            }
        }

        return null;
    }
}
