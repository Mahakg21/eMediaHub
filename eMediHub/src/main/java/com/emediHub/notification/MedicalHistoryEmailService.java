package com.emediHub.notification;

import com.emediHub.history.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.util.Properties;

public class MedicalHistoryEmailService {

    // ─────────── MAIN METHOD ───────────
    public static boolean send(String toEmail,MedicalHistory h) {

        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@") || !toEmail.contains(".")) {
            System.out.println("Invalid email: " + toEmail);
            return false;
        }

        try {
            Session session = createSession();

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL, EmailConfig.HOSPITAL_NAME));

            msg.setRecipient( Message.RecipientType.TO, new InternetAddress(toEmail.trim()));

            msg.setSubject(
                "eMediHub — Medical Visit"
                + " Record | "
                + safe(h.getPatientName())
                + " | "
                + safe(h.getVisitDate()));

            msg.setContent(buildHTML(h),"text/html; charset=utf-8");

            Transport.send(msg);
            System.out.println("Medical history sent to: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            System.out.println("Gmail auth failed!");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            System.out.println("Email failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─────────── CREATE SESSION ───────────
    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        return Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailConfig.FROM_EMAIL,EmailConfig.APP_PASSWORD
                    );
                }
            });
    }

    // ─────────── BUILD HTML ───────────
    private static String buildHTML(MedicalHistory h) {

        // Follow up section
        String followUp = (h.getFollowUpDate() != null && !h.getFollowUpDate().isEmpty())
            ? "<div style='"
                + "margin:0 30px 20px;"
                + "padding:16px 20px;"
                + "background:#fff3cd;"
                + "border-left:"
                + "4px solid #ffc107;"
                + "border-radius:"
                + "0 6px 6px 0'>"
                + "<p style='"
                + "margin:0;"
                + "font-weight:bold;"
                + "color:#856404;"
                + "font-size:14px'>"
                + "Follow Up Appointment"
                + "</p>"
                + "<p style='"
                + "margin:6px 0 0;"
                + "color:#664d03;"
                + "font-size:15px;"
                + "font-weight:bold'>"
                + h.getFollowUpDate()
                + "</p>"
                + "<p style='"
                + "margin:4px 0 0;"
                + "color:#856404;"
                + "font-size:12px'>"
                + "Please visit hospital "
                + "on the above date."
                + "</p>"
                + "</div>"
            : "";

        // Notes section
        String notesSection = (h.getNotes() != null
                && !h.getNotes().isEmpty())
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
                + "Doctor's Notes</p>"
                + "<p style='"
                + "margin:6px 0 0;"
                + "color:#6c757d;"
                + "font-size:13px'>"
                + safe(h.getNotes())
                + "</p>"
                + "</div>"
            : "";

        return "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<meta name='viewport' "
            + "content='width=device-width,"
            + "initial-scale=1.0'>"
            + "<title>Medical History"
            + "</title>"
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
            + "Medical Visit Record"
            + "</p>"
            + "</div>"

            // ── GREEN BANNER ──
            + "<div style='"
            + "background:#27ae60;"
            + "padding:12px 30px;"
            + "display:flex;"
            + "justify-content:"
            + "space-between'>"
            + "<span style='"
            + "color:white;"
            + "font-weight:bold;"
            + "font-size:15px'>"
            + "Visit Record #"
            + h.getId() + "</span>"
            + "<span style='"
            + "color:#d5f5e3;"
            + "font-size:13px'>"
            + safe(h.getVisitDate())
            + "</span>"
            + "</div>"

            // ── PATIENT INFO ──
            + "<div style='padding:25px 30px'>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "background:#f8f9fa;"
            + "border-radius:8px;"
            + "overflow:hidden'>"

            + infoRow("Patient",safe(h.getPatientName()),false)
            + infoRow("Doctor","Dr. "
                + safe(h.getDoctorName()),true)
            + infoRow("Visit Date",safe(h.getVisitDate()),false)

            + "</table>"
            + "</div>"

            // ── DIAGNOSIS ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:16px 20px;"
            + "background:#fdf2f8;"
            + "border-left:"
            + "4px solid #8e44ad;"
            + "border-radius:"
            + "0 6px 6px 0'>"
            + "<p style='"
            + "margin:0;"
            + "font-weight:bold;"
            + "color:#6c3483;"
            + "font-size:13px;"
            + "text-transform:uppercase;"
            + "letter-spacing:1px'>"
            + "Diagnosis</p>"
            + "<p style='"
            + "margin:8px 0 0;"
            + "color:#2c3e50;"
            + "font-size:15px;"
            + "font-weight:bold'>"
            + safe(h.getDiagnosis())
            + "</p>"
            + "</div>"

            // ── SYMPTOMS ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:16px 20px;"
            + "background:#eaf4fb;"
            + "border-left:"
            + "4px solid #3498db;"
            + "border-radius:"
            + "0 6px 6px 0'>"
            + "<p style='"
            + "margin:0;"
            + "font-weight:bold;"
            + "color:#1a5276;"
            + "font-size:13px;"
            + "text-transform:uppercase;"
            + "letter-spacing:1px'>"
            + "Symptoms</p>"
            + "<p style='"
            + "margin:8px 0 0;"
            + "color:#2c3e50;"
            + "font-size:14px'>"
            + safe(h.getSymptoms())
            + "</p>"
            + "</div>"

            // ── TREATMENT ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:16px 20px;"
            + "background:#e9f7ef;"
            + "border-left:"
            + "4px solid #27ae60;"
            + "border-radius:"
            + "0 6px 6px 0'>"
            + "<p style='"
            + "margin:0;"
            + "font-weight:bold;"
            + "color:#1e8449;"
            + "font-size:13px;"
            + "text-transform:uppercase;"
            + "letter-spacing:1px'>"
            + "Treatment</p>"
            + "<p style='"
            + "margin:8px 0 0;"
            + "color:#2c3e50;"
            + "font-size:14px'>"
            + safe(h.getTreatment())
            + "</p>"
            + "</div>"

            // ── VITALS ──
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
            + "Vitals</p>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse'>"
            + "<tr>"
            + vitalCell("Blood Pressure",safe(h.getBloodPressure()), "#e74c3c")
            + vitalCell("Temperature",safe(h.getTemperature()), "#e67e22")
            + vitalCell("Weight",safe(h.getWeight()), "#3498db")
            + "</tr>"
            + "</table>"
            + "</div>"

            // ── NOTES ──
            + notesSection

            // ── FOLLOW UP ──
            + followUp

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
            + "This is your official "
            + "medical visit record from "
            + "eMediHub Hospital. "
            + "Please keep it safe for "
            + "future reference. "
            + "Do not share with others."
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
            + "email. Please do not reply."
            + "</p>"
            + "</div>"
            + "</div>"
            + "</body></html>";
    }

    // ── Info row helper ──
    private static String infoRow(String label,String value,boolean gray) {
        String bg = gray ? "#ecf0f1" : "#f8f9fa";
        return "<tr style='"
            + "background:" + bg + "'>"
            + "<td style='"
            + "padding:13px 16px;"
            + "color:#6c757d;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:0.5px;"
            + "font-weight:bold;"
            + "width:130px'>"
            + label + "</td>"
            + "<td style='"
            + "padding:13px 16px;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:14px'>"
            + value + "</td>"
            + "</tr>";
    }

    // ── Vital cell helper ──
    private static String vitalCell(String label,String value,String color) {
        return "<td style='"
            + "padding:12px;"
            + "text-align:center;"
            + "background:#f8f9fa;"
            + "border-radius:8px;"
            + "margin:4px;"
            + "width:33%'>"
            + "<p style='"
            + "margin:0;"
            + "font-size:12px;"
            + "color:#6c757d'>"
            + label + "</p>"
            + "<p style='"
            + "margin:6px 0 0;"
            + "font-size:18px;"
            + "font-weight:bold;"
            + "color:" + color + "'>"
            + value + "</p>"
            + "</td>";
    }

    private static String safe(String val) {
        return (val != null && !val.isEmpty()) ? val : "-";
    }
}
