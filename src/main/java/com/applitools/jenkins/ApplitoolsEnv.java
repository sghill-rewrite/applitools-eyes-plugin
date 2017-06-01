package com.applitools.jenkins;
import java.io.Serializable;
/**
 * Created by addihorowitz on 5/20/17.
 */
public class ApplitoolsEnv implements Serializable{
    public String serverURL = "https://eyes.applitools.com";

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        if (serverURL != null && !serverURL.isEmpty())
            this.serverURL = serverURL;
    }

}
