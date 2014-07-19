Sample Custom Workflow Actions

This sample includes a set of custom workflow actions packaged in an enterprise application that can be deployed onto your portal server. 

Deploying:
1. Login to the WebSphere administration console.
2. Navigate to "Application / New Applications" and install the CustomWorkflowActions.ear to the WebSphere_Portal server instance. 
3. No special configuration is needed for the EAR. Work through all the install steps using the default. Note: Ensure that the application is deployed to the WebSphere_Portal server.
4. Ensure that the application is running. If not, start it.

Configuration the Authoring UI:
1. Login to WebSphere Portal and navigate to the WCM Authoring Portlet
2. Create a new "Workflow Action > Custom Action"
3. Click "Select Action" to select from the deployed custom actions

Troubleshooting:
Q: The error "An error occurred rendering the custom JSP." is shown under the text area.
A: Check that the web application has been started. Consult the logs for any errors.

Extending the integration:
The download package includes all source files necessary to build the enterprise application.

The build has a number of dependencies on Portal and WCM libraries. These are listed in the build.xml
