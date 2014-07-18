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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.ibm.workplace.wcm.api.Document;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowAction;
import com.ibm.workplace.wcm.api.custom.CustomWorkflowActionResult;
import com.ibm.workplace.wcm.api.custom.Directives;

/**
 * Sample Email Custom Workflow Action
 * @author David de Vos
 */
public class EmailWorkflowAction implements CustomWorkflowAction
{
   /** The name of this class */
   private static final String s_CLASSNAME = EmailWorkflowAction.class.getName();
   /** The logger for this class */
   private static final Logger s_log = Logger.getLogger(s_CLASSNAME);
   
   /** The UTF-8 Encoding */
   public static final String UTF8 = "UTF-8";
   
   /** The mail server host name */
   private static final String s_MAIL_HOST = "[YOUR_MAIL_SERVER]";
   /** The mail 'from' address to use */   
   private static final String s_MAIL_FROM = "[YOUR_FROM_ADDRESS]";
	
   /**
    * Get the Date that this action should execute.  This method is always called prior to running the execute method.
    * @param document Target document.  Custom code must not modify the document in this method.
    * @return Execute date.  If date is in the past, the action will be executed immediately.  Use the
    *         DATE_EXECUTE_NOW constant to execute immediately.  If the date is in the future, the action
    *         will be scheduled for this date.  The returned execute date must be the same when run on any server where
    *         the action is syndicated.  If the execute date is different, the scheduled action will run at different times
    *         on different servers.
    */
   public Date getExecuteDate(Document p_document)
   {
      return DATE_EXECUTE_NOW;
   }   

   /**
    * Execute the action against the supplied document.  Changes to the document will be saved if the result does not
    * indicate a failure.  Changes to the document will be ignored if the result indicates a failure.
    * @param document Target document.  Custom code must not save or delete this document inside the execute method.
    *                 Custom code must not call any workflow methods against this document inside the execute method.
    *                 Use the approriate return code to trigger a workflow action.
    * @throws Throwable Any throwable that is thrown by this method will be treated as a failure result.
    * @return Result providing access to the outcome of the action and a message.  Special result codes can be used to trigger 
    *         workflow actions against the target document.  Returning null will be treated as a failure result.
    */
   public CustomWorkflowActionResult execute(Document p_document)
   {
      boolean isLoggingFiner = s_log.isLoggable(Level.FINER);
      if (isLoggingFiner)
      {
         s_log.entering(s_CLASSNAME, "execute");
      }
      
      ActionResultBuilder builder = ActionResultBuilder.with(Directives.CONTINUE);
      String message = "";
      
      // Setup email properties
      String subject = "[YOUR EMAIL SUBJECT]";
      String content = "[YOUR EMAIL CONTENT]";
      List<String> to = new ArrayList<String>();
      List<String> cc = new ArrayList<String>();
      List<String> bcc = new ArrayList<String>();
      
      // Send email
      try
      {
         sendEmail (s_MAIL_HOST, s_MAIL_FROM, to, cc, bcc, subject, content, UTF8);
         message = "Email sent successfully";
      }
      catch (Exception e)
      {
         message = "Error sending email, " + e;
      }

      builder.message(message);
      
      if (isLoggingFiner)
      {
         s_log.exiting(s_CLASSNAME, "execute");
      }
      return builder.toResult();
   }
   
   /**
    * Sends the specified message to the indicated people
    *
    * @param p_mailHost The name of the email server
    * @param p_from The from address of the email
    * @param p_to The list of people to send the email to
    * @param p_cc The list of people to cc the email to
    * @param p_bcc The list of people to bcc the email to
    * @param p_subject The subject of the email
    * @param p_content The message to send
    * @param p_charset The charset of the subject and message
    * @throws MessagingException If there is an exception creating or sending the email
    * @throws UnsupportedEncodingException If there is an exception processing the supplied charset
    */
   private static void sendEmail (String p_mailHost, String p_from, List<String> p_to, List<String> p_cc, List<String> p_bcc, String p_subject, String p_content, String p_charset)
      throws MessagingException, UnsupportedEncodingException
   {
      boolean isLoggingFiner = s_log.isLoggable(Level.FINER);
      boolean isLoggingFinest = s_log.isLoggable(Level.FINEST);
      if (isLoggingFiner)
      {
         s_log.entering(s_CLASSNAME, "sendEmail", new Object[] {p_mailHost, p_from, p_to, p_cc, p_bcc, p_subject, p_content, p_charset});
      }

      // Validate arguments
      if ((p_mailHost == null) || (p_mailHost.trim().length() <= 0))
      {
         throw new MessagingException("Error mail HOST not specified");
      }
      if ((p_from == null) || (p_from.trim().length() <= 0))
      {
         throw new MessagingException("Error mail FROM not specified");
      }

      // Strip any 'null' entries from TO list
      if (p_to != null)
      {
         Iterator<String> toIterator = p_to.iterator();
         while (toIterator.hasNext())
         {
            if (toIterator.next() == null)
            {
               toIterator.remove();
            }
         }
      }
      
      if ((p_to == null) || (p_to.isEmpty()))
      {
         throw new MessagingException("Error mail TO not specified");
      }

      // Strip any 'null' entries from CC list
      if (p_cc != null)
      {
         Iterator<String> ccIterator = p_cc.iterator();
         while (ccIterator.hasNext())
         {
            if (ccIterator.next() == null)
            {
               ccIterator.remove();
            }
         }
      }

      Properties props = new Properties();
      // Set the default transport type for this connection
      props.put("mail.smtp.host", p_mailHost);
      // Set sendpartial to continue sending the email where the address has some valid
      // and some invalid email addresses
      props.put("mail.smtp.sendpartial", "true");

      // Get connection to mail server
      javax.mail.Session mailConnection = javax.mail.Session.getInstance(props);

      // Create new MimeMessage
      MimeMessage msg = new MimeMessage(mailConnection);

      // Set From
      msg.setFrom(new InternetAddress(p_from));
      msg.setReplyTo(new Address[]{new InternetAddress(p_from)});
      // Set To
      Iterator<String> toIterator = p_to.iterator();
      while (toIterator.hasNext())
      {
         String emailAddress = toIterator.next();
         if (emailAddress != null)
         {
            msg.addRecipient(javax.mail.Message.RecipientType.TO,
               new InternetAddress(emailAddress));
         }
      }
      // Set CC
      if ((p_cc != null) && (p_cc.size() > 0))
      {
         Iterator<String> ccIterator = p_cc.iterator();
         while (ccIterator.hasNext())
         {
            String emailAddress = ccIterator.next();
            if (emailAddress != null)
            {
               msg.addRecipient(javax.mail.Message.RecipientType.CC,
                  new InternetAddress(emailAddress));
            }
         }
      }
      // Set BCC
      if ((p_bcc != null) && (p_bcc.size() > 0))
      {
         Iterator<String> bccIterator = p_bcc.iterator();
         while (bccIterator.hasNext())
         {
            String emailAddress = bccIterator.next();
            if (emailAddress != null)
            {
               msg.addRecipient(javax.mail.Message.RecipientType.BCC,
                  new InternetAddress(emailAddress));
            }
         }
      }

      // Set Subject
      if (p_subject != null)
      {
         msg.setSubject(p_subject, p_charset);
      }

      // Set Content ***
      if (p_content != null)
      {
         MimeBodyPart messageContent = new MimeBodyPart();
         messageContent.setText(p_content, p_charset);
         messageContent.setHeader( "Content-Transfer-Encoding","quoted-printable");
         MimeMultipart multipart = new MimeMultipart();
         multipart.addBodyPart(messageContent);
         msg.setContent(multipart);
      }

      // Save Settings in Mime Message
      if (isLoggingFinest)
      {
         s_log.log(Level.FINEST, "Send Email: Save Changes to Mime Message");
      }
      msg.saveChanges();

      // Send Email
      if (isLoggingFinest)
      {
         s_log.log(Level.FINEST, "Send Email: Actually send email");
      }
      Transport.send(msg);

      if (isLoggingFiner)
      {
         s_log.exiting(s_CLASSNAME, "sendEmail");
      }
   }
}
