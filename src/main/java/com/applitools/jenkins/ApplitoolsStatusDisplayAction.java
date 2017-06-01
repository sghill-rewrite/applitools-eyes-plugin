package com.applitools.jenkins;

import hudson.Util;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import hudson.model.JobProperty;
import jenkins.tasks.SimpleBuildWrapper;
import org.jenkinsci.plugins.workflow.graph.FlowNode;


/**
 * Encapsulates the Applitools' status display action.
 */
public class ApplitoolsStatusDisplayAction extends AbstractApplitoolsStatusDisplayAction{
    private static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmss";
    private String projectName;
    private int buildNumber;
    private Calendar buildTimestamp;
    private String serverURL;
    private String iframeText;
    private static final Logger logger = Logger.getLogger(ApplitoolsStatusDisplayAction.class.getName());

    public ApplitoolsStatusDisplayAction(Run build) {
        this.projectName = build.getParent().getName();
        this.buildNumber = build.getNumber();
        this.buildTimestamp = build.getTimestamp();
        this.serverURL = null;

        for (Object property : build.getParent().getAllProperties()) {
            if (property instanceof ApplitoolsProjectConfigProperty) {
                this.serverURL = ((ApplitoolsProjectConfigProperty) property).getServerURL();
                break;
            }
        }
        this.iframeText = getIframeText();
    }


    @Override
    public String getIframeText() {
        try{
            String iframeURL = generateIframeURL();
            if (iframeURL == null)
            {
                // In case Applitools support has been removed from the project,
                // remove iframes from old reports
                return "";
            }

            return "<iframe id=\"frame\" src=\"" + iframeURL +
                    "\" style=\"overflow:hidden;overflow-x:hidden;overflow-y:hidden;height:710px;width:1024px;max-width:100%;resize:vertical;\"></iframe>\n";
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
        return generateBatchId(this.projectName, this.buildNumber, this.buildTimestamp);
    }

    private String generateIframeURL()
    {
        if (serverURL == null || serverURL.isEmpty())
        {
            // In case Applitools support has been removed from the project
            return null;
        }

        return serverURL + "/app/batchesnoauth/?startInfoBatchId=" + generateBatchId() + "&hideBatchList=true&intercom=false";
    }

    public static String generateBatchId(String projectName, int buildNumber, Calendar buildTimestamp)
    {
        final String BATCH_ID_PREFIX = "jenkins";
        SimpleDateFormat buildDate = new SimpleDateFormat(TIMESTAMP_PATTERN);
        buildDate.setTimeZone(buildTimestamp.getTimeZone());

        return BATCH_ID_PREFIX + "-" + projectName + "-" + buildNumber + "-" + buildDate.format(buildTimestamp.getTime());
    }


}

