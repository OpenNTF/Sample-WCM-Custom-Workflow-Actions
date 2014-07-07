/*
 * Copyright 2014  IBM Corp.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.ibm.wcm.sample.customactions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.workplace.wcm.api.Document;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowAction;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowActionFactory;

/**
 * Simple implementation of a custom workflow action factory. <br />
 * The factory follows these rules: <br />
 * <ul>
 * <li>factory name will be SampleCustomWorkflowActionFactory</li>
 * <li>factory title will be Sample Custom Workflow Action Factory</li>
 * <li>read a property file that stores a list of classnames for the actions</li>
 * <li>Action titles and descriptions will come from the CustomActionResources bundle
 * <li>getAction will use a no-argument constructor to create the action if it is in the list of classes</li>
 * </ul>
 */
public class CustomWorkflowActionFactoryImpl implements CustomWorkflowActionFactory
{
   /** class name for the logger */
   private static final String LOG_CLASS = CustomWorkflowActionFactoryImpl.class.getName();

   /** logging level */
   private static final Level LOG_LEVEL = Level.FINER;

   /** class logger */
   private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

   /** The unique factory name */
   private static final String FACTORY_NAME = "SampleCustomWorkflowActionFactory";
   
   /** Properties file path */
   public static final String PROPERTIES = "factory.properties";

   /** Actions property.  Holds a comma-separated list of action class names. */
   public static final String PROPERTY_ACTIONS = "actions";

   /** Delimiter used to separate class names in the actions property. */
   public static final String ACTIONS_DELIMITER = ",";

   /** Set of fully qualified class names for the registered actions for easy lookup */
   private Set<String> m_actionClassNames = new HashSet<String>();

   /** Array of fully qualified class names for the registered actions for the API */
   private String[] m_actionClassNamesArray;
   
   /**
    * Construct a simple custom workflow action factory using default properties.
    */
   public CustomWorkflowActionFactoryImpl()
   {
      this(PROPERTIES);
   }

   /**
    * Construct a simple custom workflow action factory
    * @param p_propertiesPath Properties file to drive this factory
    */
   public CustomWorkflowActionFactoryImpl(String p_propertiesPath)
   {
      loadProperties(p_propertiesPath);
   }

   /**
    * Load the action class names, titles and descriptions from the default properties file.
    */
   protected void loadProperties(String p_propertiesPath)
   {
      final String LOG_METHOD = "loadProperties(p_propertiesPath)";
      boolean isFiner = LOGGER.isLoggable(LOG_LEVEL);
      if (isFiner)
      {
         LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] {p_propertiesPath});
      }
      
      InputStream stream = CustomWorkflowActionFactoryImpl.class.getResourceAsStream(p_propertiesPath);
      Properties properties = new Properties();
      try
      {
         properties.load(stream);
      }
      catch (IOException e)
      {
         String msg = CustomActionResources.getFormattedString(CustomActionResources.UNABLE_TO_LOAD_PROPERTIES_1, Locale.getDefault(), new Object[]{e.getMessage()});              
         LOGGER.log(Level.WARNING, msg, e);
      }

      String actionsProperty = properties.getProperty(PROPERTY_ACTIONS, "");
      StringTokenizer tokenizer = new StringTokenizer(actionsProperty, ACTIONS_DELIMITER);
      List<String> actions = new ArrayList<String>();
      while (tokenizer.hasMoreTokens())
      {
         String className = tokenizer.nextToken();

         // Add to list of action class names
         actions.add(className);
         m_actionClassNames.add(className);
      }
      
      m_actionClassNamesArray = actions.toArray(new String[actions.size()]);
      
      if (isFiner)
      {
         LOGGER.exiting(LOG_CLASS, LOG_METHOD);
      }
   }

   /**
    * Get the factory name.
    * @return Factory name
    */
   public String getName()
   {
      // Use a unique name for the factory to avoid name conflicts
      return FACTORY_NAME;
   }

   /**
    * Get the display title for this factory.
    * @param locale Locale to display title in. 
    * @return Factory title
    */
   public String getTitle(Locale locale)
   {
      return CustomActionResources.getString(CustomActionResources.FACTORY_TITLE, locale);
   }

   /**
    * Get the registered action names.  Uses the class names for names and titles.
    * @return Array of action names.
    */
   public String[] getActionNames()
   {
      return m_actionClassNamesArray;
   }

   /**
    * Get the display title for the supplied action name
    * @param p_displayLocale Locale to display title in. 
    * @param p_actionName Action name.
    * @return Action display title.
    */
   public String getActionTitle(Locale locale, String actionName)
   {
      return CustomActionResources.getActionTitle(actionName, locale);
   }

   /**
    * Get the description for the supplied action name
    * @param locale Locale to display title in. 
    * @param actionName Action name.
    * @return Action display title.
    */
   public String getActionDescription(Locale locale, String actionName)
   {
      return CustomActionResources.getActionDescription(actionName, locale);
   }

   /**
    * Get the custom workflow action for the supplied action name.
    * Assumes that the supplied action name is the action class which
    * has a no-argument constructor.
    * @param actionName Assumed to be the class name
    * @param document Target document.  Ignored by this implementation.
    * @return Custom workflow action.  Null if the action could not be retrieved
    */
   public CustomWorkflowAction getAction(String actionName, Document document)
   {
      final String LOG_METHOD = "getAction(actionName, document)";
      boolean isFiner = LOGGER.isLoggable(LOG_LEVEL);
      if (isFiner)
      {
         LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] {actionName, document});
      }
      
      CustomWorkflowAction action = null;

      // Determine if the action name is a registered action class name
      if (m_actionClassNames.contains(actionName))
      {
         // Use reflection to create a new instance of the action class
         // using the no-argument constructor
         Object actionObject = null;
         try
         {
            Class actionClass = Class.forName(actionName);
            Constructor constructor = actionClass.getConstructor(new Class[]{});
            actionObject = constructor.newInstance(new Object[]{});

            if (actionObject instanceof CustomWorkflowAction)
            {
               action = (CustomWorkflowAction) actionObject;
            }
            else
            {
               // Action object does not implement CustomWorkflowAction interface
               System.err.println(ResourceBundleUtility.getFormattedString(
                  CustomActionResources.BUNDLE_NAME,
                  CustomActionResources.ACTION_DOES_NOT_IMPLEMENT_INTERFACE_1,
                  Locale.getDefault(),
                  new Object[] {actionName}
               ));
            }
         }
         catch (ClassNotFoundException e)
         {
            String msg = CustomActionResources.getFormattedString(
               CustomActionResources.ACTION_CLASS_NOT_FOUND_1, 
               Locale.getDefault(), 
               new Object[]{actionName});              
            LOGGER.log(Level.WARNING, msg, e);
         }
         catch (NoSuchMethodException e)
         {
            String msg = CustomActionResources.getFormattedString(
               CustomActionResources.ACTION_CONSTRUCTOR_NOT_FOUND_1, 
               Locale.getDefault(), 
               new Object[]{actionName});              
            LOGGER.log(Level.WARNING, msg, e);
         }
         catch (InstantiationException e)
         {
            String msg = CustomActionResources.getFormattedString(
               CustomActionResources.UNABLE_TO_INSTANTIATE_ACTION_2, 
               Locale.getDefault(), 
               new Object[]{actionName, e.getMessage()});              
            LOGGER.log(Level.WARNING, msg, e);
         }
         catch (IllegalAccessException e)
         {
            String msg = CustomActionResources.getFormattedString(
               CustomActionResources.UNABLE_TO_INSTANTIATE_ACTION_2, 
               Locale.getDefault(), 
               new Object[]{actionName, e.getMessage()});              
            LOGGER.log(Level.WARNING, msg, e);
         }
         catch (InvocationTargetException e)
         {
            String msg = CustomActionResources.getFormattedString(
               CustomActionResources.UNABLE_TO_INSTANTIATE_ACTION_2, 
               Locale.getDefault(), 
               new Object[]{actionName, e.getMessage()});              
            LOGGER.log(Level.WARNING, msg, e);
         }
      }
      else
      {
         String msg = CustomActionResources.getFormattedString(
            CustomActionResources.UNKNOWN_ACTION_NAME_1, 
            Locale.getDefault(), 
            new Object[]{actionName});              
         LOGGER.log(Level.WARNING, msg);
      }
      
      if (isFiner)
      {
         LOGGER.exiting(LOG_CLASS, LOG_METHOD, action);
      }

      return action;
   }
}
