package com.emediHub.payment;

public class Payment {
    private int    id;
    private String patientName;
    private String paymentType;
    private double amount;
    private String paymentMethod;
    private String upiId;
    private String status;
    private String transactionId;
    private String remarks;
    private String createdAt;

    public Payment() {}

    public Payment(int id, String patientName,
                   String paymentType, double amount,
                   String paymentMethod, String upiId,
                   String status, String transactionId,
                   String remarks, String createdAt) {
        this.id            = id;
        this.patientName   = patientName;
        this.paymentType   = paymentType;
        this.amount        = amount;
        this.paymentMethod = paymentMethod;
        this.upiId         = upiId;
        this.status        = status;
        this.transactionId = transactionId;
        this.remarks       = remarks;
        this.createdAt     = createdAt;
    }

    public int    getId()            { return id; }
    public String getPatientName()   { return patientName; }
    public String getPaymentType()   { return paymentType; }
    public double getAmount()        { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getUpiId()         { return upiId; }
    public String getStatus()        { return status; }
    public String getTransactionId() { return transactionId; }
    public String getRemarks()       { return remarks; }
    public String getCreatedAt()     { return createdAt; }

    public void setId(int id)                      { this.id = id; }
    public void setPatientName(String n)           { this.patientName = n; }
    public void setPaymentType(String t)           { this.paymentType = t; }
    public void setAmount(double a)                { this.amount = a; }
    public void setPaymentMethod(String m)         { this.paymentMethod = m; }
    public void setUpiId(String u)                 { this.upiId = u; }
    public void setStatus(String s)                { this.status = s; }
    public void setTransactionId(String t)         { this.transactionId = t; }
    public void setRemarks(String r)               { this.remarks = r; }
    public void setCreatedAt(String c)             { this.createdAt = c; }
}
