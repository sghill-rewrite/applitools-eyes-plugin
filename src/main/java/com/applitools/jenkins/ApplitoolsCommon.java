package com.applitools.jenkins;

import hudson.model.BuildListener;
import hudson.model.JobProperty;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.io.IOException;
import java.util.Map;

/**
 * Common methods to be used other parts of the plugin
 */
public class ApplitoolsCommon {

    public final static String APPLITOOLS_DEFAULT_URL = "https://eyes.applitools.com";

    public static void integrateWithApplitools(Run run, String serverURL
    ) throws IOException
    {
        updateProjectProperties(run, serverURL);
        addApplitoolsActionToBuild(run);
        run.save();
    }
    private static void updateProjectProperties(Run run, String serverURL
                                               ) throws IOException
    {
        boolean found = false;
        for (Object property:run.getParent().getAllProperties())
        {
            if (property instanceof ApplitoolsProjectConfigProperty)
            {
                ((ApplitoolsProjectConfigProperty)property).setServerURL(serverURL);
                found = true;
                break;
            }
        }
        if (!found)
        {
            JobProperty jp = new ApplitoolsProjectConfigProperty(serverURL);
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

    public static void buildEnvVariablesForExternalUsage(Map<String, String> env, final Run build, final TaskListener listener, String serverURL)
    {
        String projectName = build.getParent().getDisplayName();
        String batchId = ApplitoolsStatusDisplayAction.generateBatchId(projectName, build.getNumber(), build.getTimestamp());
        String batchName = projectName;
        ApplitoolsEnvironmentUtil.outputVariables(listener, env, serverURL, batchName, batchId, projectName);
    }


}
