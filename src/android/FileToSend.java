package com.inffinix.plugins;

/**
 * Created by Eduardo_Jimenez on 14/07/2016.
 */
public class FileToSend {
    private int id;
    private String filePath;
    private String server;
    private String fileName;
    private String password;
    private String login;
    private String group;
    private String account;
    private String description;

    public FileToSend() {
    }

    public FileToSend(int id, String description, String account, String group, String login, String password, String server, String fileName, String filePath) {
        this.id = id;
        this.description = description;
        this.account = account;
        this.group = group;
        this.login = login;
        this.password = password;
        this.server = server;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FileToSend{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", server='" + server + '\'' +
                ", fileName='" + fileName + '\'' +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", group='" + group + '\'' +
                ", account='" + account + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
