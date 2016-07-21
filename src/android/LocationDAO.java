package com.inffinix.plugins;

import java.util.List;

/**
 * Created by Eduardo_Jimenez on 18/07/2016.
 */
public interface LocationDAO {
    public void insert(Location location);
    public void delete(int id);
    public List<Location> getAll();
}
