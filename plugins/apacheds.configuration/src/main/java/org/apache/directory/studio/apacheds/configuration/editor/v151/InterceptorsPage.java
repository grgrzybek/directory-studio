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
package org.apache.directory.studio.apacheds.configuration.editor.v151;


import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class represents the Interceptors Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InterceptorsPage extends FormPage
{
    /** The Page ID*/
    public static final String ID = ServerConfigurationEditor.ID + ".V151.InterceptorsPage"; //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = "Interceptors";

    /** The Master/Details Block */
    private InterceptorsMasterDetailsBlock masterDetailsBlock;


    /**
     * Creates a new instance of InterceptorsPage.
     *
     * @param editor
     *      the associated editor
     */
    public InterceptorsPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        PlatformUI.getWorkbench().getHelpSystem().setHelp( getPartControl(),
            ApacheDSConfigurationPluginConstants.PLUGIN_ID + "." + "configuration_editor_151" ); //$NON-NLS-1$ //$NON-NLS-2$

        final ScrolledForm form = managedForm.getForm();
        form.setText( "Interceptors" );
        masterDetailsBlock = new InterceptorsMasterDetailsBlock( this );
        masterDetailsBlock.createContent( managedForm );
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
        if ( masterDetailsBlock != null )
        {
            masterDetailsBlock.save();
        }
    }
}
