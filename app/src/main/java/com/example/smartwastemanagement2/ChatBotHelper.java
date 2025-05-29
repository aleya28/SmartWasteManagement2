package com.example.smartwastemanagement2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages chat bot responses and conversation flow
 */
public class ChatBotHelper {
    
    // List of possible responses for different topics
    private final Map<String, List<String>> responseCategories = new HashMap<>();
    
    // List of common keywords to detect topics
    private final Map<String, String> keywordToCategory = new HashMap<>();
    
    public ChatBotHelper() {
        initializeResponses();
        initializeKeywords();
    }
    
    /**
     * Set up response categories
     */
    private void initializeResponses() {
        // Greeting responses
        List<String> greetings = new ArrayList<>();
        greetings.add("Hello! How can I assist you today with waste management?");
        greetings.add("Hi there! What can I help you with?");
        greetings.add("Welcome to Smart Waste Management! How may I help you?");
        responseCategories.put("greeting", greetings);
        
        // Booking responses
        List<String> booking = new ArrayList<>();
        booking.add("You can schedule a waste collection by going to the Booking tab and providing your details.");
        booking.add("To book a waste collection service, please go to the Booking section and submit your request with pickup date and time.");
        booking.add("Our waste collection booking process is simple - just select the Booking tab, enter waste details, and choose your preferred pickup time.");
        responseCategories.put("booking", booking);
        
        // Payment responses
        List<String> payment = new ArrayList<>();
        payment.add("Payment can be made through the app using credit/debit cards, e-wallets, or online banking.");
        payment.add("You can pay for waste collection services directly in the app. We accept various payment methods for your convenience.");
        payment.add("To make a payment, go to your bookings, select the submission, and click on the Pay Now button.");
        responseCategories.put("payment", payment);
        
        // Recycling responses
        List<String> recycling = new ArrayList<>();
        recycling.add("We recycle paper, plastic, glass, metal, and electronic waste. Make sure to segregate them properly.");
        recycling.add("Proper segregation is important for effective recycling. Please separate your waste into different categories before collection.");
        recycling.add("Our recycling process handles most household materials. For special items like batteries or large appliances, please mention in your booking notes.");
        responseCategories.put("recycling", recycling);
        
        // Location responses
        List<String> location = new ArrayList<>();
        location.add("You can find our recycling centers by going to the 'Find Us' section in the app.");
        location.add("Our recycling centers are displayed on the map in the app. You can see the nearest one to your location.");
        location.add("To locate the nearest collection center, please use the map feature in our app.");
        responseCategories.put("location", location);
        
        // Services responses - added to handle questions about available services
        List<String> services = new ArrayList<>();
        services.add("We offer waste collection, recycling, waste segregation education, and e-waste management services.");
        services.add("Our services include regular waste pickup, recycling programs, hazardous waste disposal, and waste management consulting.");
        services.add("Smart Waste Management provides services like scheduled waste collection, recycling, community cleanup events, and waste reduction consulting.");
        responseCategories.put("services", services);
        
        // Contact responses
        List<String> contact = new ArrayList<>();
        contact.add("You can contact our customer service at support@smartwaste.com or call us at 1-800-RECYCLE.");
        contact.add("For any questions or assistance, email us at help@smartwaste.com or use the Contact Us form in the app.");
        contact.add("Our customer support team is available Monday to Friday, 9 AM to 6 PM. Reach us at 1-800-RECYCLE.");
        responseCategories.put("contact", contact);
        
        // Complaint responses
        List<String> complaints = new ArrayList<>();
        complaints.add("I'm sorry to hear that. Please provide details about your issue, and we'll address it promptly.");
        complaints.add("We apologize for any inconvenience. Please share more details about your concern so we can help resolve it.");
        complaints.add("Your feedback is important to us. Please let us know what went wrong, and we'll work to make it right.");
        responseCategories.put("complaint", complaints);
        
        // Fallback responses
        List<String> fallback = new ArrayList<>();
        fallback.add("I'm not sure I understand. Could you please rephrase or ask about our services, booking, payment, recycling, or locations?");
        fallback.add("Sorry, I didn't catch that. Try asking about our services, booking process, or recycling guidelines.");
        fallback.add("I'm still learning! Could you try asking in a different way or choose from topics like services, booking, payment, or recycling centers?");
        responseCategories.put("fallback", fallback);
    }
    
    /**
     * Set up keyword mappings to categories
     */
    private void initializeKeywords() {
        // Greeting keywords
        keywordToCategory.put("hello", "greeting");
        keywordToCategory.put("hi", "greeting");
        keywordToCategory.put("hey", "greeting");
        keywordToCategory.put("greetings", "greeting");
        
        // Booking keywords
        keywordToCategory.put("book", "booking");
        keywordToCategory.put("schedule", "booking");
        keywordToCategory.put("pickup", "booking");
        keywordToCategory.put("collection", "booking");
        keywordToCategory.put("collect", "booking");
        keywordToCategory.put("appointment", "booking");
        
        // Payment keywords
        keywordToCategory.put("pay", "payment");
        keywordToCategory.put("payment", "payment");
        keywordToCategory.put("cost", "payment");
        keywordToCategory.put("price", "payment");
        keywordToCategory.put("fee", "payment");
        keywordToCategory.put("charge", "payment");
        keywordToCategory.put("money", "payment");
        keywordToCategory.put("bill", "payment");
        
        // Recycling keywords
        keywordToCategory.put("recycle", "recycling");
        keywordToCategory.put("recycling", "recycling");
        keywordToCategory.put("waste", "recycling");
        keywordToCategory.put("trash", "recycling");
        keywordToCategory.put("garbage", "recycling");
        keywordToCategory.put("segregate", "recycling");
        
        // Location keywords
        keywordToCategory.put("location", "location");
        keywordToCategory.put("where", "location");
        keywordToCategory.put("center", "location");
        keywordToCategory.put("centre", "location");
        keywordToCategory.put("find", "location");
        keywordToCategory.put("map", "location");
        keywordToCategory.put("nearby", "location");
        
        // Services keywords
        keywordToCategory.put("service", "services");
        keywordToCategory.put("services", "services");
        keywordToCategory.put("offer", "services");
        keywordToCategory.put("provide", "services");
        keywordToCategory.put("available", "services");
        keywordToCategory.put("what", "services"); // Common for "what services" questions
        
        // Contact keywords
        keywordToCategory.put("contact", "contact");
        keywordToCategory.put("phone", "contact");
        keywordToCategory.put("email", "contact");
        keywordToCategory.put("call", "contact");
        keywordToCategory.put("reach", "contact");
        keywordToCategory.put("support", "contact");
        keywordToCategory.put("help", "contact");
        
        // Complaint keywords
        keywordToCategory.put("complaint", "complaint");
        keywordToCategory.put("issue", "complaint");
        keywordToCategory.put("problem", "complaint");
        keywordToCategory.put("wrong", "complaint");
        keywordToCategory.put("bad", "complaint");
        keywordToCategory.put("unhappy", "complaint");
        keywordToCategory.put("disappointed", "complaint");
    }
    
    /**
     * Get a response based on the user's message with improved understanding
     */
    public String getResponse(String userMessage) {
        // Log the incoming message for debugging
        System.out.println("ChatBot received: " + userMessage);
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return getRandomResponse("fallback");
        }
        
        String processedMessage = userMessage.toLowerCase().trim();
        
        // Direct handling of some very specific short queries
        if (processedMessage.equals("services") || 
            processedMessage.equals("service") ||
            processedMessage.equals("what services") ||
            processedMessage.equals("what service")) {
            return getRandomResponse("services");
        }
        
        if (processedMessage.equals("payment") ||
            processedMessage.equals("pay")) {
            return getRandomResponse("payment");
        }
        
        // For "what is X" questions
        if ((processedMessage.startsWith("what is") || processedMessage.startsWith("what are")) && 
            processedMessage.contains("service")) {
            return getRandomResponse("services");
        }
        
        // General keyword detection
        String category = detectCategory(processedMessage);
        
        // Log the detected category for debugging
        System.out.println("ChatBot category detected: " + category);
        
        return getRandomResponse(category);
    }
    
    /**
     * Detect the message category based on keywords with improved algorithm
     */
    private String detectCategory(String message) {
        // Check if message is a single word or very short phrase
        boolean isShortQuery = message.trim().split("\\s+").length <= 2;
        
        // Track keyword matches with their priority scores
        Map<String, Integer> categoryScores = new HashMap<>();
        
        // Process the message for keywords
        for (Map.Entry<String, String> entry : keywordToCategory.entrySet()) {
            String keyword = entry.getKey();
            String category = entry.getValue();
            
            // Calculate match priority - exact matches get highest priority
            if (message.equals(keyword)) {
                // Exact match - highest priority
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 10);
            } else if (message.contains(" " + keyword + " ") || 
                       message.startsWith(keyword + " ") || 
                       message.endsWith(" " + keyword)) {
                // Whole word match - high priority
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 5);
            } else if (message.contains(keyword)) {
                // Partial match - lower priority
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 2);
            }
        }
        
        // Special handling for common question patterns
        if (message.startsWith("what") || message.startsWith("how") || 
            message.startsWith("where") || message.startsWith("when")) {
            
            // "What is" or "What are" questions about services
            if ((message.contains("what is") || message.contains("what are")) && 
                 message.contains("service")) {
                return "services";
            }
            
            // Questions about payment
            if (message.contains("pay") || message.contains("cost") || message.contains("price")) {
                return "payment";
            }
            
            // Questions about booking
            if (message.contains("book") || message.contains("schedule")) {
                return "booking";
            }
        }
        
        // Special case for single-word "services" query
        if (isShortQuery && message.contains("service")) {
            return "services";
        }
        
        // Find the highest scoring category
        String bestCategory = "fallback";
        int highestScore = 0;
        
        for (Map.Entry<String, Integer> score : categoryScores.entrySet()) {
            if (score.getValue() > highestScore) {
                highestScore = score.getValue();
                bestCategory = score.getKey();
            }
        }
        
        return bestCategory;
    }
    
    /**
     * Get a random response from the specified category
     */
    private String getRandomResponse(String category) {
        List<String> responses = responseCategories.get(category);
        if (responses == null || responses.isEmpty()) {
            responses = responseCategories.get("fallback");
        }
        
        Random random = new Random();
        int index = random.nextInt(responses.size());
        return responses.get(index);
    }
}
