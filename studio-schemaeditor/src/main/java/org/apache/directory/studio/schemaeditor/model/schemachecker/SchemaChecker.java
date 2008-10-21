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
package org.apache.directory.studio.schemaeditor.model.schemachecker;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.schemaeditor.model.schemachecker.NonExistingMatchingRuleError.NonExistingMatchingRuleErrorEnum;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * This class represents the SchemaChecker.
 * <p>
 * It is used to check the schema integrity.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaChecker
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The errors List */
    private List<SchemaError> errorsList;

    /** The errors MultiMap */
    private MultiMap errorsMap;

    /** The warnings List */
    private List<SchemaWarning> warningsList;

    /** The warnings MultiMap */
    private MultiMap warningsMap;

    /** The Dependencies MultiMap */
    private MultiMap dependenciesMap;

    /** The Depends On MultiMap */
    private MultiMap dependsOnMap;

    /** The 'listening to modifications' flag*/
    private boolean listeningToModifications = false;

    /** The listeners List */
    private List<SchemaCheckerListener> listeners;

    /** The SchemaHandlerListener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            checkAttributeType( at );

            notifyListeners();
        }


        public void attributeTypeModified( AttributeTypeImpl at )
        {
            List<?> deps = ( List<?> ) dependenciesMap.get( at );

            checkAttributeType( at );

            checkDependencies( deps );

            notifyListeners();
        }


        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            List<?> deps = ( List<?> ) dependenciesMap.get( at );

            removeSchemaObject( at );

            checkDependencies( deps );

            notifyListeners();
        }


        public void objectClassAdded( ObjectClassImpl oc )
        {
            checkObjectClass( oc );

            notifyListeners();
        }


        public void objectClassModified( ObjectClassImpl oc )
        {
            List<?> deps = ( List<?> ) dependenciesMap.get( oc );

            checkObjectClass( oc );

            checkDependencies( deps );

            notifyListeners();
        }


        public void objectClassRemoved( ObjectClassImpl oc )
        {
            List<?> deps = ( List<?> ) dependenciesMap.get( oc );

            removeSchemaObject( oc );

            checkDependencies( deps );

            notifyListeners();
        }


        public void schemaAdded( Schema schema )
        {
            List<AttributeTypeImpl> ats = schema.getAttributeTypes();
            for ( AttributeTypeImpl at : ats )
            {
                checkAttributeType( at );
            }

            List<ObjectClassImpl> ocs = schema.getObjectClasses();
            for ( ObjectClassImpl oc : ocs )
            {
                checkObjectClass( oc );
            }

            notifyListeners();
        }


        public void schemaRemoved( Schema schema )
        {
            List<AttributeTypeImpl> ats = schema.getAttributeTypes();
            for ( AttributeTypeImpl at : ats )
            {
                removeSchemaObject( at );
            }

            List<ObjectClassImpl> ocs = schema.getObjectClasses();
            for ( ObjectClassImpl oc : ocs )
            {
                removeSchemaObject( oc );
            }

            notifyListeners();
        }
    };


    /**
     * Creates a new instance of SchemaChecker.
     */
    public SchemaChecker()
    {
        schemaHandler = Activator.getDefault().getSchemaHandler();
        errorsList = new ArrayList<SchemaError>();
        errorsMap = new MultiValueMap();
        warningsList = new ArrayList<SchemaWarning>();
        warningsMap = new MultiValueMap();
        dependenciesMap = new MultiValueMap();
        dependsOnMap = new MultiValueMap();
        listeners = new ArrayList<SchemaCheckerListener>();
    }


    /**
     * Enables modifications listening.
     */
    public void enableModificationsListening()
    {
        if ( !listeningToModifications )
        {
            schemaHandler = Activator.getDefault().getSchemaHandler();
            schemaHandler.addListener( schemaHandlerListener );
            listeningToModifications = true;
            checkWholeSchema();
        }
    }


    /**
     * Disables modifications listening.
     */
    public void disableModificationsListening()
    {
        if ( listeningToModifications )
        {
            schemaHandler.removeListener( schemaHandlerListener );
            listeningToModifications = false;
            clearErrorsAndWarnings();
        }
    }


    /**
     * Returns true if the SchemaChecker is listening to modifications, 
     * false if not.
     *
     * @return
     *      true if the SchemaChecker is listening to modifications, 
     * false if not
     */
    public boolean isListeningToModifications()
    {
        return listeningToModifications;
    }


    /**
     * Clears all the errors and warnings.
     */
    private void clearErrorsAndWarnings()
    {
        errorsList.clear();
        errorsMap.clear();
        warningsList.clear();
        warningsMap.clear();
        dependenciesMap.clear();
        dependsOnMap.clear();
    }


    /**
     * Checks the whole schema.
     */
    private void checkWholeSchema()
    {
        Job job = new Job( "Checking the Schema" )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                List<Schema> schemas = schemaHandler.getSchemas();
                monitor.beginTask( "Checking schemas: ", schemas.size() );
                for ( Schema schema : schemas )
                {
                    monitor.subTask( schema.getName() );
                    List<AttributeTypeImpl> ats = schema.getAttributeTypes();
                    for ( AttributeTypeImpl at : ats )
                    {
                        checkAttributeType( at );
                    }

                    List<ObjectClassImpl> ocs = schema.getObjectClasses();
                    for ( ObjectClassImpl oc : ocs )
                    {
                        checkObjectClass( oc );
                    }
                    monitor.worked( 1 );
                }
                notifyListeners();
                monitor.done();

                return Status.OK_STATUS;
            }
        };

        job.setUser( true );
        //        job.setPriority( Job.SHORT );
        job.schedule();
    }


    /**
     * Checks the given attribute type.
     *
     * @param at
     *      an attribute type
     */
    private void checkAttributeType( AttributeTypeImpl at )
    {
        removeSchemaObject( at );

        // Checking OID
        String oid = at.getOid();
        if ( ( oid != null ) && ( !"".equals( oid ) ) )
        {
            List<?> list = getSchemaElementList( oid );
            if ( ( list != null ) && ( list.size() >= 2 ) )
            {
                int counter = 0;
                Object o = list.get( counter );
                while ( ( at.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                {
                    counter++;
                    o = list.get( counter );
                }
                SchemaError error = new DuplicateOidError( at, oid, ( SchemaObject ) o );
                errorsList.add( error );
                errorsMap.put( at, error );
            }
        }

        // Checking aliases
        String[] aliases = at.getNames();
        if ( ( aliases == null ) || ( aliases.length == 0 ) )
        {
            SchemaWarning warning = new NoAliasWarning( at );
            warningsList.add( warning );
            warningsMap.put( at, warning );
        }
        else if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            for ( String alias : aliases )
            {
                List<?> list = getSchemaElementList( alias );
                if ( ( list != null ) && ( list.size() >= 2 ) )
                {
                    int counter = 0;
                    Object o = list.get( counter );
                    while ( ( at.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                    {
                        counter++;
                        o = list.get( counter );
                    }
                    SchemaError error = new DuplicateAliasError( at, alias, ( SchemaObject ) o );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }
            }
        }

        // Checking superior
        String superior = at.getSuperiorName();
        if ( ( superior != null ) && ( !"".equals( superior ) ) )
        {
            AttributeTypeImpl superiorAT = schemaHandler.getAttributeType( superior );
            if ( superiorAT == null )
            {
                SchemaError error = new NonExistingATSuperiorError( at, superior );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( superior, at );
                dependsOnMap.put( at, superior );
            }
            else
            {
                dependenciesMap.put( superiorAT, at );
                dependsOnMap.put( at, superiorAT );

                // Checking Usage with superior's
                UsageEnum usage = at.getUsage();
                UsageEnum superiorATUsage = superiorAT.getUsage();
                if ( !usage.equals( superiorATUsage ) )
                {
                    SchemaError error = new DifferentUsageAsSuperiorError( at, superiorAT );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }

                // Checking Collective with superior's
                boolean collective = at.isCollective();
                boolean superiorATCollective = superiorAT.isCollective();
                if ( superiorATCollective && !collective )
                {
                    SchemaError error = new DifferentCollectiveAsSuperiorError( at, superiorAT );
                    errorsList.add( error );
                    errorsMap.put( at, error );
                }
            }
        }

        // Checking syntax
        String syntaxOid = at.getSyntaxOid();
        if ( ( syntaxOid != null ) && ( !"".equals( syntaxOid ) ) )
        {
            SyntaxImpl syntax = schemaHandler.getSyntax( syntaxOid );
            if ( syntax == null )
            {
                SchemaError error = new NonExistingSyntaxError( at, syntaxOid );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( syntaxOid, at );
                dependsOnMap.put( at, syntaxOid );
            }
            else
            {
                dependenciesMap.put( syntax, at );
                dependsOnMap.put( at, syntax );
            }
        }

        // Equality matching rule
        String equality = at.getEqualityName();
        if ( ( equality != null ) && ( !"".equals( equality ) ) )
        {
            MatchingRuleImpl equalityMR = schemaHandler.getMatchingRule( equality );
            if ( equalityMR == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, equality,
                    NonExistingMatchingRuleErrorEnum.EQUALITY );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( equality, at );
                dependsOnMap.put( at, equality );
            }
            else
            {
                dependenciesMap.put( equalityMR, at );
                dependsOnMap.put( at, equalityMR );
            }
        }

        // Ordering matching rule
        String ordering = at.getOrderingName();
        if ( ( ordering != null ) && ( !"".equals( ordering ) ) )
        {
            MatchingRuleImpl orderingMR = schemaHandler.getMatchingRule( ordering );
            if ( orderingMR == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, ordering,
                    NonExistingMatchingRuleErrorEnum.ORDERING );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( ordering, at );
                dependsOnMap.put( at, ordering );
            }
            else
            {
                dependenciesMap.put( orderingMR, at );
                dependsOnMap.put( at, orderingMR );
            }
        }

        // Substring matching rule
        String substring = at.getSubstrName();
        if ( ( substring != null ) && ( !"".equals( substring ) ) )
        {
            MatchingRuleImpl substringMR = schemaHandler.getMatchingRule( substring );
            if ( substringMR == null )
            {
                SchemaError error = new NonExistingMatchingRuleError( at, substring,
                    NonExistingMatchingRuleErrorEnum.SUBSTRING );
                errorsList.add( error );
                errorsMap.put( at, error );
                dependenciesMap.put( substring, at );
                dependsOnMap.put( at, substring );
            }
            else
            {
                dependenciesMap.put( substringMR, at );
                dependsOnMap.put( at, substringMR );
            }
        }
    }


    /**
     * Checks the given object class.
     *
     * @param oc
     *      an object class
     */
    private void checkObjectClass( ObjectClassImpl oc )
    {
        removeSchemaObject( oc );

        // Checking OID
        String oid = oc.getOid();
        if ( ( oid != null ) && ( !"".equals( oid ) ) )
        {
            List<?> list = getSchemaElementList( oid );
            if ( ( list != null ) && ( list.size() >= 2 ) )
            {
                int counter = 0;
                Object o = list.get( counter );
                while ( ( oc.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                {
                    counter++;
                    o = list.get( counter );
                }
                SchemaError error = new DuplicateOidError( oc, oid, ( SchemaObject ) o );
                errorsList.add( error );
                errorsMap.put( oc, error );
            }
        }

        // Checking aliases
        String[] aliases = oc.getNames();
        if ( ( aliases == null ) || ( aliases.length == 0 ) )
        {
            SchemaWarning warning = new NoAliasWarning( oc );
            warningsList.add( warning );
            warningsMap.put( oc, warning );
        }
        else if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            for ( String alias : aliases )
            {
                List<?> list = getSchemaElementList( alias );
                if ( ( list != null ) && ( list.size() >= 2 ) )
                {
                    int counter = 0;
                    Object o = list.get( counter );
                    while ( ( oc.equals( o ) ) && ( counter < ( list.size() - 1 ) ) )
                    {
                        counter++;
                        o = list.get( counter );
                    }
                    SchemaError error = new DuplicateAliasError( oc, oid, ( SchemaObject ) o );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                }
            }
        }

        // Checking superiors
        String[] superiors = oc.getSuperClassesNames();
        if ( ( superiors != null ) && ( superiors.length >= 1 ) )
        {
            ObjectClassTypeEnum type = oc.getType();

            for ( String superior : superiors )
            {
                ObjectClassImpl superiorOC = schemaHandler.getObjectClass( superior );
                if ( superiorOC == null )
                {
                    SchemaError error = new NonExistingOCSuperiorError( oc, superior );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                    dependenciesMap.put( superior, oc );
                    dependsOnMap.put( oc, superior );
                }
                else
                {
                    dependenciesMap.put( superiorOC, oc );
                    dependsOnMap.put( oc, superiorOC );

                    // Checking Type of Superior Hierarchy
                    ObjectClassTypeEnum superiorOCType = superiorOC.getType();
                    switch ( type )
                    {
                        case ABSTRACT:
                            if ( ( !superiorOCType.equals( ObjectClassTypeEnum.ABSTRACT ) )
                                && ( !superiorOC.getOid().equals( "2.5.6.0" ) ) )
                            {
                                SchemaError error = new ClassTypeHierarchyError( oc, superiorOC );
                                errorsList.add( error );
                                errorsMap.put( oc, error );
                            }
                            break;
                        case AUXILIARY:
                            if ( ( superiorOCType.equals( ObjectClassTypeEnum.STRUCTURAL ) )
                                && ( !superiorOC.getOid().equals( "2.5.6.0" ) ) )
                            {
                                SchemaError error = new ClassTypeHierarchyError( oc, superiorOC );
                                errorsList.add( error );
                                errorsMap.put( oc, error );
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        // Checking mandatory and optional attributes
        String[] mandatoryATNames = oc.getMustNamesList();
        String[] optionalATNames = oc.getMayNamesList();
        if ( ( mandatoryATNames != null ) && ( optionalATNames != null ) )
        {
            for ( String mandatoryATName : mandatoryATNames )
            {
                AttributeTypeImpl mandatoryAT = schemaHandler.getAttributeType( mandatoryATName );
                if ( mandatoryAT == null )
                {
                    SchemaError error = new NonExistingMandatoryATError( oc, mandatoryATName );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                    dependenciesMap.put( mandatoryATName, oc );
                    dependsOnMap.put( oc, mandatoryATName );
                }
                else
                {
                    dependenciesMap.put( mandatoryAT, oc );
                    dependsOnMap.put( oc, mandatoryAT );
                }
            }

            for ( String optionalATName : optionalATNames )
            {
                AttributeTypeImpl optionalAT = schemaHandler.getAttributeType( optionalATName );
                if ( optionalAT == null )
                {
                    SchemaError error = new NonExistingOptionalATError( oc, optionalATName );
                    errorsList.add( error );
                    errorsMap.put( oc, error );
                    dependenciesMap.put( optionalATName, oc );
                    dependsOnMap.put( oc, optionalATName );
                }
                else
                {
                    dependenciesMap.put( optionalAT, oc );
                    dependsOnMap.put( oc, optionalAT );
                }
            }
        }
    }


    /**
     * Remove the errors and warnings for the given schema element.
     *
     * @param element
     *      a schema element
     */
    private void removeSchemaObject( SchemaObject element )
    {
        // Removing old errors and warnings
        List<?> errors = ( List<?> ) errorsMap.get( element );
        if ( ( errors != null ) && ( errors.size() >= 1 ) )
        {
            for ( Object error : errors )
            {
                errorsList.remove( error );
            }
            errorsMap.remove( element );
        }
        List<?> warnings = ( List<?> ) warningsMap.get( element );
        if ( ( warnings != null ) && ( warnings.size() >= 1 ) )
        {
            for ( Object warning : warnings )
            {
                warningsList.remove( warning );
            }
            warningsMap.remove( element );
        }

        // Removing 'depends on' and dependencies
        List<?> dependsOn = ( List<?> ) dependsOnMap.get( element );
        if ( dependsOn != null )
        {
            for ( Object dep : dependsOn )
            {
                dependenciesMap.remove( dep, element );
            }
            dependsOnMap.remove( element );
        }
    }


    @SuppressWarnings("unchecked")
    private List<?> getSchemaElementList( String id )
    {
        List results = new ArrayList<Object>();

        // Attribute types
        List<?> atList = schemaHandler.getAttributeTypeList( id );
        if ( ( atList != null ) && ( atList.size() >= 1 ) )
        {
            results.addAll( atList );
        }

        // Object classes
        List<?> ocList = schemaHandler.getObjectClassList( id );
        if ( ( ocList != null ) && ( ocList.size() >= 1 ) )
        {
            results.addAll( ocList );
        }

        return results;
    }


    /**
     * Gets the errors.
     *
     * @return
     *      the errors
     */
    public List<SchemaError> getErrors()
    {
        return errorsList;
    }


    /**
     * Gets the warnings.
     *
     * @return
     *      the warnings
     */
    public List<SchemaWarning> getWarnings()
    {
        return warningsList;
    }


    /**
     * Adds a SchemaCheckerListener.
     *
     * @param listener
     *      the listener
     */
    public void addListener( SchemaCheckerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }


    /**
     * Removes a SchemaCheckerListener.
     *
     * @param listener
     *      the listener
     */
    public void removeListener( SchemaCheckerListener listener )
    {
        listeners.remove( listener );
    }


    /**
     * Notifies the listeners.
     */
    private void notifyListeners()
    {
        for ( SchemaCheckerListener listener : listeners )
        {
            listener.schemaCheckerUpdated();
        }
    }


    /**
     * Gets the errors associated with the given Schema Object
     *
     * @param so
     *      the Schema Object
     * @return
     *      the associated errors
     */
    public List<?> getErrors( SchemaObject so )
    {
        return ( List<?> ) errorsMap.get( so );
    }


    /**
     * Returns whether the given Schema Object has errors.
     *
     * @param so
     *      the Schema Object
     * @return
     *      true if the given Schema Object has errors.
     */
    public boolean hasErrors( SchemaObject so )
    {
        List<?> errors = getErrors( so );

        if ( errors == null )
        {
            return false;
        }
        else
        {
            return errors.size() > 0;
        }
    }


    /**
     * Gets the warnings associated with the given Schema Object
     *
     * @param so
     *      the Schema Object
     * @return
     *      the associated warnings
     */
    public List<?> getWarnings( SchemaObject so )
    {
        return ( List<?> ) warningsMap.get( so );
    }


    /**
     * Returns whether the given Schema Object has warnings.
     *
     * @param so
     *      the Schema Object
     * @return
     *      true if the given Schema Object has errors.
     */
    public boolean hasWarnings( SchemaObject so )
    {
        List<?> warnings = getWarnings( so );

        if ( warnings == null )
        {
            return false;
        }
        else
        {
            return warnings.size() > 0;
        }
    }


    /**
     * Checks the given list of dependencies.
     * 
     * @param deps
     *      the list of dependencies
     */
    public void checkDependencies( List<?> deps )
    {
        if ( deps != null )
        {
            for ( Object object : deps )
            {
                if ( object instanceof AttributeTypeImpl )
                {
                    checkAttributeType( ( AttributeTypeImpl ) object );
                }
                else if ( object instanceof ObjectClassImpl )
                {
                    checkObjectClass( ( ObjectClassImpl ) object );
                }
            }
        }
    }
}
