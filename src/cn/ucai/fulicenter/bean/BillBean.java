package cn.ucai.fulicenter.bean;

/**
 * Created by Administrator on 2016/8/11.
 */
public class BillBean {
    private String recipient;
    private String phone;
    private String area;
    private String street;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "BillBean{" +
                "recipient='" + recipient + '\'' +
                ", phone='" + phone + '\'' +
                ", area='" + area + '\'' +
                ", street='" + street + '\'' +
                '}';
    }
}
