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
package org.apache.directory.studio.apacheds.configuration.model.v150;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.studio.apacheds.configuration.StudioEntityResolver;
import org.apache.directory.studio.apacheds.configuration.model.AbstractServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * This class implements a parser and a writer for the 'server.xml' file of 
 * Apache Directory Server version 1.5.0.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerXmlIOV150 extends AbstractServerXmlIO implements ServerXmlIO
{
    /**
     * Checks if the Document is valid.
     *
     * @param document
     *      the Document
     * @return
     *      true if the Document is valid, false if not
     */
    protected boolean isValid( Document document )
    {
        for ( Iterator<?> i = document.getRootElement().elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
        {
            Element element = ( Element ) i.next();
            org.dom4j.Attribute classAttribute = element.attribute( "class" ); //$NON-NLS-1$
            if ( classAttribute != null
                && ( classAttribute.getValue()
                    .equals( "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" ) ) ) //$NON-NLS-1$
            {
                String partitionName = readBeanProperty( "name", element ); //$NON-NLS-1$

                if ( partitionName != null )
                {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public ServerConfiguration parse( InputStream is ) throws ServerXmlIOException
    {
        try
        {
            SAXReader reader = new SAXReader();
            reader.setEntityResolver( new StudioEntityResolver() );
            Document document = reader.read( is );

            ServerConfigurationV150 serverConfiguration = new ServerConfigurationV150();

            parse( document, serverConfiguration );

            return serverConfiguration;
        }
        catch ( Exception e )
        {
            if ( e instanceof ServerXmlIOException )
            {
                throw ( ServerXmlIOException ) e;
            }
            else
            {
                ServerXmlIOException exception = new ServerXmlIOException( e.getMessage(), e.getCause() );
                exception.setStackTrace( e.getStackTrace() );
                throw exception;
            }
        }
    }


    /**
     * Parses the Document.
     *
     * @param document
     *      the Document
     * @param serverConfiguration
     *      the Server Configuration
     * @throws NumberFormatException
     * @throws BooleanFormatException
     * @throws ServerXmlIOException 
     */
    private void parse( Document document, ServerConfigurationV150 serverConfiguration ) throws NumberFormatException,
        BooleanFormatException, ServerXmlIOException
    {
        // Reading the 'Environment' Bean
        readEnvironmentBean( document, serverConfiguration );

        // Reading the 'Configuration' Bean
        readConfigurationBean( document, serverConfiguration );
    }


    /**
     * Reads the "Environment" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readEnvironmentBean( Document document, ServerConfigurationV150 serverConfiguration )
    {
        Element environmentBean = getBeanElementById( document, "environment" ); //$NON-NLS-1$

        // Principal
        String principal = readEnvironmentBeanProperty( "java.naming.security.principal", environmentBean ); //$NON-NLS-1$
        if ( principal != null )
        {
            serverConfiguration.setPrincipal( principal );
        }

        // Password
        String password = readEnvironmentBeanProperty( "java.naming.security.credentials", environmentBean ); //$NON-NLS-1$
        if ( password != null )
        {
            serverConfiguration.setPassword( password );
        }

        // Binary Attributes
        String binaryAttributes = readEnvironmentBeanProperty( "java.naming.ldap.attributes.binary", environmentBean ); //$NON-NLS-1$
        if ( binaryAttributes != null )
        {
            String[] attributes = binaryAttributes.split( " " ); //$NON-NLS-1$

            for ( String attribute : attributes )
            {
                serverConfiguration.addBinaryAttribute( attribute );
            }
        }
    }


    /**
     * Reads the given property in the 'Environment' Bean and returns it.
     *
     * @param property
     *      the property
     * @param element
     *      the Environment Bean Element
     * @return
     *      the value of the property, or null if the property has not been found
     */
    private String readEnvironmentBeanProperty( String property, Element element )
    {
        Element propertyElement = element.element( "property" ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element propsElement = propertyElement.element( "props" ); //$NON-NLS-1$
            if ( propsElement != null )
            {
                for ( Iterator<?> i = propsElement.elementIterator( "prop" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    Element propElement = ( Element ) i.next();
                    org.dom4j.Attribute keyAttribute = propElement.attribute( "key" ); //$NON-NLS-1$
                    if ( keyAttribute != null && ( keyAttribute.getValue().equals( property ) ) )
                    {
                        return propElement.getText();
                    }
                }
            }
        }

        return null;
    }


    /**
     * Reads the "Configuration" Bean and store its values in the given ServerConfiguration.
     *
     * @param document
     *      the document to use
     * @param serverConfiguration
     *      the Server Configuration
     * @throws NumberFormatException
     * @throws BooleanFormatException 
     * @throws ServerXmlIOException 
     */
    private void readConfigurationBean( Document document, ServerConfigurationV150 serverConfiguration )
        throws NumberFormatException, BooleanFormatException, ServerXmlIOException
    {
        Element configurationBean = getBeanElementById( document, "configuration" ); //$NON-NLS-1$

        // LdapPort
        String ldapPort = readBeanProperty( "ldapPort", configurationBean ); //$NON-NLS-1$
        if ( ldapPort != null )
        {
            serverConfiguration.setPort( Integer.parseInt( ldapPort ) );
        }

        // SynchPeriodMillis
        String synchPeriodMillis = readBeanProperty( "synchPeriodMillis", configurationBean ); //$NON-NLS-1$
        if ( synchPeriodMillis != null )
        {
            serverConfiguration.setSynchronizationPeriod( Long.parseLong( synchPeriodMillis ) );
        }

        // MaxTimeLimit
        String maxTimeLimit = readBeanProperty( "maxTimeLimit", configurationBean ); //$NON-NLS-1$
        if ( maxTimeLimit != null )
        {
            serverConfiguration.setMaxTimeLimit( Integer.parseInt( maxTimeLimit ) );
        }

        // MaxSizeLimit
        String maxSizeLimit = readBeanProperty( "maxSizeLimit", configurationBean ); //$NON-NLS-1$
        if ( maxSizeLimit != null )
        {
            serverConfiguration.setMaxSizeLimit( Integer.parseInt( maxSizeLimit ) );
        }

        // MaxThreads
        String maxThreads = readBeanProperty( "maxThreads", configurationBean ); //$NON-NLS-1$
        if ( maxThreads != null )
        {
            serverConfiguration.setMaxThreads( Integer.parseInt( maxThreads ) );
        }

        // AllowAnonymousAccess
        String allowAnonymousAccess = readBeanProperty( "allowAnonymousAccess", configurationBean ); //$NON-NLS-1$
        if ( allowAnonymousAccess != null )
        {
            serverConfiguration.setAllowAnonymousAccess( parseBoolean( allowAnonymousAccess ) );
        }

        // AccessControlEnabled
        String accessControlEnabled = readBeanProperty( "accessControlEnabled", configurationBean ); //$NON-NLS-1$
        if ( accessControlEnabled != null )
        {
            serverConfiguration.setEnableAccessControl( parseBoolean( accessControlEnabled ) );
        }

        // EnableNtp
        String enableNtp = readBeanProperty( "enableNtp", configurationBean ); //$NON-NLS-1$
        if ( enableNtp != null )
        {
            serverConfiguration.setEnableNTP( parseBoolean( enableNtp ) );
        }

        // EnableKerberos
        String enableKerberos = readBeanProperty( "enableKerberos", configurationBean ); //$NON-NLS-1$
        if ( enableKerberos != null )
        {
            serverConfiguration.setEnableKerberos( parseBoolean( enableKerberos ) );
        }

        // EnableChangePassword
        String enableChangePassword = readBeanProperty( "enableChangePassword", configurationBean ); //$NON-NLS-1$
        if ( enableChangePassword != null )
        {
            serverConfiguration.setEnableChangePassword( parseBoolean( enableChangePassword ) );
        }

        // EnableChangePassword
        String denormalizeOpAttrsEnabled = readBeanProperty( "denormalizeOpAttrsEnabled", configurationBean ); //$NON-NLS-1$
        if ( denormalizeOpAttrsEnabled != null )
        {
            serverConfiguration.setDenormalizeOpAttr( parseBoolean( denormalizeOpAttrsEnabled ) );
        }

        // SystemPartition
        String systemPartitionConfiguration = readBeanProperty( "systemPartitionConfiguration", configurationBean ); //$NON-NLS-1$
        if ( systemPartitionConfiguration != null )
        {
            Partition systemPartition = readPartition( document, systemPartitionConfiguration, true );
            if ( systemPartition != null )
            {
                serverConfiguration.addPartition( systemPartition );
            }
        }
        else
        {
            throw new ServerXmlIOException( Messages.getString( "ServerXmlIOV150.0" ) ); //$NON-NLS-1$
        }

        // Other Partitions
        readOtherPartitions( configurationBean, serverConfiguration );

        // Interceptors
        readInterceptors( configurationBean, serverConfiguration );

        // ExtendedOperations
        readExtendedOperations( configurationBean, serverConfiguration );
    }


    /**
     * Reads and adds Partitions (other than the SystemPartition) to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     * @throws BooleanFormatException 
     * @throws NumberFormatException 
     */
    private void readOtherPartitions( Element configurationBean, ServerConfigurationV150 serverConfiguration )
        throws NumberFormatException, BooleanFormatException
    {
        Element propertyElement = getBeanPropertyElement( "partitionConfigurations", configurationBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element setElement = propertyElement.element( "set" ); //$NON-NLS-1$
            if ( setElement != null )
            {
                for ( Iterator<?> i = setElement.elementIterator( "ref" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    Element element = ( Element ) i.next();
                    org.dom4j.Attribute beanAttribute = element.attribute( "bean" ); //$NON-NLS-1$
                    if ( beanAttribute != null )
                    {
                        Partition partition = readPartition( configurationBean.getDocument(), beanAttribute.getValue(),
                            false );
                        if ( partition != null )
                        {
                            serverConfiguration.addPartition( partition );
                        }
                    }
                }
            }
        }
    }


    /**
     * Reads the partition associated with the given Bean ID and return it.
     *
     * @param document
     *      the document
     * @param id
     *      the Bean ID of the partition
     * @param isSystemPartition
     *      true if this partition is the System Partition
     * @return
     *      the partition associated with the given Bean ID
     * @throws BooleanFormatException 
     */
    private Partition readPartition( Document document, String id, boolean isSystemPartition )
        throws BooleanFormatException, NumberFormatException
    {
        Element partitionBean = getBeanElementById( document, id );
        if ( partitionBean != null )
        {
            Partition partition = new Partition();
            partition.setSystemPartition( isSystemPartition );

            // Name
            String name = readBeanProperty( "name", partitionBean ); //$NON-NLS-1$
            if ( name != null )
            {
                partition.setId( name );
            }

            // CacheSize
            String cacheSize = readBeanProperty( "cacheSize", partitionBean ); //$NON-NLS-1$
            if ( cacheSize != null )
            {
                partition.setCacheSize( Integer.parseInt( cacheSize ) );
            }

            // Suffix
            String suffix = readBeanProperty( "suffix", partitionBean ); //$NON-NLS-1$
            if ( suffix != null )
            {
                partition.setSuffix( suffix );
            }

            // OptimizerEnabled
            String optimizerEnabled = readBeanProperty( "optimizerEnabled", partitionBean ); //$NON-NLS-1$
            if ( optimizerEnabled != null )
            {
                partition.setEnableOptimizer( parseBoolean( optimizerEnabled ) );
            }

            // SynchOnWrite
            String synchOnWrite = readBeanProperty( "synchOnWrite", partitionBean ); //$NON-NLS-1$
            if ( synchOnWrite != null )
            {
                partition.setSynchronizationOnWrite( parseBoolean( synchOnWrite ) );
            }

            // IndexedAttributes
            partition.setIndexedAttributes( readPartitionIndexedAttributes( partitionBean ) );

            // ContextEntry
            partition.setContextEntry( readPartitionContextEntry( partitionBean ) );

            return partition;
        }

        return null;
    }


    /**
     * Reads the Indexed Attributes of the given Partition Bean Element
     *
     * @param partitionBean
     *      the Partition Bean Element
     * @return
     *      the Indexed Attributes
     */
    private List<IndexedAttribute> readPartitionIndexedAttributes( Element partitionBean ) throws NumberFormatException
    {
        List<IndexedAttribute> indexedAttributes = new ArrayList<IndexedAttribute>();

        Element propertyElement = getBeanPropertyElement( "indexedAttributes", partitionBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element setElement = propertyElement.element( "set" ); //$NON-NLS-1$
            if ( setElement != null )
            {
                for ( Iterator<?> i = setElement.elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    Element beanElement = ( Element ) i.next();
                    IndexedAttribute ia = readIndexedAttribute( beanElement );
                    if ( ia != null )
                    {
                        indexedAttributes.add( ia );
                    }
                }
            }
        }

        return indexedAttributes;
    }


    /**
     * Reads an Indexed Attribute.
     *
     * @param beanElement
     *      the Bean Element of the Indexed Attribute
     * @return
     *      the corresponding Indexed Attribute or null if it could not be parsed
     * @throws NumberFormatException
     */
    private IndexedAttribute readIndexedAttribute( Element beanElement ) throws NumberFormatException
    {
        org.dom4j.Attribute classAttribute = beanElement.attribute( "class" ); //$NON-NLS-1$
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration" ) ) //$NON-NLS-1$
        {
            String attributeId = readBeanProperty( "attributeId", beanElement ); //$NON-NLS-1$
            String cacheSize = readBeanProperty( "cacheSize", beanElement ); //$NON-NLS-1$
            if ( ( attributeId != null ) && ( cacheSize != null ) )
            {
                return new IndexedAttribute( attributeId, Integer.parseInt( cacheSize ) );
            }

        }

        return null;
    }


    /**
     * Reads the Context Entry of the given Partition Bean Element
     *
     * @param partitionBean
     *      the Partition Bean Element
     * @return
     *      the Context Entry
     */
    private Entry readPartitionContextEntry( Element partitionBean )
    {
        Element propertyElement = getBeanPropertyElement( "contextEntry", partitionBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element valueElement = propertyElement.element( "value" ); //$NON-NLS-1$
            if ( valueElement != null )
            {
                return readContextEntry( valueElement.getText() );
            }
        }

        return new DefaultEntry();
    }


    /**
     * Reads and adds the Interceptors to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readInterceptors( Element configurationBean, ServerConfigurationV150 serverConfiguration )
    {
        Element propertyElement = getBeanPropertyElement( "interceptorConfigurations", configurationBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element listElement = propertyElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    Interceptor interceptor = readInterceptor( ( Element ) i.next() );
                    if ( interceptor != null )
                    {
                        serverConfiguration.addInterceptor( interceptor );
                    }
                }
            }
        }
    }


    /**
     * Reads an Interceptor.
     *
     * @param element
     *      the Interceptor Element
     * @return
     *      the Interceptor or null if it could not be parsed
     */
    private Interceptor readInterceptor( Element element )
    {
        org.dom4j.Attribute classAttribute = element.attribute( "class" ); //$NON-NLS-1$
        if ( classAttribute != null
            && classAttribute.getValue().equals(
                "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" ) ) //$NON-NLS-1$
        {
            String name = readBeanProperty( "name", element ); //$NON-NLS-1$

            for ( Iterator<?> i = element.elementIterator( "property" ); i.hasNext(); ) //$NON-NLS-1$
            {
                Element propertyElement = ( Element ) i.next();
                org.dom4j.Attribute nameAttribute = propertyElement.attribute( "name" ); //$NON-NLS-1$
                if ( nameAttribute != null && ( nameAttribute.getValue().equals( "interceptor" ) ) ) //$NON-NLS-1$
                {
                    Element beanElement = propertyElement.element( "bean" ); //$NON-NLS-1$
                    if ( beanElement != null )
                    {
                        org.dom4j.Attribute beanClassAttribute = beanElement.attribute( "class" ); //$NON-NLS-1$
                        if ( beanClassAttribute != null )
                        {
                            Interceptor interceptor = new Interceptor( name );
                            interceptor.setClassType( beanClassAttribute.getValue() );
                            return interceptor;
                        }
                    }
                }
            }
        }

        return null;
    }


    /**
     * Reads and adds the ExtendedOperations to the Server Configuration.
     *
     * @param configurationBean
     *      the Configuration Bean Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void readExtendedOperations( Element configurationBean, ServerConfigurationV150 serverConfiguration )
    {
        Element propertyElement = getBeanPropertyElement( "extendedOperationHandlers", configurationBean ); //$NON-NLS-1$
        if ( propertyElement != null )
        {
            Element listElement = propertyElement.element( "list" ); //$NON-NLS-1$
            if ( listElement != null )
            {
                for ( Iterator<?> i = listElement.elementIterator( "bean" ); i.hasNext(); ) //$NON-NLS-1$
                {
                    ExtendedOperation extendedOperation = readExtendedOperation( ( Element ) i.next() );
                    if ( extendedOperation != null )
                    {
                        serverConfiguration.addExtendedOperation( extendedOperation );
                    }
                }
            }
        }
    }


    /**
     * Reads an Extended Operation.
     *
     * @param element
     *      the Extended Operation Element
     * @return
     *      the Extended Operation or null if it could not be parsed
     */
    private ExtendedOperation readExtendedOperation( Element element )
    {
        org.dom4j.Attribute classAttribute = element.attribute( "class" ); //$NON-NLS-1$
        if ( classAttribute != null )
        {
            return new ExtendedOperation( classAttribute.getValue() );
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String toXml( ServerConfiguration serverConfiguration ) throws IOException
    {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "beans" ); //$NON-NLS-1$

        // Environment Bean
        createEnvironmentBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // Configuration Bean
        createConfigurationBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // System Partition Configuration Bean
        createSystemPartitionConfigurationBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // User Partitions Beans
        createUserPartitionsConfigurationsBean( root, ( ServerConfigurationV150 ) serverConfiguration );

        // CustomEditors Bean
        createCustomEditorsBean( root );

        // Adding specific doctype
        document.addDocType( "beans", "-//SPRING//DTD BEAN//EN", //$NON-NLS-1$ //$NON-NLS-2$
            "http://www.springframework.org/dtd/spring-beans.dtd" ); //$NON-NLS-1$

        // Creating the output stream we're going to put the XML in
        OutputStream os = new ByteArrayOutputStream();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$

        // Writing the XML.
        XMLWriter writer = new XMLWriter( os, outformat );
        writer.write( document );
        writer.flush();
        writer.close();

        return os.toString();
    }


    /**
     * Creates the Environment Bean
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void createEnvironmentBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        Element environmentBean = root.addElement( "bean" ); //$NON-NLS-1$
        environmentBean.addAttribute( "id", "environment" ); //$NON-NLS-1$ //$NON-NLS-2$
        environmentBean.addAttribute( "class", "org.springframework.beans.factory.config.PropertiesFactoryBean" ); //$NON-NLS-1$ //$NON-NLS-2$

        Element propertyElement = environmentBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "properties" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element propsElement = propertyElement.addElement( "props" ); //$NON-NLS-1$

        // Key 'java.naming.security.authentication'
        Element propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
        propElement.addAttribute( "key", "java.naming.security.authentication" ); //$NON-NLS-1$ //$NON-NLS-2$
        propElement.setText( "simple" ); //$NON-NLS-1$

        // Key 'java.naming.security.principal'
        propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
        propElement.addAttribute( "key", "java.naming.security.principal" ); //$NON-NLS-1$ //$NON-NLS-2$
        propElement.setText( serverConfiguration.getPrincipal() );

        // Key 'java.naming.security.credentials'
        propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
        propElement.addAttribute( "key", "java.naming.security.credentials" ); //$NON-NLS-1$ //$NON-NLS-2$
        propElement.setText( serverConfiguration.getPassword() );

        // Key 'java.naming.ldap.attributes.binary'
        if ( !serverConfiguration.getBinaryAttributes().isEmpty() )
        {
            propElement = propsElement.addElement( "prop" ); //$NON-NLS-1$
            propElement.addAttribute( "key", "java.naming.ldap.attributes.binary" ); //$NON-NLS-1$ //$NON-NLS-2$
            StringBuffer sb = new StringBuffer();
            for ( String attribute : serverConfiguration.getBinaryAttributes() )
            {
                sb.append( attribute );
                sb.append( " " ); //$NON-NLS-1$
            }
            String attributes = sb.toString();
            propElement.setText( attributes.substring( 0, attributes.length() - 1 ) );
        }

    }


    /**
     * Creates the Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void createConfigurationBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        Element configurationBean = root.addElement( "bean" ); //$NON-NLS-1$
        configurationBean.addAttribute( "id", "configuration" ); //$NON-NLS-1$ //$NON-NLS-2$
        configurationBean.addAttribute( "class", //$NON-NLS-1$
            "org.apache.directory.server.configuration.MutableServerStartupConfiguration" ); //$NON-NLS-1$

        // Working directory
        Element propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "workingDirectory" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "example.com" ); // Ask Alex about this value. //$NON-NLS-1$ //$NON-NLS-2$

        // SynchPeriodMillis
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "synchPeriodMillis" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getSynchronizationPeriod() ); //$NON-NLS-1$ //$NON-NLS-2$

        // MaxTimeLimit
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "maxTimeLimit" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxTimeLimit() ); //$NON-NLS-1$ //$NON-NLS-2$

        // MaxSizeLimit
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "maxSizeLimit" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxSizeLimit() ); //$NON-NLS-1$ //$NON-NLS-2$

        // MaxThreads
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "maxThreads" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getMaxThreads() ); //$NON-NLS-1$ //$NON-NLS-2$

        // AllowAnonymousAccess
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "allowAnonymousAccess" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isAllowAnonymousAccess() ); //$NON-NLS-1$ //$NON-NLS-2$

        // AccessControlEnabled
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "accessControlEnabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableAccessControl() ); //$NON-NLS-1$ //$NON-NLS-2$

        // Enable NTP
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "enableNtp" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableNTP() ); //$NON-NLS-1$ //$NON-NLS-2$

        // EnableKerberos
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "enableKerberos" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableKerberos() ); //$NON-NLS-1$ //$NON-NLS-2$

        // EnableChangePassword
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "enableChangePassword" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isEnableChangePassword() ); //$NON-NLS-1$ //$NON-NLS-2$

        // DenormalizeOpAttrsEnabled
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "denormalizeOpAttrsEnabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.isDenormalizeOpAttr() ); //$NON-NLS-1$ //$NON-NLS-2$

        // LdapPort
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "ldapPort" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + serverConfiguration.getPort() ); //$NON-NLS-1$ //$NON-NLS-2$

        // SystemPartitionConfiguration
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "systemPartitionConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "ref", "systemPartitionConfiguration" ); //$NON-NLS-1$ //$NON-NLS-2$

        // PartitionConfigurations
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "partitionConfigurations" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getPartitions().size() > 1 )
        {
            Element setElement = propertyElement.addElement( "set" ); //$NON-NLS-1$
            int partitionCounter = 1;
            for ( Partition partition : serverConfiguration.getPartitions() )
            {
                if ( !partition.isSystemPartition() )
                {
                    setElement.addElement( "ref" ).addAttribute( "bean", "partition-" + partitionCounter ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    partitionCounter++;
                }
            }
        }

        // ExtendedOperationHandlers
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "extendedOperationHandlers" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getExtendedOperations().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( ExtendedOperation extendedOperation : serverConfiguration.getExtendedOperations() )
            {
                listElement.addElement( "bean" ).addAttribute( "class", extendedOperation.getClassType() ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // InterceptorConfigurations
        propertyElement = configurationBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "interceptorConfigurations" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( serverConfiguration.getInterceptors().size() > 1 )
        {
            Element listElement = propertyElement.addElement( "list" ); //$NON-NLS-1$
            for ( Interceptor interceptor : serverConfiguration.getInterceptors() )
            {
                Element interceptorBeanElement = listElement.addElement( "bean" ); //$NON-NLS-1$
                interceptorBeanElement.addAttribute( "class", //$NON-NLS-1$
                    "org.apache.directory.server.core.configuration.MutableInterceptorConfiguration" ); //$NON-NLS-1$

                Element interceptorPropertyElement = interceptorBeanElement.addElement( "property" ); //$NON-NLS-1$
                interceptorPropertyElement.addAttribute( "name", "name" ); //$NON-NLS-1$ //$NON-NLS-2$
                interceptorPropertyElement.addAttribute( "value", interceptor.getName() ); //$NON-NLS-1$

                interceptorPropertyElement = interceptorBeanElement.addElement( "property" ); //$NON-NLS-1$
                interceptorPropertyElement.addAttribute( "name", "interceptor" ); //$NON-NLS-1$ //$NON-NLS-2$
                interceptorPropertyElement.addElement( "bean" ).addAttribute( "class", //$NON-NLS-1$ //$NON-NLS-2$
                    ( interceptor.getClassType() == null ? "" : interceptor.getClassType() ) ); //$NON-NLS-1$
            }
        }

    }


    /**
     * Creates the SystemPartitionConfiguration Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void createSystemPartitionConfigurationBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        Partition systemPartition = null;
        for ( Partition partition : serverConfiguration.getPartitions() )
        {
            if ( partition.isSystemPartition() )
            {
                systemPartition = partition;
                break;
            }
        }

        if ( systemPartition != null )
        {
            createPartitionConfigurationBean( root, systemPartition, "systemPartitionConfiguration" ); //$NON-NLS-1$
        }
    }


    /**
     * Creates the UserPartitionConfigurations Bean.
     *
     * @param root
     *      the root Element
     * @param serverConfiguration
     *      the Server Configuration
     */
    private void createUserPartitionsConfigurationsBean( Element root, ServerConfigurationV150 serverConfiguration )
    {
        int counter = 1;
        for ( Partition partition : serverConfiguration.getPartitions() )
        {
            if ( !partition.isSystemPartition() )
            {
                createPartitionConfigurationBean( root, partition, "partition-" + counter ); //$NON-NLS-1$
                counter++;
            }
        }
    }


    /**
     * Creates a Partition Configuration Bean.
     *
     * @param root
     *      the root Element
     * @param partition
     *      the Partition
     * @param name
     *      the name to use
     */
    private void createPartitionConfigurationBean( Element root, Partition partition, String name )
    {
        Element partitionBean = root.addElement( "bean" ); //$NON-NLS-1$
        partitionBean.addAttribute( "id", name ); //$NON-NLS-1$
        partitionBean.addAttribute( "class", //$NON-NLS-1$
            "org.apache.directory.server.core.partition.impl.btree.MutableBTreePartitionConfiguration" ); //$NON-NLS-1$

        // Name
        Element propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "name" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", partition.getId() ); //$NON-NLS-1$

        // CacheSize
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "cacheSize" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + partition.getCacheSize() ); //$NON-NLS-1$ //$NON-NLS-2$

        // Suffix
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "suffix" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", partition.getSuffix() ); //$NON-NLS-1$

        // OptimizerEnabled
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "optimizerEnabled" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + partition.isEnableOptimizer() ); //$NON-NLS-1$ //$NON-NLS-2$

        // SynchOnWrite
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "synchOnWrite" ); //$NON-NLS-1$ //$NON-NLS-2$
        propertyElement.addAttribute( "value", "" + partition.isSynchronizationOnWrite() ); //$NON-NLS-1$ //$NON-NLS-2$

        // Indexed Attributes
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "indexedAttributes" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( partition.getIndexedAttributes().size() > 1 )
        {
            Element setElement = propertyElement.addElement( "set" ); //$NON-NLS-1$
            for ( IndexedAttribute indexedAttribute : partition.getIndexedAttributes() )
            {
                Element beanElement = setElement.addElement( "bean" ); //$NON-NLS-1$
                beanElement.addAttribute( "class", //$NON-NLS-1$
                    "org.apache.directory.server.core.partition.impl.btree.MutableIndexConfiguration" ); //$NON-NLS-1$

                // AttributeID
                Element beanPropertyElement = beanElement.addElement( "property" ); //$NON-NLS-1$
                beanPropertyElement.addAttribute( "name", "attributeId" ); //$NON-NLS-1$ //$NON-NLS-2$
                beanPropertyElement.addAttribute( "value", indexedAttribute.getAttributeId() ); //$NON-NLS-1$

                // CacheSize
                beanPropertyElement = beanElement.addElement( "property" ); //$NON-NLS-1$
                beanPropertyElement.addAttribute( "name", "cacheSize" ); //$NON-NLS-1$ //$NON-NLS-2$
                beanPropertyElement.addAttribute( "value", "" + indexedAttribute.getCacheSize() ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // ContextEntry
        propertyElement = partitionBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "contextEntry" ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( partition.getContextEntry() != null )
        {
            Element valueElement = propertyElement.addElement( "value" ); //$NON-NLS-1$

            Entry contextEntry = partition.getContextEntry();
            StringBuffer sb = new StringBuffer();
            Iterator<Attribute> attributes = contextEntry.iterator();
            while ( attributes.hasNext() )
            {
                Attribute attribute = attributes.next();
                Iterator<Value<?>> values = attribute.iterator();
                while ( values.hasNext() )
                {
                    Value<?> value = values.next();
                    sb.append( attribute.getId() + ": " + value.getString() + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            valueElement.setText( sb.toString() );
        }
    }


    /**
     * Creates the Custom Editors Bean.
     *
     * @param root
     *      the root Element
     */
    private void createCustomEditorsBean( Element root )
    {
        Element customEditorsBean = root.addElement( "bean" ); //$NON-NLS-1$
        customEditorsBean.addAttribute( "class", "org.springframework.beans.factory.config.CustomEditorConfigurer" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element propertyElement = customEditorsBean.addElement( "property" ); //$NON-NLS-1$
        propertyElement.addAttribute( "name", "customEditors" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element mapElement = propertyElement.addElement( "map" ); //$NON-NLS-1$
        Element entryElement = mapElement.addElement( "entry" ); //$NON-NLS-1$
        entryElement.addAttribute( "key", "javax.naming.directory.Attributes" ); //$NON-NLS-1$ //$NON-NLS-2$
        Element entryBeanElement = entryElement.addElement( "bean" ); //$NON-NLS-1$
        entryBeanElement.addAttribute( "class", //$NON-NLS-1$
            "org.apache.directory.server.core.configuration.AttributesPropertyEditor" ); //$NON-NLS-1$
    }

}
