package com.applitools.jenkins;

import hudson.model.BuildListener;
import hudson.model.TaskListener;

import java.util.Map;

/**
 * Utility class for environment variables.
 */
public class ApplitoolsEnvironmentUtil {

    public static void outputVariables(final TaskListener listener, Map<String, String> env, String serverURL, String batchName, String batchId, String projectName) {
        final String APPLITOOLS_BATCH_NAME = "BATCH_NAME";
        final String APPLITOOLS_BATCH_ID = "BATCH_ID";
        final String APPLITOOLS_PROJECT_SERVER_URL = "PROJECT_SERVER_URL";
        final String APPLITOOLS_BATCH_SEQUENCE = "BATCH_SEQUENCE";
        final String APPLITOOLS_DONT_CLOSE_BATCHES = "DONT_CLOSE_BATCHES";
        final String TRUE_VALUE = "true";

        listener.getLogger().println("Creating Applitools environment variables:");

        outputEnvironmentVariable(listener, env, APPLITOOLS_DONT_CLOSE_BATCHES, TRUE_VALUE, true);

        if (serverURL != null && !serverURL.isEmpty()) {
            outputEnvironmentVariable(listener, env, APPLITOOLS_PROJECT_SERVER_URL, serverURL, true);
        }
        if (batchId != null && !batchId.isEmpty()) {
            outputEnvironmentVariable(listener, env, APPLITOOLS_BATCH_ID, batchId, true);
        }

        if (batchName != null && !batchName.isEmpty()) {
            outputEnvironmentVariable(listener, env, APPLITOOLS_BATCH_NAME, batchName, true);
        }

        if (projectName != null) {
            outputEnvironmentVariable(listener, env, APPLITOOLS_BATCH_SEQUENCE, projectName, true);
        }
    }

    public static void outputEnvironmentVariable(final TaskListener listener, Map<String, String> env, String key, String value, boolean overwrite) {
        String prefix = "APPLITOOLS_";

        if (env.get(key) == null || overwrite) {
            String keyName = prefix + key;
            env.put(keyName, value);
            listener.getLogger().println(keyName + " = '" + value + "'");
        }
    }

}
