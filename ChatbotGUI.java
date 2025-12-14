package com.ai.chatbot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

public class ChatbotGUI extends JFrame {

    private final ChatbotEngine chatbotEngine;
    private final JTextPane chatPane; // Using JTextPane for rich text formatting (colors, alignment)
    private final JTextField inputField;
    private final JButton sendButton;

    // Custom Colors
    private static final Color BOT_BACKGROUND = new Color(229, 237, 240); // Light blue-grey
    private static final Color USER_BACKGROUND = new Color(0, 122, 255);  // Professional blue
    private static final Color BOT_TEXT_COLOR = Color.BLACK;
    private static final Color USER_TEXT_COLOR = Color.WHITE;
    private static final Color PANEL_BACKGROUND = new Color(250, 250, 250); // Off-white for background

    public ChatbotGUI() {
        // Initialize the backend engine
        this.chatbotEngine = new ChatbotEngine();

        // --- 1. Frame Setup ---
        setTitle("Professional Chatbot AI Assistant");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(450, 600));

        // --- 2. Chat Display Area (JTextPane for Bubbles) ---
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(PANEL_BACKGROUND);
        
        // Disable wrapping for better text control (though we handle line breaks manually)
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Add padding around the chat area
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.CENTER);

        // --- 3. Input Panel (Flat Design) ---
        JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10) // Internal padding
        ));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(USER_BACKGROUND);
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false); // Flat button design

        // Action Listener for the Send Button and Enter Key
        ActionListener sendAction = (ActionEvent e) -> sendMessage();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction); 

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Initial Greeting
        appendMessage("Bot", chatbotEngine.getResponse("Hi"), false);

        // Finalize Frame
        setLocationRelativeTo(null); 
        setVisible(true);
    }
    
    // --- Helper Method for Chat Bubble Formatting ---
    private void appendMessage(String sender, String text, boolean isUser) {
        // Use a Document for JTextPane formatting
        StyledDocument doc = chatPane.getStyledDocument();
        
        // Define text attributes
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attributes, "Arial");
        StyleConstants.setFontSize(attributes, 15);
        StyleConstants.setLineSpacing(attributes, 0.3f);

        // Define bubble style based on sender
        SimpleAttributeSet bubbleAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(bubbleAttributes, isUser ? USER_TEXT_COLOR : BOT_TEXT_COLOR);
        StyleConstants.setBackground(bubbleAttributes, isUser ? USER_BACKGROUND : BOT_BACKGROUND);
        
        // Set alignment (User messages right-aligned, Bot messages left-aligned)
        StyleConstants.setAlignment(attributes, isUser ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);

        try {
            // Add a little vertical spacing before the new message
            doc.insertString(doc.getLength(), "\n", null);
            
            // Create the content panel for the bubble
            JPanel bubble = new JPanel(new BorderLayout());
            bubble.setBackground(isUser ? USER_BACKGROUND : BOT_BACKGROUND);
            bubble.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isUser ? USER_BACKGROUND : BOT_BACKGROUND, 10), // Padding/Bubble size
                new EmptyBorder(0, 0, 0, 0)
            ));
            
            // The text container
            JTextArea textArea = new JTextArea(text);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 15));
            textArea.setForeground(isUser ? USER_TEXT_COLOR : BOT_TEXT_COLOR);
            textArea.setBackground(isUser ? USER_BACKGROUND : BOT_BACKGROUND);
            
            // Add text area to the bubble panel
            bubble.add(textArea, BorderLayout.CENTER);

            // Create a wrapper panel to handle alignment within the JTextPane
            JPanel wrapper = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT));
            wrapper.setBackground(PANEL_BACKGROUND);
            wrapper.add(bubble);

            // Insert the component (the wrapper panel containing the bubble)
            doc.insertString(doc.getLength(), sender + ":\n", attributes);
            chatPane.setCaretPosition(doc.getLength()); // Keep caret at the end
            chatPane.insertComponent(wrapper);
            doc.insertString(doc.getLength(), "\n", null); // Final newline

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }

        // 1. Display User Message (right-aligned bubble)
        appendMessage("You", userInput, true);
        inputField.setText(""); 

        // 2. Get Bot Response (left-aligned bubble)
        String botResponse = chatbotEngine.getResponse(userInput);
        appendMessage("Bot", botResponse, false);
        
        // 3. Check for Conversation Completion
        if (chatbotEngine.shouldEndChat(userInput)) {
            inputField.setEnabled(false);
            sendButton.setEnabled(false);
            try {
                // Add final message in a neutral style
                StyledDocument doc = chatPane.getStyledDocument();
                SimpleAttributeSet attributes = new SimpleAttributeSet();
                StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
                StyleConstants.setForeground(attributes, Color.GRAY.darker());
                StyleConstants.setFontSize(attributes, 12);
                doc.insertString(doc.getLength(), "\n\n--- Chat session closed. Restart to begin a new session. ---\n", attributes);
            } catch (BadLocationException e) {
                // Ignore
            }
        }
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(ChatbotGUI::new);
    }
}