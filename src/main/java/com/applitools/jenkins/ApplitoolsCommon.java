package com.applitools.jenkins;

import hudson.model.BuildListener;
import hudson.model.JobProperty;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.DeleteMethod;

import java.io.IOException;
import java.util.Map;

/**
 * Common methods to be used other parts of the plugin
 */
public class ApplitoolsCommon {

    public final static String APPLITOOLS_DEFAULT_URL = "https://eyes.applitools.com";
    public final static boolean NOTIFY_BY_COMPLETION = true;
    public final static String BATCH_NOTIFICATION_PATH = "/api/sessions/batches/%s/close/bypointerid";


    public static void integrateWithApplitools(Run run, String serverURL, boolean notifyByCompletion, String applitoolsApiKey
    ) throws IOException
    {
        updateProjectProperties(run, serverURL, notifyByCompletion, applitoolsApiKey);
        addApplitoolsActionToBuild(run);
        run.save();
    }
    private static void updateProjectProperties(Run run, String serverURL, boolean notifyByCompletion, String applitoolsApiKey
                                               ) throws IOException
    {
        boolean found = false;
        for (Object property:run.getParent().getAllProperties())
        {
            if (property instanceof ApplitoolsProjectConfigProperty)
            {
                ((ApplitoolsProjectConfigProperty)property).setServerURL(serverURL);
                ((ApplitoolsProjectConfigProperty)property).setNotifyByCompletion(notifyByCompletion);
                ((ApplitoolsProjectConfigProperty)property).setApiAccess(applitoolsApiKey);
                found = true;
                break;
            }
        }
        if (!found)
        {
            JobProperty jp = new ApplitoolsProjectConfigProperty(serverURL, notifyByCompletion, applitoolsApiKey);
            run.getParent().addProperty(jp);
        }
        run.getParent().save();
    }

    private static void addApplitoolsActionToBuild(final Run build)
    {
        ApplitoolsStatusDisplayAction buildAction = build.getAction(ApplitoolsStatusDisplayAction.class);
        if (buildAction == null) {
            buildAction = new ApplitoolsStatusDisplayAction(build);
            build.addAction(buildAction);
        }
    }

    public static void buildEnvVariablesForExternalUsage(Map<String, String> env, final Run build, final TaskListener listener, String serverURL, String apiAccess)
    {
        String projectName = build.getParent().getDisplayName();
        String batchId = ApplitoolsStatusDisplayAction.generateBatchId(projectName, build.getNumber(), build.getTimestamp());
        String batchName = projectName;
        ApplitoolsEnvironmentUtil.outputVariables(listener, env, serverURL, batchName, batchId, projectName, apiAccess);
    }

    public static void closeBatch(Run run, TaskListener listener, String serverURL, boolean notifyByCompletion, String apiAccess) throws IOException {
        if (notifyByCompletion && apiAccess != null && !apiAccess.isEmpty()) {
            String batchId = ApplitoolsStatusDisplayAction.generateBatchId(run.getParent().getDisplayName(), run.getNumber(), run.getTimestamp());
            HttpClient httpClient = new HttpClient();
            URI targetUrl = new URI(serverURL, false);
            targetUrl.setPath(String.format(BATCH_NOTIFICATION_PATH, batchId));
            targetUrl.setQuery("apiKey=" + apiAccess);

            DeleteMethod deleteRequest = new DeleteMethod(targetUrl.toString());
            try {
                listener.getLogger().println(String.format("Batch notification called with %s", batchId));
                int statusCode = httpClient.executeMethod(deleteRequest);
                listener.getLogger().println("Delete batch is done with " + Integer.toString(statusCode) + " status");
            } finally {
                deleteRequest.releaseConnection();
            }

        }

    }
}
