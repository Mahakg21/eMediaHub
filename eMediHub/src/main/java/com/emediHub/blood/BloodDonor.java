package com.emediHub.blood;

public class BloodDonor {
    private int id;
    private String name;
    private String bloodGroup;
    private String phone;
    private String city;
    private boolean available;

    public BloodDonor() {}

    public BloodDonor(int id, String name, String bloodGroup,
                      String phone, String city, boolean available) {
        this.id         = id;
        this.name       = name;
        this.bloodGroup = bloodGroup;
        this.phone      = phone;
        this.city       = city;
        this.available  = available;
    }

    public int     getId()          { return id; }
    public String  getName()        { return name; }
    public String  getBloodGroup()  { return bloodGroup; }
    public String  getPhone()       { return phone; }
    public String  getCity()        { return city; }
    public boolean isAvailable()    { return available; }

    public void setId(int id)                 { this.id = id; }
    public void setName(String name)          { this.name = name; }
    public void setBloodGroup(String bg)      { this.bloodGroup = bg; }
    public void setPhone(String phone)        { this.phone = phone; }
    public void setCity(String city)          { this.city = city; }
    public void setAvailable(boolean a)       { this.available = a; }
}
