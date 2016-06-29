package com.inffinix.plugins;

/**
 * Created by eduardo on 29/06/16.
 */
public interface ConfigurationTrackingDAO {
    public int insert( ConfigurationTracking ReceivedConfiguration );
    public ConfigurationTracking getConfig();
}
