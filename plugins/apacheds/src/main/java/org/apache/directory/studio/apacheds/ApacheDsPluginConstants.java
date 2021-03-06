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
package org.apache.directory.studio.apacheds;


/**
 * This class stores all the constants used in the plugin.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class ApacheDsPluginConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private ApacheDsPluginConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = ApacheDsPlugin.getDefault().getPluginProperties().getString( "Plugin_id" ); //$NON-NLS-1$

    // ------
    // IMAGES
    // ------
    public static final String IMG_SERVER_NEW = "resources/icons/server_new.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_NEW_WIZARD = "resources/icons/server_new_wizard.png"; //$NON-NLS-1$
    public static final String IMG_SERVER = "resources/icons/server.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTED = "resources/icons/server_started.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTING1 = "resources/icons/server_starting1.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTING2 = "resources/icons/server_starting2.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STARTING3 = "resources/icons/server_starting3.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPED = "resources/icons/server_stopped.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPING1 = "resources/icons/server_stopping1.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPING2 = "resources/icons/server_stopping2.gif"; //$NON-NLS-1$
    public static final String IMG_SERVER_STOPPING3 = "resources/icons/server_stopping3.gif"; //$NON-NLS-1$
    public static final String IMG_RUN = "resources/icons/run.gif"; //$NON-NLS-1$
    public static final String IMG_STOP = "resources/icons/stop.gif"; //$NON-NLS-1$
    public static final String IMG_CREATE_CONNECTION = "resources/icons/connection_new.gif"; //$NON-NLS-1$

    // --------
    // COMMANDS
    // --------
    public static final String CMD_NEW_SERVER = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Cmd_NewServer_id" ); //$NON-NLS-1$
    public static final String CMD_RUN = ApacheDsPlugin.getDefault().getPluginProperties().getString( "Cmd_Run_id" ); //$NON-NLS-1$
    public static final String CMD_STOP = ApacheDsPlugin.getDefault().getPluginProperties().getString( "Cmd_Stop_id" ); //$NON-NLS-1$
    public static final String CMD_PROPERTIES = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Properties_id" ); //$NON-NLS-1$
    public static final String CMD_OPEN_CONFIGURATION = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Cmd_OpenConfiguration_id" ); //$NON-NLS-1$
    public static final String CMD_DELETE = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Delete_id" );; //$NON-NLS-1$
    public static final String CMD_RENAME = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Cmd_Rename_id" );; //$NON-NLS-1$
    public static final String CMD_CREATE_CONNECTION = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Cmd_CreateConnection_id" ); //$NON-NLS-1$

    // --------------
    // PROPERTY PAGES
    // --------------
    public static final String PROP_SERVER_PROPERTY_PAGE = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Prop_ServerPropertyPage_id" ); //$NON-NLS-1$

    // -----
    // VIEWS
    // -----
    public static final String VIEW_SERVERS_VIEW = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "View_ServersView_id" ); //$NON-NLS-1$

    // --------
    // CONTEXTS
    // --------
    public static final String CONTEXTS_SERVERS_VIEW = ApacheDsPlugin.getDefault().getPluginProperties().getString(
        "Ctx_ServersView_id" ); //$NON-NLS-1$

    // -----------
    // PREFERENCES
    // -----------
    /** The Preference ID for the Colors and Font Debug Font setting */
    public static final String PREFS_COLORS_AND_FONTS_DEBUG_FONT = "prefs.colorAndFonts.debugFont"; //$NON-NLS-1$
    /** The Preference ID for the Colors and Font Debug Color setting */
    public static final String PREFS_COLORS_AND_FONTS_DEBUG_COLOR = "prefs.colorAndFonts.debugColor"; //$NON-NLS-1$

    /** The Preference ID for the Colors and Font Info Font setting */
    public static final String PREFS_COLORS_AND_FONTS_INFO_FONT = "prefs.colorAndFonts.infoFont"; //$NON-NLS-1$
    /** The Preference ID for the Colors and Font Info Color setting */
    public static final String PREFS_COLORS_AND_FONTS_INFO_COLOR = "prefs.colorAndFonts.infoColor"; //$NON-NLS-1$

    /** The Preference ID for the Colors and Font Warn Font setting */
    public static final String PREFS_COLORS_AND_FONTS_WARN_FONT = "prefs.colorAndFonts.warnFont"; //$NON-NLS-1$
    /** The Preference ID for the Colors and Font Warn Color setting */
    public static final String PREFS_COLORS_AND_FONTS_WARN_COLOR = "prefs.colorAndFonts.warnColor"; //$NON-NLS-1$

    /** The Preference ID for the Colors and Font Error Font settings */
    public static final String PREFS_COLORS_AND_FONTS_ERROR_FONT = "prefs.colorAndFonts.errorFont"; //$NON-NLS-1$
    /** The Preference ID for the Colors and Font Error Color setting */
    public static final String PREFS_COLORS_AND_FONTS_ERROR_COLOR = "prefs.colorAndFonts.errorColor"; //$NON-NLS-1$

    /** The Preference ID for the Colors and Font Fatal Font setting */
    public static final String PREFS_COLORS_AND_FONTS_FATAL_FONT = "prefs.colorAndFonts.fatalFont"; //$NON-NLS-1$
    /** The Preference ID for the Colors and Font Fatal Color setting */
    public static final String PREFS_COLORS_AND_FONTS_FATAL_COLOR = "prefs.colorAndFonts.fatalColor"; //$NON-NLS-1$

    /** The Preference ID for the Servers Logs Level setting */
    public static final String PREFS_SERVER_LOGS_LEVEL = "prefs.serverLogs.level"; //$NON-NLS-1$
    /** The Preference ID for the Servers Logs Level Debug value */
    public static final String PREFS_SERVER_LOGS_LEVEL_DEBUG = "debug"; //$NON-NLS-1$
    /** The Preference ID for the Servers Logs Level Info value */
    public static final String PREFS_SERVER_LOGS_LEVEL_INFO = "info"; //$NON-NLS-1$
    /** The Preference ID for the Servers Logs Level Warn value */
    public static final String PREFS_SERVER_LOGS_LEVEL_WARN = "warning"; //$NON-NLS-1$
    /** The Preference ID for the Servers Logs Level Error value */
    public static final String PREFS_SERVER_LOGS_LEVEL_ERROR = "error"; //$NON-NLS-1$
    /** The Preference ID for the Servers Logs Level Fatal value */
    public static final String PREFS_SERVER_LOGS_LEVEL_FATAL = "fatal"; //$NON-NLS-1$

    /** The Preference ID for the Servers Logs Pattern setting */
    public static final String PREFS_SERVER_LOGS_PATTERN = "prefs.serverLogs.pattern"; //$NON-NLS-1$
}
