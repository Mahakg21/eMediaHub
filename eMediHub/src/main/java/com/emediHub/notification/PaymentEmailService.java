package com.emediHub.notification;

import com.emediHub.payment.Payment;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class PaymentEmailService {

    // ─────────── MAIN METHOD ───────────
    public static boolean sendReceipt(Payment p,String toEmail) {

        // Validate
        if (toEmail == null || toEmail.trim().isEmpty() || !toEmail.contains("@") || !toEmail.contains(".")) {
            System.out.println("Invalid email: " + toEmail);
            return false;
        }

        try {
            Session session =createSession();

            Message msg =new MimeMessage(session);

            // From
            msg.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL, EmailConfig.HOSPITAL_NAME));

            // To
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail.trim()));

            // Subject
            msg.setSubject(
                "eMediHub Payment Receipt"
                + " #" + p.getId()
                + " — Rs. "
                + String.format(
                    "%.2f", p.getAmount()));

            // HTML Body
            msg.setContent(buildHTML(p),"text/html; charset=utf-8");

            // Send
            Transport.send(msg);

            System.out.println("Payment receipt sent to: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            System.out.println("Gmail auth failed — " + "check App Password");
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

    // ─────────── CREATE SESSION ───────────
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
                    return new PasswordAuthentication(EmailConfig.FROM_EMAIL, EmailConfig.APP_PASSWORD
                    );
                }
            });
    }

    // ─────────── BUILD HTML ───────────
    private static String buildHTML(Payment p) {

        String statusColor = p.getStatus().equalsIgnoreCase("Paid") ? "#27ae60" : "#e67e22";
        String statusText = p.getStatus().equalsIgnoreCase("Paid") ? "PAID" : "PENDING";

        return "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<meta name='viewport' "
            + "content='width=device-width,"
            + "initial-scale=1.0'>"
            + "<title>Payment Receipt"
            + "</title>"
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
            + "Payment Receipt</p>"
            + "</div>"

            // ── AMOUNT SECTION ──
            + "<div style='"
            + "text-align:center;"
            + "padding:32px 30px 24px'>"
            + "<p style='"
            + "margin:0;color:#95a5a6;"
            + "font-size:13px;"
            + "text-transform:uppercase;"
            + "letter-spacing:1px'>"
            + "Amount Paid</p>"
            + "<h1 style='"
            + "margin:10px 0;"
            + "color:#27ae60;"
            + "font-size:48px;"
            + "font-weight:bold'>"
            + "Rs. "
            + String.format("%.2f", p.getAmount())
            + "</h1>"
            + "<span style='"
            + "display:inline-block;"
            + "background:"
            + statusColor + ";"
            + "color:white;"
            + "padding:6px 24px;"
            + "border-radius:20px;"
            + "font-size:13px;"
            + "font-weight:bold;"
            + "letter-spacing:1px'>"
            + statusText
            + "</span>"
            + "</div>"

            // ── RECEIPT ID BANNER ──
            + "<div style='"
            + "background:#8e44ad;"
            + "color:white;"
            + "padding:10px 30px;"
            + "display:flex;"
            + "justify-content:"
            + "space-between;"
            + "font-size:14px'>"
            + "<span><b>Receipt #"
            + p.getId() + "</b></span>"
            + "<span>"
            + safe(p.getCreatedAt())
            + "</span>"
            + "</div>"

            // ── DETAILS TABLE ──
            + "<div style='padding:25px 30px'>"
            + "<table style='"
            + "width:100%;"
            + "border-collapse:collapse;"
            + "border-radius:8px;"
            + "overflow:hidden;"
            + "border:1px solid #ecf0f1'>"

            + detailRow("Patient Name", safe(p.getPatientName()), false)
            + detailRow("Payment Type", safe(p.getPaymentType()), true)
            + detailRow("Payment Method", safe(p.getPaymentMethod()), false)
            + detailRow("UPI ID", safe(p.getUpiId()), true)
            + detailRow("Transaction ID", safe(p.getTransactionId()), false)
            + detailRow("Status", safe(p.getStatus()), true)
            + detailRow("Date & Time", safe(p.getCreatedAt()), false)

            + "</table>"
            + "</div>"

            // ── AMOUNT BREAKUP ──
            + "<div style='"
            + "margin:0 30px 20px;"
            + "padding:16px 20px;"
            + "background:#f0fff4;"
            + "border:1px solid #c3e6cb;"
            + "border-radius:8px'>"
            + "<table style='width:100%'>"
            + "<tr>"
            + "<td style='"
            + "color:#6c757d;"
            + "font-size:13px'>"
            + "Subtotal</td>"
            + "<td style='"
            + "text-align:right;"
            + "color:#2c3e50;"
            + "font-size:13px'>"
            + "Rs. "
            + String.format("%.2f", p.getAmount())
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td style='"
            + "color:#6c757d;"
            + "font-size:13px'>"
            + "Tax</td>"
            + "<td style='"
            + "text-align:right;"
            + "color:#2c3e50;"
            + "font-size:13px'>"
            + "Rs. 0.00</td>"
            + "</tr>"
            + "<tr style='"
            + "border-top:"
            + "2px solid #c3e6cb'>"
            + "<td style='"
            + "color:#1a252f;"
            + "font-weight:bold;"
            + "font-size:15px;"
            + "padding-top:8px'>"
            + "Total Paid</td>"
            + "<td style='"
            + "text-align:right;"
            + "color:#27ae60;"
            + "font-weight:bold;"
            + "font-size:15px;"
            + "padding-top:8px'>"
            + "Rs. "
            + String.format("%.2f", p.getAmount())
            + "</td>"
            + "</tr>"
            + "</table>"
            + "</div>"

            // ── INFO BOX ──
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
            + "Please keep this receipt "
            + "safe. Your Transaction ID "
            + "is your proof of payment. "
            + "For any queries contact "
            + "our billing department."
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
            + "This is a system generated"
            + " email. Please do not reply."
            + "</p>"
            + "</div>"

            + "</div>"
            + "</body></html>";
    }

    // ── Table row helper ──
    private static String detailRow(String label,String value,boolean gray) {
        String bg = gray ? "#f8f9fa" : "#ffffff";
        return "<tr style='"
            + "background:" + bg + "'>"
            + "<td style='"
            + "padding:12px 16px;"
            + "color:#6c757d;"
            + "font-size:13px;"
            + "width:160px;"
            + "border-bottom:"
            + "1px solid #ecf0f1'>"
            + label + "</td>"
            + "<td style='"
            + "padding:12px 16px;"
            + "color:#2c3e50;"
            + "font-size:13px;"
            + "font-weight:bold;"
            + "border-bottom:"
            + "1px solid #ecf0f1'>"
            + value + "</td>"
            + "</tr>";
    }

    private static String safe(String val) {
        return (val != null && !val.isEmpty()) ? val : "-";
    }
}
