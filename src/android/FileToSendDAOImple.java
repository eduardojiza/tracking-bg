package com.inffinix.plugins;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eduardo on 01/07/16.
 */
public class FileToSendDAOImple implements FileToSendDAO {
    private static String PATH_FILE = "persistenceList.obj";
    private List< FileToSend > list;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Context context;

    public FileToSendDAOImple( Context context ){
        this.context = context;
        //crea archivo con arreglo vasio si no existe
        if( !fileExists() ) {
            System.out.println("--- No hay archivo ---");
            list = new ArrayList< FileToSend >();
            saveArray();
        } else {
            System.out.println("--- Si hay archivo ---");
        }
    }


    @Override
    public List<FileToSend> getAll() {
        return getArrayFromFile();
    }

    @Override
    public int insert(FileToSend file) {
        list = getArrayFromFile();
        list.add(file);
        saveArray();
        return 0;
    }

    @Override
    public int delete(FileToSend file) {
        String path = file.getFilePath();
        FileToSend temp;
        list = getArrayFromFile();
        int index = -1;
        for( int i = 0; i < list.size(); i++ ) {
            temp = list.get( i );
            if( path.equals( temp.getFilePath() ) ) {
                index = i;
                break;
            }
        }
        if( index != -1 ) {
            list.remove(index);
            saveArray();
        }
        return index;
    }


    private boolean fileExists() {
        File file = context.getFileStreamPath(PATH_FILE);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    private List<FileToSend> getArrayFromFile() {
        list = null;
        try {
            FileInputStream stream = context.openFileInput(PATH_FILE);
            input = new ObjectInputStream(stream);
            list = (List<FileToSend>)input.readObject();
            input.close();
            stream.close();

            File file = context.getFileStreamPath("persistenceList.obj");
            Log.d("------- size", "" + file.length());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private int saveArray() {
        int response = -1;
        try {
            FileOutputStream stream = context.openFileOutput( PATH_FILE, Context.MODE_PRIVATE );
            output = new ObjectOutputStream( stream );
            output.writeObject( list );
            output.close();
            stream.close();
            response = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
