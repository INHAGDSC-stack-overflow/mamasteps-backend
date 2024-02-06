package inhagdsc.mamasteps.map.domain;

import java.util.ArrayList;
import java.util.List;

public class LatLng implements Cloneable {
    double latitude;
    double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}