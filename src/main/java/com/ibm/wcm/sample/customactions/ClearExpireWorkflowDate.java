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
import com.ibm.workplace.wcm.api.WorkflowedDocument;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowAction;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowActionResult;
import com.ibm.workplace.wcm.api.custom.Directives;
import com.ibm.workplace.wcm.api.exceptions.WorkflowNotFoundException;

/**
 * Clears the workflowed items Expire date when executed. 
 */
public class ClearExpireWorkflowDate implements CustomWorkflowAction
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
   public Date getExecuteDate(Document p_document)
   {
      return DATE_EXECUTE_NOW;
   }

   /**
    * @see com.ibm.workplace.wcm.api.custom.CustomWorkflowAction#execute(com.ibm.workplace.wcm.api.Document)
    */
   public CustomWorkflowActionResult execute(Document p_document)
   {
      final String LOG_METHOD = "execute(p_document)";      
      boolean isLogging = LOGGER.isLoggable(LOG_LEVEL);
      if (isLogging)
      {
         LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] {});
      }
      
      ActionResultBuilder builder = ActionResultBuilder.with(Directives.CONTINUE);

      try 
      {
         // Modify title of this content
         if (p_document instanceof WorkflowedDocument)
         {
            WorkflowedDocument workflowedDocument = (WorkflowedDocument) p_document;
            
            // Reset the expire date
            workflowedDocument.setExpiryDate(null);
         }
      }
      catch (WorkflowNotFoundException fnfe)
      {
         builder.rollback(fnfe.getMessage()).message(fnfe.getMessage());
      }
      
      if (isLogging)
      {
         LOGGER.exiting(LOG_CLASS, LOG_METHOD);
      }
      
      return builder.toResult();
   }
}
