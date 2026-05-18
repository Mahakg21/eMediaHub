package com.emediHub.prescription;

import java.util.List;

public class Prescription {
    private int    id;
    private String patientName;
    private String doctorName;
    private String diagnosis;
    private String notes;
    private String createdAt;

    // Medicine rows in this prescription
    private List<PrescriptionItem> items;

    public Prescription() {}

    public Prescription(int id, String patientName, String doctorName, String diagnosis, String notes, String createdAt) {
        this.id          = id;
        this.patientName = patientName;
        this.doctorName  = doctorName;
        this.diagnosis   = diagnosis;
        this.notes       = notes;
        this.createdAt   = createdAt;
    }

    public int    getId()          { return id; }
    public String getPatientName() { return patientName; }
    public String getDoctorName()  { return doctorName; }
    public String getDiagnosis()   { return diagnosis; }
    public String getNotes()       { return notes; }
    public String getCreatedAt()   { return createdAt; }
    public List<PrescriptionItem> getItems() {
        return items;
    }

    public void setId(int id)                { this.id = id; }
    public void setPatientName(String n)     { this.patientName = n; }
    public void setDoctorName(String n)      { this.doctorName = n; }
    public void setDiagnosis(String d)       { this.diagnosis = d; }
    public void setNotes(String n)           { this.notes = n; }
    public void setCreatedAt(String c)       { this.createdAt = c; }
    public void setItems(
    List<PrescriptionItem> items)    { this.items = items; }
}
