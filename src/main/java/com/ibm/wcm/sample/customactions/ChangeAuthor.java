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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.workplace.wcm.api.Document;
import com.ibm.workplace.wcm.api.Editable;
import com.ibm.workplace.wcm.api.WCM_API;
import com.ibm.workplace.wcm.api.Workspace;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowAction;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowActionResult;
import com.ibm.workplace.wcm.api.custom.Directives;
import com.ibm.workplace.wcm.api.exceptions.OperationFailedException;
import com.ibm.workplace.wcm.api.exceptions.ServiceNotAvailableException;

/**
 * Replace all authors of the workflowed document with the current user.
 */
public class ChangeAuthor implements CustomWorkflowAction
{
   /** class name for the logger */
   private static final String LOG_CLASS = ChangeAuthor.class.getName();

   /** logging level */
   private static final Level LOG_LEVEL = Level.FINER;

   /** class logger */
   private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);
   
   /**
    * Get the date to run this action.
    */
   public Date getExecuteDate(Document aDocument)
   {
      return DATE_EXECUTE_NOW;
   }

   /**
    * Changes documents
    */
   public CustomWorkflowActionResult execute(Document aDocument)
   {
      final String LOG_METHOD = "execute(p_document)";      
      boolean isLogging = LOGGER.isLoggable(LOG_LEVEL);
      if (isLogging)
      {
         LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] {});
      }
      
      ActionResultBuilder builder = ActionResultBuilder.with(Directives.CONTINUE);

      if (aDocument instanceof Editable)
      {           
         try
         {
            // Preparations
            Workspace workspace = WCM_API.getRepository().getWorkspace();
            Editable editable = (Editable) aDocument;
            
            // Remove all other authors and set the current user as the author
            editable.removeAuthors(editable.getAuthors());
            editable.addAuthors(new String[]{workspace.getUserProfile().getCommonName()});    
         }
         catch (ServiceNotAvailableException snae)
         {
            builder.rollback(snae.getMessage());
         }
         catch (OperationFailedException ofe)
         {
            builder.rollback(ofe.getMessage());
         }        
      }

      if (isLogging)
      {
         LOGGER.exiting(LOG_CLASS, LOG_METHOD);
      }
      
      return builder.toResult();
   }
}
