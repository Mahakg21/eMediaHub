package com.emediHub.chatbot;

import java.util.*;

public class SymptomChecker {

    public static class DepartmentResult {
        public String department;
        public String reason;
        public boolean isEmergency;
        public String emergencyMsg;

        public DepartmentResult(
                String department,
                String reason,
                boolean isEmergency,
                String emergencyMsg) {
            this.department   = department;
            this.reason       = reason;
            this.isEmergency  = isEmergency;
            this.emergencyMsg = emergencyMsg;
        }
    }

    // ✅ Fix 1 — Map< added
    private static final Map<List<String>, DepartmentResult>
        SYMPTOM_MAP = new LinkedHashMap<>();

    static {

        SYMPTOM_MAP.put(
            Arrays.asList(
                "chest pain", "heart attack",
                "severe chest",
                "crushing pain",
                "jaw pain", "left arm pain",
                "sweating chest"),
            new DepartmentResult(
                "Cardiology",
                "Chest pain can indicate "
                + "serious heart condition",
                true,
                "EMERGENCY! Call 112 NOW! "
                + "Go to Emergency Ward "
                + "immediately!"));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "stroke", "face drooping",
                "arm weakness", "speech",
                "sudden numbness",
                "vision loss sudden",
                "worst headache"),
            new DepartmentResult(
                "Neurology",
                "These are stroke symptoms",
                true,
                "EMERGENCY! Call 112 NOW! "
                + "Every minute matters "
                + "in stroke!"));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "unconscious", "fainted",
                "not breathing",
                "severe bleeding",
                "accident", "injury",
                "fracture", "broken bone"),
            new DepartmentResult(
                "Emergency Ward",
                "Immediate medical "
                + "attention required",
                true,
                "EMERGENCY! Go to Emergency"
                + " Ward immediately! "
                + "Call 112!"));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "heart", "palpitation",
                "irregular heartbeat",
                "shortness of breath",
                "breathless",
                "chest tightness",
                "heart rate", "pulse",
                "blood pressure",
                "bp high", "hypertension",
                "bp low", "edema",
                "swollen legs",
                "fatigue breathless"),
            new DepartmentResult(
                "Cardiology",
                "Your symptoms suggest "
                + "a heart-related issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "headache", "migraine",
                "head pain", "dizziness",
                "vertigo", "seizure",
                "epilepsy", "tremor",
                "shaking", "memory loss",
                "confusion", "numbness",
                "tingling", "weakness",
                "paralysis", "nerve pain",
                "brain", "fainting"),
            new DepartmentResult(
                "Neurology",
                "Your symptoms suggest "
                + "a neurological issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "joint pain", "knee pain",
                "back pain", "spine",
                "bone pain", "muscle pain",
                "shoulder pain", "hip pain",
                "neck pain", "stiff",
                "swollen joint", "arthritis",
                "sprain", "ligament",
                "slip disc", "sciatica",
                "ankle pain", "wrist pain",
                "elbow pain", "fracture"),
            new DepartmentResult(
                "Orthopedics",
                "Your symptoms suggest "
                + "a bone or joint issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "skin rash", "itching",
                "acne", "pimple",
                "skin infection", "eczema",
                "psoriasis", "redness skin",
                "dry skin", "skin allergy",
                "hives", "urticaria",
                "hair fall", "hair loss",
                "dandruff", "fungal",
                "ringworm", "warts",
                "mole", "pigmentation",
                "skin patch", "blister"),
            new DepartmentResult(
                "Dermatology",
                "Your symptoms suggest "
                + "a skin-related issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "ear pain", "ear infection",
                "hearing loss", "tinnitus",
                "ringing ear", "sore throat",
                "throat pain", "tonsil",
                "nose bleed", "nasal block",
                "sinusitis", "runny nose",
                "sneezing", "congestion",
                "voice loss", "hoarse",
                "swallowing problem",
                "smell loss", "taste loss"),
            new DepartmentResult(
                "ENT",
                "Your symptoms suggest "
                + "an Ear Nose Throat issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "stomach pain", "abdominal",
                "vomiting", "nausea",
                "diarrhea", "constipation",
                "acidity", "heartburn",
                "bloating", "gas",
                "indigestion", "ulcer",
                "liver", "jaundice",
                "yellow eyes", "hepatitis",
                "appendix", "ibs", "crohn",
                "rectal bleeding",
                "black stool",
                "loose motion"),
            new DepartmentResult(
                "Gastroenterology",
                "Your symptoms suggest "
                + "a digestive system issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "cough", "dry cough",
                "wet cough", "cold",
                "fever cough", "asthma",
                "wheezing", "breathing",
                "lungs", "pneumonia",
                "tuberculosis", "tb",
                "blood cough", "sputum",
                "chest infection",
                "respiratory",
                "oxygen low", "covid",
                "bronchitis"),
            new DepartmentResult(
                "Pulmonology",
                "Your symptoms suggest "
                + "a lung or breathing issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "child fever", "baby",
                "infant", "toddler",
                "kids", "children",
                "child cough", "child pain",
                "child vomiting",
                "vaccination", "growth",
                "child not eating",
                "child diarrhea",
                "newborn", "pediatric"),
            new DepartmentResult(
                "Pediatrics",
                "For children's health, "
                + "a pediatrician is best",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "depression", "anxiety",
                "stress", "mental",
                "panic attack", "phobia",
                "ocd", "bipolar",
                "schizophrenia", "insomnia",
                "sleep problem", "suicide",
                "self harm", "mood swing",
                "hallucination", "paranoid",
                "eating disorder",
                "addiction", "trauma"),
            new DepartmentResult(
                "Psychiatry",
                "Your symptoms suggest "
                + "a mental health issue. "
                + "Seeking help is strength!",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "periods", "menstrual",
                "irregular periods",
                "pcos", "pcod",
                "pregnancy", "pregnant",
                "vaginal", "uterus",
                "ovary", "menopause",
                "breast pain", "fertility",
                "white discharge",
                "pelvic pain"),
            new DepartmentResult(
                "Gynecology",
                "Your symptoms suggest "
                + "a gynecological issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "urine problem",
                "burning urine",
                "frequent urination",
                "kidney stone",
                "kidney pain",
                "blood in urine", "uti",
                "prostate", "bladder",
                "kidney infection",
                "renal", "dialysis"),
            new DepartmentResult(
                "Urology",
                "Your symptoms suggest "
                + "a urinary system issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "eye pain",
                "blurred vision",
                "red eye", "eye infection",
                "watery eyes",
                "eye allergy", "cataract",
                "glaucoma",
                "vision problem", "specs",
                "eye strain", "dry eyes",
                "double vision", "squint"),
            new DepartmentResult(
                "Ophthalmology",
                "Your symptoms suggest "
                + "an eye-related issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "diabetes", "sugar",
                "thyroid", "weight gain",
                "weight loss sudden",
                "hormone", "insulin",
                "adrenal", "pituitary",
                "excessive thirst",
                "excessive hunger",
                "fatigue extreme"),
            new DepartmentResult(
                "Endocrinology",
                "Your symptoms suggest "
                + "a hormonal or metabolic"
                + " issue",
                false, null));

        SYMPTOM_MAP.put(
            Arrays.asList(
                "fever", "cold", "flu",
                "body ache", "weakness",
                "fatigue", "tired",
                "general checkup",
                "checkup",
                "not feeling well",
                "malaria", "dengue",
                "typhoid", "viral",
                "infection", "antibiotics"),
            new DepartmentResult(
                "General Medicine",
                "A general physician "
                + "can help with your "
                + "symptoms",
                false, null));
    }

    // ✅ Fix 2 — Map.Entry< added
    public static DepartmentResult checkSymptoms(String input) {
        if (input == null || input.trim().isEmpty())
            return null;

        String msg =input.toLowerCase().trim();

        Map<DepartmentResult, Integer> scores = new LinkedHashMap<>();

        for (Map.Entry<List<String>, DepartmentResult> entry : SYMPTOM_MAP.entrySet()) {
            int score = 0;
            for (String keyword : entry.getKey()) {
                if (msg.contains(keyword)) {
                    score += entry.getValue().isEmergency ? 10 : 1;
                }
            }
            if (score > 0)
                scores.put(entry.getValue(),score);
        }

        if (scores.isEmpty()) return null;

        return scores.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    public static List<String> getAllDepartments() {
        return Arrays.asList(
            "Cardiology", "Neurology",
            "Orthopedics", "Dermatology",
            "ENT", "Gastroenterology",
            "Pulmonology", "Pediatrics",
            "Psychiatry", "Gynecology",
            "Urology", "Ophthalmology",
            "Endocrinology",
            "General Medicine",
            "Emergency Ward");
    }
}