package elfak.diplomski.Model;

import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Neca on 7.11.2017..
 */

public class MenuCategory extends RealmObject {

    @PrimaryKey
    @Required
    private String category;
    private byte[] image;
    private String sortNumber;
    @Required
    private Boolean visibility;
    private String categoryType;
    @Required
    private Integer numberOfItemsInBestSeller;

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public String getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(String sortNumber) {
        this.sortNumber = sortNumber;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public Integer getNumberOfItemsInBestSeller() {
        return numberOfItemsInBestSeller;
    }

    public void setNumberOfItemsInBestSeller(Integer numberOfItemsInBestSeller) {
        this.numberOfItemsInBestSeller = numberOfItemsInBestSeller;
    }
}
