package com.inffinix.plugins;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by eduardo on 01/07/16.
 */
public class FileToSend implements Serializable {
    private String filePath;
    private String fileName;
    private String server;
    private Map< String, String > parameters;

    public FileToSend() {
        filePath = null;
        fileName = null;
        server = null;
        parameters = null;
    }

    public FileToSend(String filePath, String fileName, String server, Map<String, String> parameters) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.server = server;
        this.parameters = parameters;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "FileToSend{" + "filePath=" + filePath + ", fileName=" + fileName + ", server=" + server + '}';
    }
}
