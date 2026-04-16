package se.lexicon.flightbooking_api.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import se.lexicon.flightbooking_api.dto.AvailableFlightDTO;
import se.lexicon.flightbooking_api.dto.BookFlightRequestDTO;
import se.lexicon.flightbooking_api.dto.FlightBookingDTO;
import se.lexicon.flightbooking_api.dto.FlightListDTO;
import se.lexicon.flightbooking_api.service.FlightBookingService;

import java.util.List;

@Component
public class FlightTools {

    private final FlightBookingService flightBookingService;


    public FlightTools(FlightBookingService flightBookingService) {
        this.flightBookingService = flightBookingService;
    }


    //Find all flights
    @Tool(name = "findAllFlights" , description = "Find all the flight details")
    public List<FlightListDTO> findAllFlights(){
        return flightBookingService.findAll();
    }

    //Find available flights
    @Tool(name = "findAvailableFlights" , description = "Find all available flights details")
    public List<AvailableFlightDTO> findAvailableFlights(){
        return  flightBookingService.findAvailableFlights();
    }

    //Find Bookings detail by email
    @Tool(name = "findBookingsByEmail", description = "Find the flight bookings by email")
    public List<FlightBookingDTO> findBookingsByEmail(String email){
        return flightBookingService.findBookingsByEmail(email);
    }

    //Book Flight
    @Tool(name = "bookingFlight", description = "Book a flight for a passenger")
    public FlightBookingDTO bookingFlight(Long flightId,String passengerName, String passengerEmail ){
        BookFlightRequestDTO requestDTO=new BookFlightRequestDTO(passengerName,passengerEmail);
        return  flightBookingService.bookFlight(flightId,requestDTO);
    }

    //Cancel Flight
    @Tool(name = "cancelFlight", description = "Cancel a flight for a passenger")
    public String cancelFlight(Long flightId, String passengerEmail ){
        flightBookingService.cancelFlight(flightId,passengerEmail);
        return "Booking cancelled successfully for " + passengerEmail;
    }
}
