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
import org.kohsuke.stapler.QueryParameter;

import java.net.URL;

/**
 * Code for the build page.
 */
public class ApplitoolsBuildWrapper extends BuildWrapper implements Serializable {
    public final static String BATCH_NOTIFICATION_PATH = "/api/sessions/batches/%s/close/bypointerid";
    public String serverURL;
    public boolean notifyByCompletion;
    public String applitoolsApiKey;

    @DataBoundConstructor
    public ApplitoolsBuildWrapper(String serverURL, boolean notifyByCompletion, String applitoolsApiKey) {
        this.applitoolsApiKey = applitoolsApiKey;
        this.notifyByCompletion = notifyByCompletion;
        if (serverURL != null && !serverURL.isEmpty())
        {
            if (DescriptorImpl.validURL(serverURL))
            {
                this.serverURL = serverURL.trim();
            }
        } else {
            this.serverURL = ApplitoolsCommon.APPLITOOLS_DEFAULT_URL;
        }
    }

    @Override
    public Environment setUp(final AbstractBuild build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {

        runPreBuildActions(build, listener);

        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                ApplitoolsCommon.closeBatch(build, listener, serverURL, notifyByCompletion, applitoolsApiKey);
                return true;
            }

            @Override
            public void buildEnvVars(Map<String, String> env) {
                ApplitoolsCommon.buildEnvVariablesForExternalUsage(env, build, listener, serverURL, applitoolsApiKey);
            }
        };
    }

    private void runPreBuildActions(final Run build, final BuildListener listener) throws IOException, InterruptedException
    {
        listener.getLogger().println("Starting Applitools Eyes pre-build (server URL is '" + this.serverURL + "') apiKey is " + this.applitoolsApiKey);

        ApplitoolsCommon.integrateWithApplitools(build, this.serverURL, this.notifyByCompletion, this.applitoolsApiKey);

        listener.getLogger().println("Finished Applitools Eyes pre-build");
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<BuildWrapper> {
        public static String APPLITOOLS_DEFAULT_URL="https://eyes.applitools.com";
        public static boolean NOTIFY_BY_COMPLETION=true;

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
            return new ApplitoolsBuildWrapper(formData.getString("serverURL"), formData.getBoolean("notifyByCompletion"), formData.getString("applitoolsApiKey"));
        }
    }
}

