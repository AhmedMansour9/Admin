package bekya.admin;

/**
 * Created by HP on 04/06/2018.
 */

public class Retrivedata {
    private String img1;
    private String img2;
    private String img3;
   private String img4;
   private String name;
   private String discrption;
   private String discount;
   private String phone;
   private String date;
   private String token;
   private String key;
   private Boolean Admin;
    public Retrivedata(){}

    public Retrivedata(String img1, String img2, String img3, String img4, String name, String discrption, String discount, String phone, String date, String token) {
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.name = name;
        this.discrption = discrption;
        this.discount = discount;
        this.phone = phone;
        this.date = date;
        this.token = token;
    }

    public Boolean getAdmin() {
        return Admin;
    }

    public void setAdmin(Boolean admin) {
        Admin = admin;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscrption() {
        return discrption;
    }

    public void setDiscrption(String discrption) {
        this.discrption = discrption;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
