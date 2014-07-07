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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ibm.workplace.wcm.api.WebContentCustomWorkflowService;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowActionResult;
import com.ibm.workplace.wcm.api.custom.Directive;
import com.ibm.workplace.wcm.api.custom.DirectiveParams;
import com.ibm.workplace.wcm.api.custom.Directives;
import com.ibm.workplace.wcm.api.custom.RollbackDirectiveParams;

/**
 * Builder to help generate the CustomWorkflowActionResult that are returned to WCM
 * after executing the custom workflow actions.
 */
public class ActionResultBuilder
{
   /** Class name for the logger */
   private static final String LOG_CLASS = ActionResultBuilder.class.getName();

   /** Class logger */
   private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);
   
   /** The message to return to WCM */
   String message;
   
   /** The directive to return to WCM */
   Directive directive;
   
   /** The directive params that match the directive passed in. */
   DirectiveParams params;
   
   /** Custom Workflow Service JNDI name */
   private static final String WCM_CUSTOM_WORKFLOW_SERVICE = "portal:service/wcm/WebContentCustomWorkflowService";
   
   /** Reference to the WebContentCustomWorkflowService */
   private static WebContentCustomWorkflowService CUSTOM_WF_SERVICE;
   
   static
   {
      try
      {
         // Construct and inital Context
         InitialContext ctx = new InitialContext();
                  
         // Retrieve Custom Workflow Service
         CUSTOM_WF_SERVICE = (WebContentCustomWorkflowService) ctx.lookup(WCM_CUSTOM_WORKFLOW_SERVICE);

      }
      catch (NamingException ne)
      {
         // Exception retrieving Service
         LOGGER.log(Level.SEVERE, ne.getMessage(), ne);
      }
   }
   
   /**
    * Constructor
    * @param directive the initial directive. If none supplied defaults to CONTINUE
    */
   private ActionResultBuilder(Directive directive)
   {
      if (directive != null)
      {
         this.directive = directive;
      }
      else
      {
         directive = Directives.CONTINUE;
      }
   }
   
   /**
    * Create a new builder with the initial directive
    * 
    * @param directive the directive
    * 
    * @return new builder with the initial directive
    */
   public static final ActionResultBuilder with(Directive directive) 
   {
      return new ActionResultBuilder(directive);
   }
   
   /**
    * Set the directive to return to WCM. Null values will be ignored.
    * 
    * <p>Note if directive params have been set for a previous directive they will be cleared.
    * 
    * @param directive the directive to return to WCM
    * 
    * @return this builder
    */
   public ActionResultBuilder directive(Directive directive) 
   {
      if (directive != null)
      {    
         this.directive = directive;    
         
         checkParamsMatchDirectiveType();
      }
      return this;
   }

   /**
    * Checks that the directive params are valid for the current directive. If they are not
    * then they will be cleared.
    */
   private void checkParamsMatchDirectiveType()
   {
      if (params != null && directive != null)
      {
         DirectiveParams mockParams = directive.createDirectiveParams();
         if (mockParams != null && !mockParams.getClass().isAssignableFrom(this.params.getClass()))
         {           
            this.params = null;
         }
      }
   }
   
   /**
    * 
    * message description
    * @param message
    * @return this builder
    */
   public ActionResultBuilder message(String message) 
   {
      this.message = message;
      return this;
   }
   
   /**
    * Set the directive params to return to WCM. If the params 
    * are not compatible with the current directive then they will be ignored
    * 
    * @param params the directive params
    * 
    * @return this builder
    */
   public ActionResultBuilder params(DirectiveParams params) 
   {
      this.params = params;
      
      checkParamsMatchDirectiveType();
      
      return this;
   }
   
   /**
    * Set the message to return to WCM
    * 
    * @param message the message
    * 
    * @return this builder
    */
   public ActionResultBuilder rollback(String message)
   {
      this.directive = Directives.ROLLBACK_DOCUMENT;
      RollbackDirectiveParams params = (RollbackDirectiveParams) Directives.ROLLBACK_DOCUMENT.createDirectiveParams();
      params.setCustomErrorMsg(message);     
      return this;
   }
   
   /**
    * Returns the CustomWorkflowActionResult with the values set on this builder
    * 
    * @return CustomWorkflowActionResult with the values set on this builder
    */
   public CustomWorkflowActionResult toResult()
   {      
      // Create a result object
      return CUSTOM_WF_SERVICE.createResult(this.directive, this.message, this.params); 
   }
}

