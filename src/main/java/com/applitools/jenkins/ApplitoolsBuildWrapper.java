package com.applitools.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import hudson.util.FormValidation;
import hudson.model.JobProperty;
import org.kohsuke.stapler.QueryParameter;
import java.net.URL;
/**
 * Code for the build page.
 */

public class ApplitoolsBuildWrapper extends BuildWrapper implements Serializable {
    public String serverURL = DescriptorImpl.APPLITOOLS_DEFAULT_URL;

    @DataBoundConstructor
    public ApplitoolsBuildWrapper(String serverURL) {
        if (serverURL != null && !serverURL.isEmpty())
        {
            if (DescriptorImpl.validURL(serverURL))
            {
                this.serverURL = serverURL.trim();
            }
        } else {
            this.serverURL = DescriptorImpl.APPLITOOLS_DEFAULT_URL;
        }
    }

    @Override
    public Environment setUp(final AbstractBuild build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {

        runPreBuildActions(build, listener);

        return new Environment() {

            @Override
            public void buildEnvVars(Map<String, String> env) {
                buildEnvVariablesForExternalUsage(env, build, listener);
            }
        };
    }

    private void runPreBuildActions(final AbstractBuild build, final BuildListener listener) throws IOException, InterruptedException
    {
        listener.getLogger().println("Starting Applitools Eyes pre-build (server URL is '" + this.serverURL + "')");

        updateProjectProperties(build);
        addApplitoolsActionToBuild(build);
        build.save();

        listener.getLogger().println("Finished Applitools Eyes pre-build");
    }

    private void buildEnvVariablesForExternalUsage(Map<String, String> env, final AbstractBuild build, final BuildListener listener)
    {
        String batchId = ApplitoolsStatusDisplayAction.generateBatchId(build.getProject().getDisplayName(), build.getNumber(), build.getTimestamp());
        String batchName = build.getProject().getDisplayName();
        ApplitoolsEnvironmentUtil.outputVariables(listener, env, serverURL, batchName, batchId);
    }

    private void updateProjectProperties(final AbstractBuild build) throws IOException
    {
        boolean found = false;
        for (Object property:build.getProject().getAllProperties())
        {
            if (property instanceof ApplitoolsProjectConfigProperty)
            {
                ((ApplitoolsProjectConfigProperty)property).setServerURL(this.serverURL);
                found = true;
                break;
            }
        }
        if (!found)
        {
            JobProperty jp = new ApplitoolsProjectConfigProperty(this.serverURL);
            build.getProject().addProperty(jp);
        }
        build.getProject().save();
    }

    private void addApplitoolsActionToBuild(final AbstractBuild build)
    {
        ApplitoolsStatusDisplayAction buildAction = build.getAction(ApplitoolsStatusDisplayAction.class);
        if (buildAction == null) {
            buildAction = new ApplitoolsStatusDisplayAction(build);
            build.addAction(buildAction);
        }
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<BuildWrapper> {
        public static final String APPLITOOLS_DEFAULT_URL = "https://eyes.applitools.com";
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // indicates that this builder can be used with all kinds of project types
            return true;
        }

        protected static boolean validURL(String url)
        {
            // Just making sure the URL is valid.
            try {
                new URL(url);
            } catch (Exception ex) {
                return false;
            }
            return true;
        }

        public FormValidation doCheckServerURL(@QueryParameter String value)
        {
            if (validURL(value))
            {
                return FormValidation.ok();
            }
            else
            {
                return FormValidation.error("Not a valid URL. Please make sure to use the following format https://<server>");
            }
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Applitools Support";
        }

        @Override
        public BuildWrapper newInstance(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
            return new ApplitoolsBuildWrapper(formData.getString("serverURL"));
        }
    }
}

