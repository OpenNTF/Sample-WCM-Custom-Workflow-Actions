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

import java.util.Locale;

public class CustomActionResources
{
   public static final String BUNDLE_NAME = "com.ibm.wcm.sample.customactions.CustomActionResources";

   /** Prefix used for the title property. */
   public static final String PROPERTY_PREFIX_TITLE = "title.";

   /** Prefix used for the description property. */
   public static final String PROPERTY_PREFIX_DESCRIPTION = "description.";  

   public static final String FACTORY_TITLE = "FACTORY_TITLE";
   
   public static final String UNKNOWN_ACTION_NAME_1 = "UNKNOWN_ACTION_NAME_1";
   public static final String UNABLE_TO_LOAD_PROPERTIES_1 = "UNABLE_TO_LOAD_PROPERTIES_1";
   public static final String ACTION_CLASS_NOT_FOUND_1 = "ACTION_CLASS_NOT_FOUND_1";
   public static final String ACTION_CONSTRUCTOR_NOT_FOUND_1 = "ACTION_CONSTRUCTOR_NOT_FOUND_1";
   public static final String UNABLE_TO_INSTANTIATE_ACTION_2 = "UNABLE_TO_INSTANTIATE_ACTION_2";
   public static final String ACTION_DOES_NOT_IMPLEMENT_INTERFACE_1 ="ACTION_DOES_NOT_IMPLEMENT_INTERFACE_1";

   public static final String CONFIG_FACTORY_NAME_1 = "CONFIG_FACTORY_NAME_1";
   public static final String CONFIG_ACTION_NAMES = "CONFIG_ACTION_NAMES";

   public static final String ACTION_MSG_SUCCESS = "ACTION_MSG_SUCCESS";
   public static final String ACTION_MSG_CATEGORY_NOT_FOUND_2 = "ACTION_MSG_CATEGORY_NOT_FOUND_2";
   public static final String ACTION_MSG_CATEGORY_NOT_DEFINED = "ACTION_MSG_CATEGORY_NOT_DEFINED";
   public static final String ACTION_MSG_TARGET_NOT_CONTENT = "ACTION_MSG_TARGET_NOT_CONTENT";
   
   /**
    * Returns the title corresponding to the action supplied. 
    * 
    * @param actionClassName the custom workflow action class name
    * @param locale the locale
    * 
    * @return the title corresponding to the action supplied.
    */
   public static String getActionTitle(String actionClassName, Locale locale) 
   {
      return getString(PROPERTY_PREFIX_TITLE + actionClassName, locale);
   }
   
   /**
    * Returns the description corresponding to the action supplied. 
    * 
    * @param actionClassName the custom workflow action class name
    * @param locale the locale
    * 
    * @return the description corresponding to the action supplied.
    */
   public static String getActionDescription(String actionClassName, Locale locale) 
   {
      return getString(PROPERTY_PREFIX_DESCRIPTION + actionClassName, locale);
   }
   
   /**
    * Retrieves the resource bundle string with substituted argument values from 
    * the CustomActionResources bundle.
    *
    * @param resourceKey the key of the string, cannot be null
    * @param locale Display locale
    * @param arguments an object array of values to substitute into the
    * string.  Cannot be null.  If there are fewer arguments than required,
    * the remaining values will display as "null".
    *
    * @return the resource bundle string with substituted values
    */
   public static String getFormattedString(String resourceKey, Locale locale, Object[] arguments)
   {
      return ResourceBundleUtility.getFormattedString(
         CustomActionResources.BUNDLE_NAME,
         resourceKey,
         locale,
         arguments
      );
   }
    
   /**
    * Retrieves a resource bundle string with no argument values from 
    * the CustomActionResources bundle.
    *
    * @param resourceKey the key of the string, cannot be null
    * @param locale Display locale
    *
    * @return the resource bundle string
    */
   public static String getString(String resourceKey, Locale locale)
   {
      return ResourceBundleUtility.getString(
         CustomActionResources.BUNDLE_NAME,
         resourceKey,
         locale
      );
   }
}
