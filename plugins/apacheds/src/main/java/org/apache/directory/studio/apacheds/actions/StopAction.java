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


import java.io.IOException;

import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.ConsolesHandler;
import org.apache.directory.studio.apacheds.LogMessageConsole;
import org.apache.directory.studio.apacheds.jobs.LaunchServerJob;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This class implements the stop action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StopAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;

    /** The server */
    private Server server;


    /**
     * Creates a new instance of StopAction.
     */
    public StopAction()
    {
        super( Messages.getString( "StopAction.Stop" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of StopAction.
     *
     * @param view
     *      the associated view
     */
    public StopAction( ServersView view )
    {
        super( Messages.getString( "StopAction.Stop" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( ApacheDsPluginConstants.CMD_STOP );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_STOP );
        setToolTipText( Messages.getString( "StopAction.StopToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( ApacheDsPlugin.getDefault().getImageDescriptor( ApacheDsPluginConstants.IMG_STOP ) );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( view != null )
        {
            // Getting the selection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
            if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
            {
                // Getting the server
                server = ( Server ) selection.getFirstElement();

                // Setting the server of the server to 'stopping'
                server.setState( ServerStateEnum.STOPPING );

                // Getting the launch job
                LaunchServerJob launchJob = server.getLaunchJob();
                if ( launchJob != null )
                {
                    // Getting the launch
                    ILaunch launch = launchJob.getLaunch();
                    if ( ( launch != null ) && ( !launch.isTerminated() ) )
                    {
                        // Terminating the launch
                        try
                        {
                            launch.terminate();
                            writeToInfoConsoleMessageStream( Messages.getString( "StopAction.ServerStopped" ) ); //$NON-NLS-1$
                        }
                        catch ( DebugException e )
                        {
                            ApacheDsPluginUtils.reportError( Messages.getString( "StopAction.ErrorWhenStopping" ) //$NON-NLS-1$
                                + e.getMessage() );
                        }
                    }
                }
            }
        }
    }


    /**
     * Writes the given message to the Info console message stream.
     *
     * @param message
     *      the message
     */
    private void writeToInfoConsoleMessageStream( final String message )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                LogMessageConsole console = ConsolesHandler.getDefault().getLogMessageConsole( server.getId() );
                try
                {
                    console.getInfoConsoleMessageStream().write( message );
                }
                catch ( IOException e )
                {
                    ApacheDsPluginUtils.reportError( Messages.getString( "StopAction.ErrorWhenWriting" ) //$NON-NLS-1$
                        + e.getMessage() );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
