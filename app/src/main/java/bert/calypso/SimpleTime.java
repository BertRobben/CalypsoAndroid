package bert.calypso;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class SimpleTime implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SimpleTime createFromParcel(Parcel in) {
            return new SimpleTime(in);
        }

        public SimpleTime[] newArray(int size) {
            return new SimpleTime[size];
        }
    };

    private final int hours;
    private final int minutes;

    public SimpleTime(int hours, int minutes) {
        this.hours = hours + (minutes/60);
        this.minutes = minutes % 60;
    }

    public SimpleTime(Parcel in) {
        this(in.readInt(), in.readInt());
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public SimpleTime plusMinutes(int minutes) {
        return new SimpleTime(hours, this.minutes + minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleTime that = (SimpleTime) o;
        return hours == that.hours &&
                minutes == that.minutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hours, minutes);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getHours());
        dest.writeInt(getMinutes());
    }
}
