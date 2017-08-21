package com.getbooks.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by marinaracu on 15.08.17.
 */

public class UserSession {

    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("customer_firstname")
    @Expose
    private String customerFirstname;
    @SerializedName("customer_lastname")
    @Expose
    private String customerLastname;
    @SerializedName("customer_email")
    @Expose
    private String customerEmail;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerFirstname() {
        return customerFirstname;
    }

    public void setCustomerFirstname(String customerFirstname) {
        this.customerFirstname = customerFirstname;
    }

    public String getCustomerLastname() {
        return customerLastname;
    }

    public void setCustomerLastname(String customerLastname) {
        this.customerLastname = customerLastname;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

}
