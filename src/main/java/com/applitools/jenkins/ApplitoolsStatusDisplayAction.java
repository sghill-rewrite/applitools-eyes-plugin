package com.applitools.jenkins;

/**
 * Created by addihorowitz on 8/11/16.
 */
import hudson.model.AbstractBuild;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import hudson.model.JobProperty;

public class ApplitoolsStatusDisplayAction extends AbstractApplitoolsStatusDisplayAction {
    private static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmss";
    private final AbstractBuild<?, ?> build;
    private String iframeText;
    private static final Logger logger = Logger.getLogger(ApplitoolsStatusDisplayAction.class.getName());

    public ApplitoolsStatusDisplayAction(AbstractBuild<?, ?> build) {
        this.build = build;
        this.iframeText = getIframeText();
    }

    @Override
    public String getIframeText() {
        try{
            String serverURL = generateIframeURL();
            if (serverURL == null)
            {
                // In case Applitools support has been removed from the project,
                // remove iframes from old reports
                return "";
            }

            return "<iframe id=\"frame\" src=\"" + serverURL +
                    "\" style=\"overflow:hidden;overflow-x:hidden;overflow-y:hidden;height:600px;width:1024px;max-width:100%;resize:vertical;\"></iframe>\n";
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            logger.warning(sw.toString());
            return "";
        }
    }

    private String generateBatchId()
    {
        return generateBatchId(getBuild().getProject().getName(), getBuild().getNumber() , getBuild().getTimestamp());
    }

    private String generateIframeURL()
    {
        String serverURL = null;

        for (JobProperty property : getBuild().getProject().getAllProperties()) {
            if (property instanceof ApplitoolsProjectConfigProperty) {
                serverURL = ((ApplitoolsProjectConfigProperty) property).getServerURL();
                break;
            }
        }
        if (serverURL == null || serverURL.isEmpty())
        {
            // In case Applitools support has been removed from the project
            return null;
        }
        return serverURL + "/app/batchesnoauth/?startInfoBatchId=" + generateBatchId() + "&hideBatchList=true&intercom=false";
    }

    @Override
    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    public static String generateBatchId(String projectName, int buildNumber, Calendar buildTimestamp)
    {
        final String BATCH_ID_PREFIX = "jenkins";
        SimpleDateFormat buildDate = new SimpleDateFormat(TIMESTAMP_PATTERN);
        buildDate.setTimeZone(buildTimestamp.getTimeZone());

        return BATCH_ID_PREFIX + "-" + projectName + "-" + buildNumber + "-" + buildDate.format(buildTimestamp.getTime());
    }
}

