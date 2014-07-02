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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility to retrieve messages from a resource bundle.
 */
public class ResourceBundleUtility
{
   /**
    * Retrieves a resource bundle string with substituted argument values using the
    * default locale in the JVM
    *
    * @param p_bundleName the name of the resource bundle, cannot be null
    * @param p_key the key of the string, cannot be null
    * @param p_locale Display locale
    * @param p_arguments an object array of values to substitute into the
    * string.  Cannot be null.  If there are fewer arguments than required,
    * the remaining values will display as "null".
    *
    * @return the resource bundle string with substituted values
    */
   public static final String getFormattedString(String p_bundleName,
                                                 String p_key,
                                                 Locale p_locale,
                                                 Object[] p_arguments)
   {
      Locale locale = p_locale;
      if (p_locale == null)
      {
         p_locale = Locale.getDefault();
      }

      String messagePattern = null;
      try
      {
         ResourceBundle bundle = ResourceBundle.getBundle(p_bundleName, locale);
         messagePattern = bundle.getString(p_key);
      }
      catch (MissingResourceException e)
      {
         // Ignore missing resource bundle or message
      }

      String message = null;
      if (messagePattern == null)
      {
         // No message pattern found - return a string in the format !p_key!p_arguments
         ArrayList argsList = new ArrayList();

         if (p_arguments != null)
         {
            for (int i = 0; i < p_arguments.length; i++)
            {
               argsList.add(p_arguments[i]);
            }
         }

         message = "!" + p_key + "! " + argsList;
      }
      else
      {
         // Format the message pattern to get the message
         MessageFormat formatter = new MessageFormat(messagePattern);
         formatter.setLocale(locale);
         message = formatter.format(p_arguments);
      }

      return message;
   }

   /**
    * Retrieves a resource bundle string with no argument values using the
    * default locale in the JVM
    *
    * @param p_bundleName the name of the resource bundle, cannot be null
    * @param p_key the key of the string, cannot be null
    * @param p_locale Display locale
    *
    * @return the resource bundle string
    */
   public static String getString(String p_bundleName, String p_key, Locale p_locale)
   {
      return getFormattedString(p_bundleName, p_key, p_locale, new Object[]{});
   }
}
