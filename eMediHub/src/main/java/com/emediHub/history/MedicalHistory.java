package com.emediHub.history;

public class MedicalHistory {
    private int    id;
    private String patientName;
    private String doctorName;
    private String visitDate;
    private String diagnosis;
    private String treatment;
    private String symptoms;
    private String bloodPressure;
    private String temperature;
    private String weight;
    private String notes;
    private String followUpDate;
    private String createdAt;

    public MedicalHistory() {}

    public MedicalHistory(
            int id, String patientName,
            String doctorName,
            String visitDate,
            String diagnosis,
            String treatment,
            String symptoms,
            String bloodPressure,
            String temperature,
            String weight,
            String notes,
            String followUpDate,
            String createdAt) {
        this.id            = id;
        this.patientName   = patientName;
        this.doctorName    = doctorName;
        this.visitDate     = visitDate;
        this.diagnosis     = diagnosis;
        this.treatment     = treatment;
        this.symptoms      = symptoms;
        this.bloodPressure = bloodPressure;
        this.temperature   = temperature;
        this.weight        = weight;
        this.notes         = notes;
        this.followUpDate  = followUpDate;
        this.createdAt     = createdAt;
    }

    public int    getId()            { return id; }
    public String getPatientName()   { return patientName; }
    public String getDoctorName()    { return doctorName; }
    public String getVisitDate()     { return visitDate; }
    public String getDiagnosis()     { return diagnosis; }
    public String getTreatment()     { return treatment; }
    public String getSymptoms()      { return symptoms; }
    public String getBloodPressure() { return bloodPressure; }
    public String getTemperature()   { return temperature; }
    public String getWeight()        { return weight; }
    public String getNotes()         { return notes; }
    public String getFollowUpDate()  { return followUpDate; }
    public String getCreatedAt()     { return createdAt; }

    public void setId(int id)                  { this.id = id; }
    public void setPatientName(String n)       { this.patientName = n; }
    public void setDoctorName(String n)        { this.doctorName = n; }
    public void setVisitDate(String d)         { this.visitDate = d; }
    public void setDiagnosis(String d)         { this.diagnosis = d; }
    public void setTreatment(String t)         { this.treatment = t; }
    public void setSymptoms(String s)          { this.symptoms = s; }
    public void setBloodPressure(String bp)    { this.bloodPressure = bp; }
    public void setTemperature(String t)       { this.temperature = t; }
    public void setWeight(String w)            { this.weight = w; }
    public void setNotes(String n)             { this.notes = n; }
    public void setFollowUpDate(String f)      { this.followUpDate = f; }
    public void setCreatedAt(String c)         { this.createdAt = c; }
}
