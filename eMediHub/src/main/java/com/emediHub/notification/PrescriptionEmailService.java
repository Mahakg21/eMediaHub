package com.emediHub.notification;

import com.emediHub.prescription.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class PrescriptionEmailService {

    // ─────────── MAIN METHOD ───────────
    // Ye method call karo prescription
    // email bhejne ke liye
    public static boolean send(String toEmail,Prescription p,List<PrescriptionItem> items) {

        // Email validate karo
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@") || !toEmail.contains(".")) {
            System.out.println("Invalid email: " + toEmail);
            return false;
        }

        try {
            // Gmail SMTP session banao
            Session session = buildSession();

            // Email message banao
            Message msg = new MimeMessage(session);

            // From
            msg.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL, EmailConfig.HOSPITAL_NAME));

            // To
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail.trim()));

            // Subject
            msg.setSubject(
                "eMediHub Prescription #"
                + p.getId()
                + " — "
                + p.getDoctorName());

            // HTML body
            msg.setContent(buildHTML(p, items), "text/html; charset=utf-8");

            // Send karo
            Transport.send(msg);

            System.out.println("Email sent to: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            System.out.println("Gmail auth failed — " + "check email/password");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            System.out.println("Email send failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────── BUILD GMAIL SESSION ───────────
    private static Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.ssl.trust","smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout","10000");
        props.put("mail.smtp.timeout","10000");

        return Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailConfig.FROM_EMAIL, EmailConfig.APP_PASSWORD);
                }
            });
    }

    // ─────────── BUILD HTML EMAIL ───────────
    private static String buildHTML(Prescription p,List<PrescriptionItem> items) {

        // Medicine rows banao
        StringBuilder medRows = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            PrescriptionItem item = items.get(i);
            String rowBg = (i % 2 == 0) ? "#f8f9fa" : "#ffffff";

            medRows.append("<tr style='" + "background:" + rowBg + "'>");

            // Sr No
            medRows.append(
                "<td style='"
                + "padding:10px 12px;"
                + "border:1px solid #dee2e6;"
                + "text-align:center;"
                + "font-weight:bold;"
                + "color:#6c757d'>"
                + (i + 1) + "</td>");

            // Medicine name
            medRows.append(
                "<td style='"
                + "padding:10px 12px;"
                + "border:1px solid #dee2e6;"
                + "font-weight:bold;"
                + "color:#2c3e50'>"
                + safe(item.getMedicineName())
                + "</td>");

            // Dosage
            medRows.append(
                "<td style='"
                + "padding:10px 12px;"
                + "border:1px solid #dee2e6;"
                + "text-align:center;"
                + "color:#495057'>"
                + safe(item.getDosage())
                + "</td>");

            // Frequency
            medRows.append(
                "<td style='"
                + "padding:10px 12px;"
                + "border:1px solid #dee2e6;"
                + "text-align:center;"
                + "color:#495057'>"
                + safe(item.getFrequency())
                + "</td>");

            // Duration
            medRows.append(
                "<td style='"
                + "padding:10px 12px;"
                + "border:1px solid #dee2e6;"
                + "text-align:center;"
                + "color:#e74c3c;"
                + "font-weight:bold'>"
                + safe(item.getDuration())
                + "</td>");

            // Instructions
            medRows.append(
                "<td style='"
                + "padding:10px 12px;"
                + "border:1px solid #dee2e6;"
                + "color:#e67e22;"
                + "font-style:italic'>"
                + safe(item.getInstructions())
                + "</td>");

            medRows.append("</tr>");
        }

        // Notes section
        String notesSection = "";
        if (p.getNotes() != null && !p.getNotes().isEmpty()) {
            notesSection =
                "<div style='"
                + "margin:20px 0;"
                + "padding:15px 18px;"
                + "background:#fff8e1;"
                + "border-left:"
                + "4px solid #ffc107;"
                + "border-radius:0 6px 6px 0'>"
                + "<p style='"
                + "margin:0 0 4px;"
                + "font-weight:bold;"
                + "color:#856404'>"
                + "Doctor's Notes</p>"
                + "<p style='"
                + "margin:0;color:#664d03'>"
                + safe(p.getNotes())
                + "</p>"
                + "</div>";
        }

        // Full HTML email
        return "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<meta name='viewport' "
            + "content='width=device-width,"
            + "initial-scale=1.0'>"
            + "<title>eMediHub Prescription"
            + "</title>"
            + "</head>"
            + "<body style='"
            + "margin:0;padding:0;"
            + "background-color:#f4f6f9;"
            + "font-family:Arial,"
            + "Helvetica,sans-serif'>"

            + "<div style='"
            + "max-width:680px;"
            + "margin:30px auto;"
            + "background:#ffffff;"
            + "border-radius:12px;"
            + "overflow:hidden;"
            + "box-shadow:"
            + "0 4px 20px rgba(0,0,0,0.08)'>"

            // ── TOP HEADER ──
            + "<div style='"
            + "background:#1a252f;"
            + "padding:28px 30px;"
            + "text-align:center'>"
            + "<h1 style='"
            + "margin:0;"
            + "color:#ffffff;"
            + "font-size:26px;"
            + "letter-spacing:1px'>"
            + "eMediHub</h1>"
            + "<p style='"
            + "margin:6px 0 0;"
            + "color:#7f8c8d;"
            + "font-size:13px;"
            + "letter-spacing:2px;"
            + "text-transform:uppercase'>"
            + "Medical Prescription</p>"
            + "</div>"

            // ── GREEN BANNER ──
            + "<div style='"
            + "background:#27ae60;"
            + "padding:12px 30px;"
            + "display:flex;"
            + "justify-content:space-between'>"
            + "<span style='"
            + "color:white;"
            + "font-weight:bold;"
            + "font-size:15px'>"
            + "Prescription #"
            + p.getId() + "</span>"
            + "<span style='"
            + "color:#d5f5e3;"
            + "font-size:13px'>"
            + safe(p.getCreatedAt())
            + "</span>"
            + "</div>"

            // ── PATIENT + DOCTOR INFO ──
            + "<div style='"
            + "padding:25px 30px 0'>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "background:#f8f9fa;"
            + "border-radius:8px;"
            + "overflow:hidden'>"

            + "<tr>"
            + "<td style='"
            + "padding:13px 16px;"
            + "color:#6c757d;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:0.5px;"
            + "width:110px;"
            + "font-weight:bold'>"
            + "Patient</td>"
            + "<td style='"
            + "padding:13px 16px;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:15px'>"
            + safe(p.getPatientName())
            + "</td>"
            + "<td style='"
            + "padding:13px 16px;"
            + "color:#6c757d;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:0.5px;"
            + "width:100px;"
            + "font-weight:bold'>"
            + "Doctor</td>"
            + "<td style='"
            + "padding:13px 16px;"
            + "font-weight:bold;"
            + "color:#2980b9;"
            + "font-size:15px'>"
           
            + safe(p.getDoctorName())
            + "</td>"
            + "</tr>"

            + "<tr style='"
            + "background:#ecf0f1'>"
            + "<td style='"
            + "padding:13px 16px;"
            + "color:#6c757d;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:0.5px;"
            + "font-weight:bold'>"
            + "Diagnosis</td>"
            + "<td colspan='3' style='"
            + "padding:13px 16px;"
            + "color:#e74c3c;"
            + "font-weight:bold;"
            + "font-size:14px'>"
            + safe(p.getDiagnosis())
            + "</td>"
            + "</tr>"

            + "</table>"
            + "</div>"

            // ── MEDICINES HEADING ──
            + "<div style='"
            + "padding:20px 30px 10px'>"
            + "<h3 style='"
            + "margin:0;"
            + "color:#1a252f;"
            + "font-size:16px;"
            + "padding-left:12px;"
            + "border-left:"
            + "4px solid #27ae60'>"
            + "Medicines Prescribed (Rx)"
            + "</h3>"
            + "</div>"

            // ── MEDICINES TABLE ──
            + "<div style='"
            + "padding:0 30px'>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "font-size:13px'>"

            + "<thead>"
            + "<tr style='"
            + "background:#1a252f;"
            + "color:#ffffff'>"
            + "<th style='"
            + "padding:11px 12px;"
            + "text-align:center;"
            + "width:40px'>#</th>"
            + "<th style='"
            + "padding:11px 12px;"
            + "text-align:left'>"
            + "Medicine Name</th>"
            + "<th style='"
            + "padding:11px 12px;"
            + "text-align:center'>"
            + "Dosage</th>"
            + "<th style='"
            + "padding:11px 12px;"
            + "text-align:center'>"
            + "Frequency</th>"
            + "<th style='"
            + "padding:11px 12px;"
            + "text-align:center'>"
            + "Duration</th>"
            + "<th style='"
            + "padding:11px 12px;"
            + "text-align:center'>"
            + "Instructions</th>"
            + "</tr>"
            + "</thead>"

            + "<tbody>"
            + medRows.toString()
            + "</tbody>"
            + "</table>"
            + "</div>"

            // ── NOTES ──
            + "<div style='padding:0 30px'>"
            + notesSection
            + "</div>"

            // ── DOCTOR SIGNATURE ──
            + "<div style='"
            + "margin:20px 30px;"
            + "padding:18px 20px;"
            + "background:#f0fff4;"
            + "border:1px solid #c3e6cb;"
            + "border-radius:8px;"
            + "text-align:right'>"
            + "<p style='"
            + "margin:0;"
            + "font-weight:bold;"
            + "font-size:17px;"
            + "color:#1a252f'>"
          
            + safe(p.getDoctorName())
            + "</p>"
            + "<p style='"
            + "margin:4px 0 0;"
            + "color:#6c757d;"
            + "font-size:12px'>"
            + "eMediHub Hospital</p>"
            + "<p style='"
            + "margin:4px 0 0;"
            + "color:#adb5bd;"
            + "font-size:11px;"
            + "font-style:italic'>"
            + "Digitally signed prescription"
            + "</p>"
            + "</div>"

            // ── IMPORTANT NOTE ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:14px 16px;"
            + "background:#e8f4f8;"
            + "border-left:"
            + "4px solid #3498db;"
            + "border-radius:0 6px 6px 0'>"
            + "<p style='"
            + "margin:0;"
            + "font-size:12px;"
            + "color:#2980b9;"
            + "font-weight:bold'>"
            + "Important</p>"
            + "<p style='"
            + "margin:4px 0 0;"
            + "font-size:12px;"
            + "color:#5d6d7e'>"
            + "Take medicines as prescribed. "
            + "Do not stop without "
            + "doctor's advice. "
            + "Keep this prescription safe."
            + "</p>"
            + "</div>"

            // ── FOOTER ──
            + "<div style='"
            + "background:#1a252f;"
            + "padding:20px 30px;"
            + "text-align:center'>"
            + "<p style='"
            + "margin:0;"
            + "color:#ecf0f1;"
            + "font-size:14px;"
            + "font-weight:bold'>"
            + "eMediHub Hospital</p>"
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
            + "This is a system generated "
            + "email from eMediHub. "
            + "Please do not reply."
            + "</p>"
            + "</div>"

            + "</div>"
            + "</body></html>";
    }

    // Null safe helper
    private static String safe(String val) {
        return (val != null && !val.isEmpty()) ? val : "-";
    }
}
