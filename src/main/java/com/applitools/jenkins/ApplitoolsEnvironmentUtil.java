package com.applitools.jenkins;

import hudson.model.BuildListener;

import java.util.Map;

/**
 * Created by addihorowitz on 8/28/16.
 */
public class ApplitoolsEnvironmentUtil {

    public static void outputVariables(final BuildListener listener, Map<String, String> env, String serverURL, String batchId) {
        final String APPLITOOLS_BATCH_ID = "BATCH_ID";
        final String APPLITOOLS_PROJECT_SERVER_URL = "PROJECT_SERVER_URL";

        listener.getLogger().println("Creating Applitools environment variables:");

        if (serverURL != null && !serverURL.isEmpty()) {
            outputEnvironmentVariable(listener, env, APPLITOOLS_PROJECT_SERVER_URL, serverURL, true);
        }
        if (batchId != null && !batchId.isEmpty()) {
            outputEnvironmentVariable(listener, env, APPLITOOLS_BATCH_ID, batchId, true);
        }
    }

    public static void outputEnvironmentVariable(final BuildListener listener, Map<String, String> env, String key, String value, boolean overwrite) {
        String prefix = "APPLITOOLS_";

        if (env.get(key) == null || overwrite) {
            String keyName = prefix + key;
            env.put(keyName, value);
            listener.getLogger().println(keyName + " = '" + value + "'");
        }
    }
}
