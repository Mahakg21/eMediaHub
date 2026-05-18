package com.emediHub.lab;

public class LabReport {
    private int    id;
    private String patientName;
    private String doctorName;
    private String testName;
    private String testDate;
    private String result;
    private String normalRange;
    private String unit;
    private String status;
    private String remarks;
    private String labTechnician;
    private String createdAt;

    public LabReport() {}

    public LabReport(
            int id,
            String patientName,
            String doctorName,
            String testName,
            String testDate,
            String result,
            String normalRange,
            String unit,
            String status,
            String remarks,
            String labTechnician,
            String createdAt) {
        this.id            = id;
        this.patientName   = patientName;
        this.doctorName    = doctorName;
        this.testName      = testName;
        this.testDate      = testDate;
        this.result        = result;
        this.normalRange   = normalRange;
        this.unit          = unit;
        this.status        = status;
        this.remarks       = remarks;
        this.labTechnician = labTechnician;
        this.createdAt     = createdAt;
    }

    public int    getId()            { return id; }
    public String getPatientName()   { return patientName; }
    public String getDoctorName()    { return doctorName; }
    public String getTestName()      { return testName; }
    public String getTestDate()      { return testDate; }
    public String getResult()        { return result; }
    public String getNormalRange()   { return normalRange; }
    public String getUnit()          { return unit; }
    public String getStatus()        { return status; }
    public String getRemarks()       { return remarks; }
    public String getLabTechnician() { return labTechnician; }
    public String getCreatedAt()     { return createdAt; }

    public void setId(int id)                  { this.id = id; }
    public void setPatientName(String n)       { this.patientName = n; }
    public void setDoctorName(String n)        { this.doctorName = n; }
    public void setTestName(String t)          { this.testName = t; }
    public void setTestDate(String d)          { this.testDate = d; }
    public void setResult(String r)            { this.result = r; }
    public void setNormalRange(String n)       { this.normalRange = n; }
    public void setUnit(String u)              { this.unit = u; }
    public void setStatus(String s)            { this.status = s; }
    public void setRemarks(String r)           { this.remarks = r; }
    public void setLabTechnician(String l)     { this.labTechnician = l; }
    public void setCreatedAt(String c)         { this.createdAt = c; }
}
