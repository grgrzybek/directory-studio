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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.studio.ldapbrowser.common.dialogs.preferences.TextFormatsPreferencePage;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the page to select the target Excel file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportExcelToWizardPage extends ExportBaseToPage
{

    /** The extensions used by Excel files */
    private static final String[] EXTENSIONS = new String[]
        { "*.xls", "*.*" };


    /**
     * Creates a new instance of ExportExcelToWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ExportExcelToWizardPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName, wizard );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_XLS_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        final Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        super.createControl( composite );

        BaseWidgetUtils.createSpacer( composite, 3 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        String text = "See <a>Text Formats</a> for Excel file format preferences.";
        Link link = BaseWidgetUtils.createLink( composite, text, 2 );
        link.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(), TextFormatsPreferencePage.class.getName(), null,
                    TextFormatsPreferencePage.XLS_TAB ).open();
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 3 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createWrappedLabel( composite,
            "Warning: Excel export is memory intensive! Maximum number of exportable entries is limited to 65000!", 2 );
    }


    /**
     * {@inheritDoc}
     */
    protected String[] getExtensions()
    {
        return EXTENSIONS;
    }


    /**
     * {@inheritDoc}
     */
    protected String getFileType()
    {
        return "Excel";
    }

}
