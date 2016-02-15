package com.sci2015fair.distance;

import android.location.Location;

import java.util.Date;

/**
 * Created by Mitchell on 12/31/2015.
 */
public class LocationClusterObject {
    private Date firstDateTime;//date and time of the earliest Location in the cluster (clusterData[])
    private Date lastDateTime;//date and time of the latest (last) Location object in the cluster (clusterData[])
    private long avgLatitude;//latitude of coordinate calculated to be the weighted center of cluster
    private long avgLongitude;//longitude of weighted center coordinate
    private long closestLatitude;//latitude of real coordinate closest to calculated weighted center
    private long closestLongitude;//longitude of real coordinate closest to calculated weighted center
    private long furthestLatitude;//latitude of real coordinate farthest from calculated weighted center
    private long furthestLongitude;//longitude of real coordinate farthest from calculated weighted center
    private Location[] clusterData;//array stores all the Location objects that make up the cluster

    public LocationClusterObject(Location[] clusterData) {
        this.clusterData = clusterData;
    }


    public Date getFirstDateTime() {
        return firstDateTime;
    }

    public void setFirstDateTime(Date firstDateTime) {
        this.firstDateTime = firstDateTime;
    }

    public Date getLastDateTime() {
        return lastDateTime;
    }

    public void setLastDateTime(Date lastDateTime) {
        this.lastDateTime = lastDateTime;
    }

    public long getAvgLatitude() {
        return avgLatitude;
    }

    public void setAvgLatitude(long avgLatitude) {
        this.avgLatitude = avgLatitude;
    }

    public long getAvgLongitude() {
        return avgLongitude;
    }

    public void setAvgLongitude(long avgLongitude) {
        this.avgLongitude = avgLongitude;
    }

    public long getClosestLatitude() {
        return closestLatitude;
    }

    public void setClosestLatitude(long closestLatitude) {
        this.closestLatitude = closestLatitude;
    }

    public long getClosestLongitude() {
        return closestLongitude;
    }

    public void setClosestLongitude(long closestLongitude) {
        this.closestLongitude = closestLongitude;
    }

    public long getFurthestLatitude() {
        return furthestLatitude;
    }

    public void setFurthestLatitude(long furthestLatitude) {
        this.furthestLatitude = furthestLatitude;
    }

    public long getFurthestLongitude() {
        return furthestLongitude;
    }

    public void setFurthestLongitude(long furthestLongitude) {
        this.furthestLongitude = furthestLongitude;
    }

    public Location[] getClusterData() {
        return clusterData;
    }

    public void setClusterData(Location[] clusterData) {
        this.clusterData = clusterData;
    }
}
