package com.emediHub.prescription;

public class PrescriptionItem {
    private int    id;
    private int    prescriptionId;
    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;

    public PrescriptionItem() {}

    public PrescriptionItem(int id, int prescriptionId, String medicineName, String dosage, String frequency, String duration, String instructions) {
        this.id             = id;
        this.prescriptionId = prescriptionId;
        this.medicineName   = medicineName;
        this.dosage         = dosage;
        this.frequency      = frequency;
        this.duration       = duration;
        this.instructions   = instructions;
    }

    public int    getId()             { return id; }
    public int    getPrescriptionId() { return prescriptionId; }
    public String getMedicineName()   { return medicineName; }
    public String getDosage()         { return dosage; }
    public String getFrequency()      { return frequency; }
    public String getDuration()       { return duration; }
    public String getInstructions()   { return instructions; }

    public void setId(int id)                  { this.id = id; }
    public void setPrescriptionId(int pid)     { this.prescriptionId = pid; }
    public void setMedicineName(String m)      { this.medicineName = m; }
    public void setDosage(String d)            { this.dosage = d; }
    public void setFrequency(String f)         { this.frequency = f; }
    public void setDuration(String d)          { this.duration = d; }
    public void setInstructions(String i)      { this.instructions = i; }
}
