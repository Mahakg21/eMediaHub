package com.emediHub.chatbot;
import com.emediHub.chatbot.DoctorFinderDao;
import java.util.*;

public class ChatBot {

    private static final DoctorFinderDao doctorDAO = new DoctorFinderDao();

    // ─── MAIN REPLY METHOD ───
    public static String getReply(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty())
            return "Please type your " + "symptoms or question.";

        String msg = userMessage.toLowerCase().trim();

        // ─── 1. Check symptoms first ───
        SymptomChecker.DepartmentResult result = SymptomChecker.checkSymptoms(msg);

        if (result != null) {
            return buildDoctorResponse(result, msg);
        }

        // ─── 2. Greetings ───
        if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey") || msg.contains("namaste")) {
            return "Hello! Welcome to "
                + "eMediHub.\n\n"
                + "I can help you find "
                + "the right doctor "
                + "based on your symptoms.\n\n"
                + "Please describe your "
                + "symptoms and I will "
                + "guide you to the right"
                + " department!";
        }

        // ─── 3. Department search ───
        if (msg.contains("department") || msg.contains("which doctor") || msg.contains("which dept") || msg.contains("kaunsa") || msg.contains("doctor chahiye") || msg.contains("which specialist")) {
            return "Please describe your "
                + "symptoms and I will "
                + "find the right "
                + "department for you!\n\n"
                + "Example: 'I have chest "
                + "pain and breathlessness'";
        }

        // ─── 4. Available doctors ───
        if (msg.contains("available doctor") || msg.contains("doctor list") || msg.contains("show doctors")) {
            return "Please tell me your "
                + "symptoms so I can "
                + "find the right "
                + "specialist for you!";
        }

        // ─── 5. Appointment ───
        if (msg.contains("appointment") || msg.contains("book") || msg.contains("schedule")) {
            return "To book an appointment:"
                + "\n1. Go to the Hospital "
                + "Management section\n"
                + "2. Click Appointments tab"
                + "\n3. Select your doctor\n"
                + "4. Choose date and time"
                + "\n\nOr tell me your "
                + "symptoms and I will "
                + "suggest which doctor "
                + "to book!";
        }

        // ─── 6. Emergency ───
        if (msg.contains("emergency") || msg.contains("urgent") || msg.contains("ambulance") || msg.contains("112")) {
            return "EMERGENCY CONTACTS:\n\n"
                + "Ambulance: 108\n"
                + "Emergency: 112\n"
                + "Hospital: 1800-XXX-XXXX\n\n"
                + "Emergency Ward: Ground "
                + "Floor, Gate 1\n\n"
                + "Please go to Emergency"
                + " Ward immediately!";
        }

        // ─── 7. Blood donation ───
        if (msg.contains("blood") || msg.contains("donor") || msg.contains("donate")) {
            return "For blood donation:\n\n"
                + "Visit Blood Donation "
                + "section to:\n"
                + "Find donors by blood "
                + "group\n"
                + "Register as a donor\n"
                + "Search donors by city\n\n"
                + "Blood Bank: Room 005, "
                + "Ground Floor\n"
                + "Phone: 1800-XXX-XXXX";
        }

        // ─── 8. Timings ───
        if (msg.contains("timing") || msg.contains("hours") || msg.contains("open") || msg.contains("time")) {
            return "Hospital Timings:\n\n"
                + "OPD: 9 AM – 5 PM\n"
                + "(Mon to Sat)\n\n"
                + "Emergency: 24/7\n"
                + "Blood Bank: 24/7\n"
                + "Pharmacy: 8 AM – 10 PM\n\n"
                + "Sunday: Emergency only";
        }

        // ─── 9. Fees ───
        if (msg.contains("fee") || msg.contains("cost") || msg.contains("charge") || msg.contains("price") || msg.contains("payment")) {
            return "Consultation Fees:\n\n"
                + "General OPD: Rs. 200\n"
                + "Specialist: Rs. 500\n"
                + "Emergency: Rs. 300\n\n"
                + "For payment, visit the"
                + " Payment Center in our"
                + " app or billing counter"
                + " on Ground Floor.";
        }

        // ─── 10. Thanks ───
        if (msg.contains("thank") || msg.contains("thanks") || msg.contains("shukriya") || msg.contains("dhanyawad")) {
            return "You are welcome! "
                + "Take care and stay "
                + "healthy!\n\n"
                + "If you need anything "
                + "else, just type your "
                + "symptoms and I will "
                + "guide you!";
        }

        // ─── 11. Bye ───
        if (msg.contains("bye") || msg.contains("goodbye") || msg.contains("exit")) {
            return "Goodbye! Stay healthy "
                + "and take care!\n\n"
                + "Visit us again at "
                + "eMediHub. Your health"
                + " is our priority!";
        }

        // ─── 12. Help ───
        if (msg.contains("help") || msg.contains("what can") || msg.contains("kya kar")) {
            return "I can help you with:\n\n"
                + "Symptom checking and "
                + "department guidance\n"
                + "Finding right doctor\n"
                + "Appointment booking\n"
                + "Hospital timings\n"
                + "Blood donation info\n"
                + "Emergency contacts\n"
                + "Fee information\n\n"
                + "Just describe your "
                + "symptoms and I will "
                + "find the right doctor!";
        }

        // ─── DEFAULT ───
        return "I am sorry, I did not "
            + "understand.\n\n"
            + "Please describe your "
            + "symptoms like:\n"
            + "I have headache and fever\n"
            + "My chest is paining\n"
            + "I have skin rash\n\n"
            + "I will find the right "
            + "department and doctor "
            + "for you!\n\n"
            + "Emergency: Call 112";
    }

    // ─── BUILD DOCTOR RESPONSE ───
    private static String buildDoctorResponse(SymptomChecker.DepartmentResult result, String msg) {

        StringBuilder reply = new StringBuilder();

        // Emergency warning first
        if (result.isEmergency) {
            reply.append("⚠ " + result.emergencyMsg + "\n\n");
        }

        // Department info
        reply.append("Department: ").append(result.department).append("\n");
        reply.append(result.reason).append("\n\n");

        // Find doctors from DB
        List<String[]> doctors = doctorDAO.findByDepartment(result.department);

        if (!doctors.isEmpty()) {
            reply.append("Available Doctors:\n");
            reply.append("─".repeat(28) + "\n");

            for (String[] doc : doctors) {
                String name   = doc[0];
                String spec   = doc[1];
                String phone  = doc[2];
                String room   = doc[3];
                String status = doc[4];

                reply.append("Dr. " + name + "\n");
                reply.append("Specialization: " + spec + "\n");
                reply.append("Room: " + room + "\n");
                reply.append("Status: " + status + "\n");
                reply.append("Phone: " + phone + "\n");
                reply.append("─".repeat(28) + "\n");
            }
        } else {
            reply.append(
                "No doctors currently "
                + "listed for this "
                + "department.\n"
                + "Please visit Reception"
                + " for assistance.\n\n");
        }

        // Booking tip
        if (!result.isEmergency) {
            reply.append(
                "\nTo book appointment:\n"
                + "Go to Hospital "
                + "Management → "
                + "Appointments tab");
        }

        return reply.toString();
    }
}