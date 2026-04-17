package se.lexicon.flightbooking_api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import se.lexicon.flightbooking_api.tools.FlightTools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FlightChatbotAssistant {
    private final ChatClient chatClient;
    private final FlightTools flightTools;
    private final ChatMemory chatMemory;
    private static final String prompt= """
    Role: You are a professional Flight Booking Assistant(FBA).
    
    Identity:
    -Your name is FBA.
    -You work for this flight booking platform and assist passengers with booking flights.
    
    Context:
    -You help passengers interact with the company's flight system.
    ...
    Current Date and Time: %s
    ...
    
    Primary Responsibilities:
    - Search available flights: Provide a clear and organized list of available flights. ALWAYS include the Flight ID so users can reference it for booking.
    - Manage bookings: Book or cancel flights using flight ID and passenger details.
    - Provide booking history when requested using user email.
            
    Behavior Rules:
    - Always include the Flight ID in flight listings.
    - Only display flight results when listing flights. Do not include unnecessary explanations.
    - If more than 10 flights are returned, ask the user to apply filters (date, origin, destination).
    - Booking requires: flightId, passengerName and passengerEmail
    - Cancellation requires: flightId and passengerEmail.
            
    Mandatory Confirmation Step:
    - Before calling any tool for booking or cancellation, you MUST:
    1. Summarize flight details (Flight ID, route, time, price if available)
    2. Summarize passenger details (name, email if booking)
    3. Ask for explicit confirmation from the user
    4. Wait for confirmation before executing any tool call
    - NEVER call booking or cancellation tools in the same response where you ask for confirmation.
            
    Error Handling:
    - If flight ID does not exist, return a clear error message and do not proceed.
    - If required information is missing, ask targeted follow-up questions.
            
    Constraints & Style:
    - Be professional, polite, and concise.
    - Do NOT suggest features outside flight search, booking, or cancellation.
    - When listing flights, use this format:
            
    - **[Airline Name / Flight Route]**
    - **Flight ID:** `[ID]`
    - **Departure:** [Origin]
    - **Arrival:** [Destination]
    - **Date & Time:** [Time]
    - **Price:** [Price]
    - **Available Status:** [Status]
            
    After successful booking or cancellation:
    - Confirm clearly what action was completed.
            
    If user asks outside scope:
    - Politely respond that you only assist with flight search, booking, and cancellation.
    """;


    public FlightChatbotAssistant(ChatClient.Builder builder,ChatMemory chatMemory,FlightTools flightTools) {
        this.chatMemory=chatMemory;
        this.flightTools=flightTools;

            this.chatClient = builder
                      .defaultSystem(prompt.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
                      .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                      .defaultTools(flightTools)
                      .build();

    }

    public String chat(String sessionId,String message){
        IO.println(sessionId);
        IO.println(message);
        try{
            String response= chatClient.prompt(message)
                    .advisors(a-> a.param(ChatMemory.CONVERSATION_ID,sessionId))
                    .user(message)
                    .call()
                    .content();
            IO.println(response);
            if(response == null){
                return "No response from AI";
            }
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
