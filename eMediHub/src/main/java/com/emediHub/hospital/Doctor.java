package com.emediHub.hospital;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private String status;
    
    public Doctor() {}

    public Doctor(int id, String name, String specialization, String phone, String email,String status) {
        this.id             = id;
        this.name           = name;
        this.specialization = specialization;
        this.phone          = phone;
        this.email          = email;
        this.status         = status;
    }

    public int    getId()             { return id; }
    public String getName()           { return name; }
    public String getSpecialization() { return specialization; }
    public String getPhone()          { return phone; }
    public String getEmail()          { return email; }
    public String getStatus()         { return status; }

    public void setId(int id)                  { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setSpecialization(String s)    { this.specialization = s; }
    public void setPhone(String phone)         { this.phone = phone; }
    public void setEmail(String email)         { this.email = email; }
    public void setStatus(String status)       { this.status = status; }
}