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

package org.apache.directory.studio.ldapservers.jobs;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements a {@link Job} that is used to start an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StartLdapServerJob extends Job
{
    /** The server */
    private LdapServer server;


    /**
     * Creates a new instance of StartLdapServerJob.
     * 
     * @param server
     *            the LDAP Server
     */
    public StartLdapServerJob( LdapServer server )
    {
        super( "" ); //$NON-NLS-1$
        this.server = server;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run( IProgressMonitor monitor )
    {
        // Setting the name of the Job
        setName( NLS.bind( Messages.getString( "StartLdapServerJob.Starting" ), new String[] { server.getName() } ) ); //$NON-NLS-1$

        // Setting the status on the server to 'starting'
        server.setStatus( LdapServerStatus.STARTING );

        LdapServerAdapterExtension ldapServerAdapterExtension = server.getLdapServerAdapterExtension();
        if ( ldapServerAdapterExtension != null )
        {
            LdapServerAdapter ldapServerAdapter = ldapServerAdapterExtension.getInstance();
            if ( ldapServerAdapter != null )
            {
                try
                {
                    ldapServerAdapter.start( server, monitor );
                }
                catch ( Exception e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // TODO remove this
        try
        {
            Thread.sleep( 3000 );
        }
        catch ( InterruptedException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        server.setStatus( LdapServerStatus.STARTED );

        return Status.OK_STATUS;
    }
}