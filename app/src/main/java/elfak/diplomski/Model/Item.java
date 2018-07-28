package elfak.diplomski.Model;

import io.realm.MutableRealmInteger;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Neca on 25.10.2017..
 */

public class Item extends RealmObject {
    @PrimaryKey
    @Required
    private String name;
    private String describe;
    private String rating;
    private byte[] image;
    private String price;
    private MenuCategory menuCategory;
    @LinkingObjects("item")
    private final RealmResults<CommentAndRating> foodCoomments = null;
    //private String priceCurrency;
    @Required
    private Boolean recommended;
    @Required
    private Boolean onDiscount;
    private String discountPrice;
    //@Required
    //public final MutableRealmInteger numberTimesOrdered = MutableRealmInteger.valueOf(0);
    private int numberTimesOrdered;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setDescribe(String describe)
    {
        this.describe = describe;
    }

    public String getDescribe()
    {
        return this.describe;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public String getRating()
    {
        return this.rating;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public byte[] getImage()
    {
        return this.image;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return this.price;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    public MenuCategory getMenuCategory() {
        return this.menuCategory;
    }

    public RealmResults<CommentAndRating> getFoodCoomments() {
        return foodCoomments;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public Boolean getOnDiscount() {
        return onDiscount;
    }

    public void setOnDiscount(Boolean onDiscount) {
        this.onDiscount = onDiscount;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getNumberTimesOrdered() {
        return numberTimesOrdered;
    }

    public void setNumberTimesOrdered(int numberTimesOrdered) {
        this.numberTimesOrdered = numberTimesOrdered;
    }

    /*public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }*/
}

