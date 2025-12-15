package com.ai.chatbot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatbotEngine {

    // Map to store Keywords (Regex Pattern) -> Intent/Answer
    private final Map<Pattern, String> knowledgeBase;

    public ChatbotEngine() {
        this.knowledgeBase = new HashMap<>();
        loadKnowledgeBase();
    }

    // --- Rule-Based Training ---
    // The order of insertion can be used for prioritization in matching
    private void loadKnowledgeBase() {
        // 1. Greetings and Basic Conversation
        knowledgeBase.put(Pattern.compile("^(hello|hi|hey|greetings|good morning|good evening)", Pattern.CASE_INSENSITIVE),
                "Hello! I am a simple Java chatbot. How can I help you today?");
        knowledgeBase.put(Pattern.compile("^(how are you|how do you do)", Pattern.CASE_INSENSITIVE),
                "I am a program, but thank you for asking! I'm running perfectly.");
        knowledgeBase.put(Pattern.compile("^(thank you|thanks|that's all)", Pattern.CASE_INSENSITIVE),
                "You're very welcome! Is there anything else I can assist with?");
        knowledgeBase.put(Pattern.compile("^(bye|goodbye|exit|close)", Pattern.CASE_INSENSITIVE),
                "Goodbye! Have a great day.");

        // 2. FAQ Section: Business Hours (Keywords: hours, schedule, open)
        knowledgeBase.put(Pattern.compile("(hours|schedule|open|close)", Pattern.CASE_INSENSITIVE),
                "Our business hours are Monday to Friday, 9:00 AM to 5:00 PM IST.");

        // 3. FAQ Section: Contact Information (Keywords: contact, email, phone)
        knowledgeBase.put(Pattern.compile("(contact|email|phone|support)", Pattern.CASE_INSENSITIVE),
                "You can reach us at support@example.com or call 555-1234 for direct support.");

        // 4. FAQ Section: Product Info/Pricing (Keywords: product, service, pricing)
        knowledgeBase.put(Pattern.compile("(product|service|pricing)", Pattern.CASE_INSENSITIVE),
                "We offer various products/services. Please visit our main website page for the latest pricing.");

        // 5. Basic Bot Identity
        knowledgeBase.put(Pattern.compile("(who are you|your name|what is your name|you a bot)", Pattern.CASE_INSENSITIVE),
                "I am Chatbot v1.0, a Java-based interactive assistant designed to answer your FAQs.");
    }

    /**
     * The core method to process user input and return a response.
     * Implements a simple form of NLP via regex-based keyword matching.
     * @param input The user's text.
     * @return The chatbot's response.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Please type a message.";
        }

        // Simple Pre-processing (NLP technique)
        String cleanedInput = input.trim().toLowerCase();

        // Check against the knowledge base (Rule-based logic)
        for (Map.Entry<Pattern, String> entry : knowledgeBase.entrySet()) {
            Matcher matcher = entry.getKey().matcher(cleanedInput);
            if (matcher.find()) {
                // Return the first match found (prioritization)
                return entry.getValue();
            }
        }

        // Default or "I don't know" response
        return "I apologize, but I don't have a specific answer for that FAQ yet. Could you try rephrasing?";
    }
    
    /**
     * Helper to check if the response indicates the user wants to end the chat.
     */
    public boolean shouldEndChat(String input) {
        String cleanedInput = input.trim().toLowerCase();
        
        // Simple regex check for exit keywords
        Pattern exitPattern = Pattern.compile("^(bye|goodbye|exit|close|that's all)", Pattern.CASE_INSENSITIVE);
        return exitPattern.matcher(cleanedInput).find();
    }
}