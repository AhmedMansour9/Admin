package bekya.admin;

/**
 * Created by kunda on 10/2/2017.
 */

public class Category {
    private String catogories;
    private String img;

    public Category() {

    }

    public Category(String catogories, String img) {
        this.catogories = catogories;
        this.img = img;
    }

    public String getCatogories() {
        return catogories;
    }

    public void setCatogories(String catogories) {
        this.catogories = catogories;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
