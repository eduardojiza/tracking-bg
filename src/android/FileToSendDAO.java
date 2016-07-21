package com.inffinix.plugins;

import java.util.List;

/**
 * Created by eduardo on 01/07/16.
 */
public interface FileToSendDAO {
    public List<FileToSend> getAll();
    public void insert(FileToSend file);
    public void delete(int id);
}