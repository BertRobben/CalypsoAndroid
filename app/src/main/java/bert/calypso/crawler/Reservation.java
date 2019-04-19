package bert.calypso.crawler;

import android.os.Parcel;
import android.os.Parcelable;

import bert.calypso.SimpleTime;

public class Reservation implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    private final String hall;
    private final String date;
    private final SimpleTime startTime;
    private final SimpleTime endTime;

    public Reservation(String hall, String date, SimpleTime startTime, SimpleTime endTime) {
        this.hall = hall;
        this.startTime = startTime;
        this.date = date;
        this.endTime = endTime;
    }

    public Reservation(Parcel in) {
        this(in.readString(), in.readString(), new SimpleTime(in), new SimpleTime(in));
    }

    public String getHall() {
        return hall;
    }

    public SimpleTime getStartTime() {
        return startTime;
    }

    public SimpleTime getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "hall='" + hall + '\'' +
                ", date='" + date + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public boolean canMerge(Reservation other) {
        return endTime.equals(other.startTime) && date.equals(other.date) && hall.equals(other.hall);
    }

    public Reservation merge(Reservation other) {
        if (canMerge(other)) {
            return new Reservation(hall, date, startTime, other.endTime);
        }
        throw new IllegalArgumentException("Can't merge " + this + " with " + other);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hall);
        dest.writeString(date);
        startTime.writeToParcel(dest, flags);
        endTime.writeToParcel(dest, flags);
    }
}
