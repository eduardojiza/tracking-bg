package com.inffinix.plugins;

import java.util.List;

/**
 * Created by eduardo on 01/07/16.
 */
public interface FileToSendDAO {
    public List<FileToSend> getAll();
    public int insert(FileToSend file);
    public int delete(FileToSend file);
}