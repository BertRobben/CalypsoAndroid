package bert.calypso.crawler;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Reservations implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Reservations createFromParcel(Parcel in) {
            return new Reservations(in);
        }

        public Reservations[] newArray(int size) {
            return new Reservations[size];
        }
    };

    private final List<Reservation> reservations;

    public Reservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Reservations(Parcel in) {
        reservations = new ArrayList<>();
        in.readTypedList(reservations, Reservation.CREATOR);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(reservations);
    }
}
