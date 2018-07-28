package elfak.diplomski.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Neca on 28.11.2017..
 */

public class CommentAndRating extends RealmObject {

    @PrimaryKey
    @Required
    private String usernameAndFood;
    private String dateAndTime;
    private String comment;
    private double rating;
    private User user;
    private Item item;

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getUsernameAndFood() {
        return usernameAndFood;
    }

    public void setUsernameAndFood(String usernameAndFood) {
        this.usernameAndFood = usernameAndFood;
    }
}
