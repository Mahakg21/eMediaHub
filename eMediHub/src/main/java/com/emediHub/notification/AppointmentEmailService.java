package com.emediHub.notification;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class AppointmentEmailService {

    public static boolean sendApproval(
            String toEmail,
            String patientName,
            String doctorName,
            String department,
            String date,
            String status,
            String remarks) {

        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@"))
            return false;

        try {
            Session session = createSession();

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL,EmailConfig.HOSPITAL_NAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail.trim()));
            msg.setSubject("eMediHub — Appointment " + status + " | " + patientName + " | " + date);
            msg.setContent(buildHTML(patientName, doctorName, department, date, status, remarks),"text/html; charset=utf-8");

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
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put( "mail.smtp.timeout", "10000");

        return Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailConfig.FROM_EMAIL, EmailConfig.APP_PASSWORD);
                }
            });
    }

    private static String buildHTML(String patientName,String doctorName,String department,String date,String status,String remarks) {

        boolean approved = status.equals("Approved");

        String headerColor = approved ? "#27ae60" : "#e74c3c";
        String iconText = approved ? "✓" : "✗";
        String iconBg = approved ? "#27ae60" : "#e74c3c";
        String statusMsg = approved
            ? "Your appointment has been"
            + " approved!"
            : "Your appointment has been"
            + " rejected.";

        String remarksSection = (remarks != null && !remarks.isEmpty())
            ? "<div style='"
                + "margin:0 25px 20px;"
                + "padding:14px 16px;"
                + "background:#f8f9fa;"
                + "border-left:4px solid "
                + headerColor + ";"
                + "border-radius:"
                + "0 6px 6px 0'>"
                + "<p style='"
                + "margin:0;"
                + "font-weight:bold;"
                + "color:#495057;"
                + "font-size:13px'>"
                + "Remarks from Doctor"
                + "</p>"
                + "<p style='"
                + "margin:6px 0 0;"
                + "color:#6c757d;"
                + "font-size:13px'>"
                + remarks + "</p>"
                + "</div>"
            : "";

        String tipSection = approved
            ? "<div style='"
                + "margin:0 25px 20px;"
                + "padding:14px 16px;"
                + "background:#e8f4f8;"
                + "border-left:"
                + "4px solid #3498db;"
                + "border-radius:"
                + "0 6px 6px 0'>"
                + "<p style='"
                + "margin:0;"
                + "font-weight:bold;"
                + "color:#2980b9;"
                + "font-size:13px'>"
                + "Important Tips</p>"
                + "<p style='"
                + "margin:5px 0 0;"
                + "font-size:12px;"
                + "color:#5d6d7e'>"
                + "Please arrive 10 minutes"
                + " early. Bring this email"
                + " and any previous reports."
                + " Contact helpline if you"
                + " need to reschedule."
                + "</p>"
                + "</div>"
            : "<div style='"
                + "margin:0 25px 20px;"
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
                + "Next Steps</p>"
                + "<p style='"
                + "margin:5px 0 0;"
                + "font-size:12px;"
                + "color:#922b21'>"
                + "Please contact our "
                + "helpline to book a new"
                + " appointment or visit "
                + "the hospital reception."
                + "</p>"
                + "</div>";

        return "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<title>Appointment "
            + status + "</title>"
            + "</head>"
            + "<body style='"
            + "margin:0;padding:0;"
            + "background:#f4f6f9;"
            + "font-family:Arial,"
            + "Helvetica,sans-serif'>"

            + "<div style='"
            + "max-width:600px;"
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
            + "Appointment " + status
            + "</p>"
            + "</div>"

            // ── STATUS ICON ──
            + "<div style='"
            + "text-align:center;"
            + "padding:30px 30px 20px'>"
            + "<div style='"
            + "width:72px;height:72px;"
            + "background:" + iconBg + ";"
            + "border-radius:50%;"
            + "margin:0 auto;"
            + "line-height:72px;"
            + "font-size:36px;"
            + "color:white;"
            + "font-weight:bold'>"
            + iconText + "</div>"
            + "<h2 style='"
            + "color:#2c3e50;"
            + "margin:16px 0 6px;"
            + "font-size:20px'>"
            + statusMsg + "</h2>"
            + "<p style='"
            + "color:#7f8c8d;"
            + "margin:0;font-size:14px'>"
            + "Dear " + patientName
            + ", below are your "
            + "appointment details."
            + "</p>"
            + "</div>"

            // ── STATUS BANNER ──
            + "<div style='"
            + "background:"
            + headerColor + ";"
            + "padding:10px 30px;"
            + "text-align:center;"
            + "color:white;"
            + "font-weight:bold;"
            + "font-size:15px;"
            + "letter-spacing:1px'>"
            + "STATUS: "
            + status.toUpperCase()
            + "</div>"

            // ── DETAILS ──
            + "<div style='"
            + "padding:20px 25px'>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "background:#f8f9fa;"
            + "border-radius:8px;"
            + "overflow:hidden'>"

            + detailRow("Patient", patientName, false)
            + detailRow("Doctor", "Dr. " + doctorName, true)
            + detailRow("Department", department, false)
            + detailRow("Date", date, true)
            + detailRow("Status", status, false)
            + "</table>"
            + "</div>"

            // ── REMARKS ──
            + remarksSection

            // ── TIPS ──
            + tipSection

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
            + "System generated email."
            + " Please do not reply."
            + "</p>"
            + "</div>"

            + "</div>"
            + "</body></html>";
    }

    private static String detailRow(String label,String value,boolean gray) {
        String bg = gray
            ? "#ecf0f1" : "#f8f9fa";
        return "<tr style='"
            + "background:" + bg + "'>"
            + "<td style='"
            + "padding:12px 16px;"
            + "color:#6c757d;"
            + "font-size:12px;"
            + "text-transform:uppercase;"
            + "letter-spacing:0.5px;"
            + "font-weight:bold;"
            + "width:130px'>"
            + label + "</td>"
            + "<td style='"
            + "padding:12px 16px;"
            + "font-weight:bold;"
            + "color:#2c3e50;"
            + "font-size:14px'>"
            + (value != null ? value : "-")
            + "</td>"
            + "</tr>";
    }
}
