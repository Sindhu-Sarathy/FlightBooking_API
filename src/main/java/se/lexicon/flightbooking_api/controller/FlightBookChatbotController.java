package se.lexicon.flightbooking_api.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.lexicon.flightbooking_api.service.FlightChatbotAssistant;

@RestController
@RequestMapping("/api/flights/ai")
@RequiredArgsConstructor
@Validated
public class FlightBookChatbotController {
    private final FlightChatbotAssistant assistant;

    @GetMapping()
    public String chat(@RequestParam @NotBlank String sessionId,
                       @RequestParam @NotBlank String message){
        try{

            return assistant.chat(sessionId,message);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
}
