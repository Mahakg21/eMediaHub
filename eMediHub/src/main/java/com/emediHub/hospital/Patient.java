package com.emediHub.hospital;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String disease;

    public Patient() {}

    public Patient(int id, String name, int age, String gender, String phone, String disease) {
        this.id      = id;
        this.name    = name;
        this.age     = age;
        this.gender  = gender;
        this.phone   = phone;
        this.disease = disease;
    }

    public int    getId()      { return id; }
    public String getName()    { return name; }
    public int    getAge()     { return age; }
    public String getGender()  { return gender; }
    public String getPhone()   { return phone; }
    public String getDisease() { return disease; }

    public void setId(int id)           { this.id = id; }
    public void setName(String name)    { this.name = name; }
    public void setAge(int age)         { this.age = age; }
    public void setGender(String g)     { this.gender = g; }
    public void setPhone(String p)      { this.phone = p; }
    public void setDisease(String d)    { this.disease = d; }
}
