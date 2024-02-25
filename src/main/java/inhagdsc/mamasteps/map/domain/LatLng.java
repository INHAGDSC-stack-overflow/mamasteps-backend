package inhagdsc.mamasteps.map.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LatLng implements Cloneable {
    double latitude;
    double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLng latLng = (LatLng) o;
        return Double.compare(latLng.latitude, latitude) == 0 &&
                Double.compare(latLng.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    public static <T extends LatLng> List<T> deepCopyList(List<T> originalList) {
        List<T> copiedList = new ArrayList<>();
        for (T item : originalList) {
            copiedList.add((T) item.clone());
        }
        return copiedList;
    }
    @Override
    public LatLng clone() {
        try {
            return (LatLng) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}