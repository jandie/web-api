package securoserve.library;

/**
 * This class contains location information.
 * Created by Jandie on 13-3-2017.
 */
public class Location {
    private double longitude;
    private double latitude;
    private double radius;
    private int id;

    /***
     * the constructor for a new location .
     * @param longitude the longitude of the location.
     * @param latitude the latutude of the location.
     * @param radius the radius of the location, can be 0.
     */
    public Location(int id, double latitude, double longitude, double radius) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.id = id;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}