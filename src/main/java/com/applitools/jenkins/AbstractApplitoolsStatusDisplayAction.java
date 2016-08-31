package com.applitools.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Action;

/**
 * Created by addihorowitz on 8/11/16.
 */
public abstract class AbstractApplitoolsStatusDisplayAction implements Action {

    public abstract String getIframeText();

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }

    public abstract AbstractBuild<?, ?> getBuild();
}
