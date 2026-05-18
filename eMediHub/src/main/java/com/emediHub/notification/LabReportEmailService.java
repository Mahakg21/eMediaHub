package com.emediHub.notification;

import com.emediHub.lab.LabReport;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class LabReportEmailService {

    public static boolean send(String toEmail, LabReport r) {

        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@") || !toEmail.contains("."))
            return false;

        try {
            Session session = createSession();
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(
                    EmailConfig.FROM_EMAIL,
                    EmailConfig.HOSPITAL_NAME
                ));

            msg.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(
                    toEmail.trim()));

            msg.setSubject(
                "eMediHub Lab Report — "
                + safe(r.getTestName())
                + " | "
                + safe(r.getPatientName())
                + " | "
                + safe(r.getTestDate()));

            msg.setContent(buildHTML(r), "text/html; charset=utf-8");

            Transport.send(msg);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put( "mail.smtp.starttls.enable", "true");
        props.put( "mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put( "mail.smtp.connectiontimeout", "10000");
        props.put( "mail.smtp.timeout", "10000");

        return Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        EmailConfig.FROM_EMAIL,
                        EmailConfig.APP_PASSWORD
                    );
                }
            });
    }

    private static String buildHTML(LabReport r) {

        // Status color
        String statusBg, statusText;
        switch (safe(r.getStatus())) {
            case "Abnormal":
                statusBg   = "#e74c3c";
                statusText = "#ffffff";
                break;
            case "Critical":
                statusBg   = "#c0392b";
                statusText = "#ffffff";
                break;
            case "Borderline":
                statusBg   = "#f39c12";
                statusText = "#ffffff";
                break;
            default:
                statusBg   = "#27ae60";
                statusText = "#ffffff";
        }

        // Remarks section
        String remarksSection =
            (r.getRemarks() != null
                && !r.getRemarks().isEmpty())
            ? "<div style='"
                + "margin:0 30px 20px;"
                + "padding:14px 16px;"
                + "background:#f8f9fa;"
                + "border-left:"
                + "4px solid #6c757d;"
                + "border-radius:"
                + "0 6px 6px 0'>"
                + "<p style='"
                + "margin:0;"
                + "font-weight:bold;"
                + "color:#495057;"
                + "font-size:13px'>"
                + "Doctor Remarks</p>"
                + "<p style='"
                + "margin:6px 0 0;"
                + "color:#6c757d;"
                + "font-size:13px'>"
                + safe(r.getRemarks())
                + "</p>"
                + "</div>"
            : "";

        // Warning for abnormal
        String warningSection =
            (safe(r.getStatus())
                .equals("Abnormal")
            || safe(r.getStatus())
                .equals("Critical"))
            ? "<div style='"
                + "margin:0 30px 20px;"
                + "padding:14px 16px;"
                + "background:#fdf2f2;"
                + "border-left:"
                + "4px solid #e74c3c;"
                + "border-radius:"
                + "0 6px 6px 0'>"
                + "<p style='"
                + "margin:0;"
                + "font-weight:bold;"
                + "color:#c0392b;"
                + "font-size:13px'>"
                + "Important Warning</p>"
                + "<p style='"
                + "margin:5px 0 0;"
                + "color:#922b21;"
                + "font-size:13px'>"
                + "Your test result is "
                + safe(r.getStatus())
                + ". Please consult your "
                + "doctor Dr. "
                + safe(r.getDoctorName())
                + " immediately."
                + "</p>"
                + "</div>"
            : "";

        return "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<title>Lab Report</title>"
            + "</head>"
            + "<body style='"
            + "margin:0;padding:0;"
            + "background:#f4f6f9;"
            + "font-family:Arial,"
            + "Helvetica,sans-serif'>"

            + "<div style='"
            + "max-width:650px;"
            + "margin:30px auto;"
            + "background:#ffffff;"
            + "border-radius:12px;"
            + "overflow:hidden;"
            + "box-shadow:0 4px 20px "
            + "rgba(0,0,0,0.08)'>"

            // ── HEADER ──
            + "<div style='"
            + "background:#1a252f;"
            + "padding:28px 30px;"
            + "text-align:center'>"
            + "<h1 style='"
            + "margin:0;color:#ffffff;"
            + "font-size:26px;"
            + "letter-spacing:1px'>"
            + "eMediHub</h1>"
            + "<p style='"
            + "margin:6px 0 0;"
            + "color:#7f8c8d;"
            + "font-size:12px;"
            + "letter-spacing:2px;"
            + "text-transform:uppercase'>"
            + "Laboratory Report"
            + "</p>"
            + "</div>"

            // ── REPORT ID BANNER ──
            + "<div style='"
            + "background:#2980b9;"
            + "padding:12px 30px;"
            + "display:flex;"
            + "justify-content:"
            + "space-between'>"
            + "<span style='"
            + "color:white;"
            + "font-weight:bold;"
            + "font-size:15px'>"
            + "Report #" + r.getId()
            + "</span>"
            + "<span style='"
            + "color:#d6eaf8;"
            + "font-size:13px'>"
            + safe(r.getCreatedAt())
            + "</span>"
            + "</div>"

            // ── PATIENT INFO ──
            + "<div style='"
            + "padding:25px 30px 15px'>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "background:#f8f9fa;"
            + "border-radius:8px;"
            + "overflow:hidden'>"
            + infoRow("Patient", safe(r.getPatientName()), false)
            + infoRow("Referred By", "Dr. " + safe(r.getDoctorName()), true)
            + infoRow("Test Date", safe(r.getTestDate()), false)
            + infoRow("Lab Technician", safe(r.getLabTechnician()), true)
            + "</table>"
            + "</div>"

            // ── TEST NAME ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:16px 20px;"
            + "background:#eaf4fb;"
            + "border-left:"
            + "4px solid #2980b9;"
            + "border-radius:"
            + "0 6px 6px 0'>"
            + "<p style='"
            + "margin:0;"
            + "font-weight:bold;"
            + "color:#1a5276;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:1px'>"
            + "Test Name</p>"
            + "<p style='"
            + "margin:6px 0 0;"
            + "color:#2c3e50;"
            + "font-size:17px;"
            + "font-weight:bold'>"
            + safe(r.getTestName())
            + "</p>"
            + "</div>"

            // ── RESULT TABLE ──
            + "<div style='"
            + "margin:0 30px 20px'>"
            + "<p style='"
            + "margin:0 0 12px;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:14px;"
            + "border-bottom:"
            + "2px solid #ecf0f1;"
            + "padding-bottom:8px'>"
            + "Test Result</p>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "border:1px solid #dee2e6;"
            + "border-radius:8px;"
            + "overflow:hidden'>"

            // Table header
            + "<thead>"
            + "<tr style='"
            + "background:#2c3e50;"
            + "color:white'>"
            + "<th style='"
            + "padding:12px 16px;"
            + "text-align:left;"
            + "font-size:13px'>"
            + "Test</th>"
            + "<th style='"
            + "padding:12px 16px;"
            + "text-align:center;"
            + "font-size:13px'>"
            + "Result</th>"
            + "<th style='"
            + "padding:12px 16px;"
            + "text-align:center;"
            + "font-size:13px'>"
            + "Unit</th>"
            + "<th style='"
            + "padding:12px 16px;"
            + "text-align:center;"
            + "font-size:13px'>"
            + "Normal Range</th>"
            + "<th style='"
            + "padding:12px 16px;"
            + "text-align:center;"
            + "font-size:13px'>"
            + "Status</th>"
            + "</tr>"
            + "</thead>"

            // Table body
            + "<tbody>"
            + "<tr style='"
            + "background:#f8f9fa'>"
            + "<td style='"
            + "padding:14px 16px;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:14px;"
            + "border-bottom:"
            + "1px solid #dee2e6'>"
            + safe(r.getTestName())
            + "</td>"
            + "<td style='"
            + "padding:14px 16px;"
            + "text-align:center;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:16px;"
            + "border-bottom:"
            + "1px solid #dee2e6'>"
            + safe(r.getResult())
            + "</td>"
            + "<td style='"
            + "padding:14px 16px;"
            + "text-align:center;"
            + "color:#6c757d;"
            + "font-size:13px;"
            + "border-bottom:"
            + "1px solid #dee2e6'>"
            + safe(r.getUnit())
            + "</td>"
            + "<td style='"
            + "padding:14px 16px;"
            + "text-align:center;"
            + "color:#6c757d;"
            + "font-size:13px;"
            + "border-bottom:"
            + "1px solid #dee2e6'>"
            + safe(r.getNormalRange())
            + "</td>"
            + "<td style='"
            + "padding:14px 16px;"
            + "text-align:center;"
            + "border-bottom:"
            + "1px solid #dee2e6'>"
            + "<span style='"
            + "background:" + statusBg
            + ";color:" + statusText
            + ";padding:4px 12px;"
            + "border-radius:12px;"
            + "font-size:12px;"
            + "font-weight:bold'>"
            + safe(r.getStatus())
            + "</span>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</div>"

            // ── REMARKS ──
            + remarksSection

            // ── WARNING (if abnormal) ──
            + warningSection

            // ── IMPORTANT NOTE ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:14px 16px;"
            + "background:#e8f4f8;"
            + "border-left:"
            + "4px solid #3498db;"
            + "border-radius:"
            + "0 6px 6px 0'>"
            + "<p style='"
            + "margin:0;"
            + "font-size:12px;"
            + "color:#2980b9;"
            + "font-weight:bold'>"
            + "Important</p>"
            + "<p style='"
            + "margin:5px 0 0;"
            + "font-size:12px;"
            + "color:#5d6d7e'>"
            + "This report is generated "
            + "by eMediHub Laboratory. "
            + "Please consult your doctor "
            + "for interpretation of "
            + "results. Do not self-medicate."
            + "</p>"
            + "</div>"

            // ── FOOTER ──
            + "<div style='"
            + "background:#1a252f;"
            + "padding:22px 30px;"
            + "text-align:center'>"
            + "<p style='"
            + "margin:0;"
            + "color:#ecf0f1;"
            + "font-size:15px;"
            + "font-weight:bold'>"
            + "eMediHub Hospital"
            + "</p>"
            + "<p style='"
            + "margin:6px 0 0;"
            + "color:#7f8c8d;"
            + "font-size:12px'>"
            + "Helpline: "
            + EmailConfig.HELPLINE
            + "</p>"
            + "<p style='"
            + "margin:8px 0 0;"
            + "color:#566573;"
            + "font-size:11px'>"
            + "This is a system generated"
            + " email. Please do not reply."
            + "</p>"
            + "</div>"
            + "</div>"
            + "</body></html>";
    }

    private static String infoRow(String label,String value,boolean gray) {
        String bg = gray ? "#ecf0f1" : "#f8f9fa";
        return "<tr style='"
            + "background:" + bg + "'>"
            + "<td style='"
            + "padding:12px 16px;"
            + "color:#6c757d;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:0.5px;"
            + "font-weight:bold;"
            + "width:140px'>"
            + label + "</td>"
            + "<td style='"
            + "padding:12px 16px;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:14px'>"
            + value + "</td>"
            + "</tr>";
    }

    private static String safe(String val) {
        return (val != null && !val.isEmpty()) ? val : "-";
    }
}
