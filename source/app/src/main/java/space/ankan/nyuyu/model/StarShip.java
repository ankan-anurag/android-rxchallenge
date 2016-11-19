package space.ankan.nyuyu.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ankan.
 * TODO: Add a class comment
 */

public class StarShip implements Serializable, Comparable<StarShip> {

    public String name;
    public List<String> films;
    public String cost_in_credits;
    private String boundedFilmText;

    private long getCost() {
        try {
            return Long.valueOf(cost_in_credits);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    @Override
    public int compareTo(@NonNull StarShip that) {
        //descending order
        return Long.valueOf(that.getCost()).compareTo(this.getCost());
    }

    public String getBoundedFilmText() {
        return boundedFilmText;
    }

    public void setBoundedFilmText(String boundedFilmText) {
        this.boundedFilmText = boundedFilmText;
    }
}
