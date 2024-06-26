package com.taller.psico.api;

import com.taller.psico.bl.Authbl;
import com.taller.psico.bl.QuoteBl;
import com.taller.psico.dto.IsAvailableDTO;
import com.taller.psico.dto.QuotesDTO;
import com.taller.psico.dto.QuotesObtenerDTO;
import com.taller.psico.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/quote")
public class QuoteApi {
    private static final Logger logger = LoggerFactory.getLogger(QuoteApi.class);
    private final QuoteBl quoteBl;
    private final Authbl authBl;  // Authentication service

    @Autowired
    public QuoteApi(QuoteBl quoteBl, Authbl authBl) {
        this.quoteBl = quoteBl;
        this.authBl = authBl;  // Initialize the authentication service
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<QuotesDTO>> createQuote(@RequestBody QuotesDTO quoteDTO, @RequestHeader("Authorization") String token) {
        logger.info("Request to create a new quote received: {}", quoteDTO);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for creating a quote.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            QuotesDTO result = quoteBl.createQuote(quoteDTO);
            if (result == null) {
                logger.info("No more slots available for this time.");
                return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "No more slots available for this time."));
            }
            logger.info("Quote created successfully with ID: {}", result.getQuotesId());
            return ResponseEntity.ok(new ResponseDTO<>(200, result, "Cita creada exitosamente."));
        } catch (Exception e) {
            logger.error("Error while creating quote: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al crear cita: " + e.getMessage()));
        }
    }

    //Decir un un availability esta disponible por fecha de quote y id de availability
    @PostMapping("/is-available")
    public ResponseEntity<ResponseDTO<Boolean>> isAvailable(@RequestBody IsAvailableDTO isAvailableDTO, @RequestHeader("Authorization") String token) {
        logger.info("Checking if availability is available for quote");
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for checking availability.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            boolean isAvailable = quoteBl.isAvailable(isAvailableDTO);
            return ResponseEntity.ok(new ResponseDTO<>(200, isAvailable, "Disponibilidad verificada exitosamente."));
        } catch (Exception e) {
            logger.error("Error while checking availability for quote: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al verificar disponibilidad para cita: " + e.getMessage()));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<QuotesObtenerDTO>>> getAllQuotes(@RequestHeader("Authorization") String token) {
        logger.info("Fetching all quotes");
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesObtenerDTO> quotes = quoteBl.getAllQuotes();
            logger.info("All quotes retrieved successfully");
            return ResponseEntity.ok(new ResponseDTO<>(200, quotes, "Todas las citas recuperadas exitosamente."));
        } catch (Exception e) {
            logger.error("Error while retrieving all quotes: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar las citas: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<List<QuotesDTO>>> getUserQuotes(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quotes for user with ID: {}", userId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching user quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesDTO> userQuotes = quoteBl.getUserQuotes(userId);
            logger.info("Quotes for user ID {} retrieved successfully", userId);
            return ResponseEntity.ok(new ResponseDTO<>(200, userQuotes, "Citas del usuario recuperadas exitosamente."));
        } catch (Exception e) {
            logger.error("Error while retrieving quotes for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar las citas del usuario: " + e.getMessage()));
        }
    }

    //obtener todas la cita de un usuario con estado false
    @GetMapping("/user/historial/{userId}")
    public ResponseEntity<ResponseDTO<List<QuotesDTO>>> getUserQuotesHistorial(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quotes for user with ID: {}", userId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching user quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesDTO> userQuotes = quoteBl.getUserQuotesHistorial(userId,1);
            logger.info("Quotes for user ID {} retrieved successfully", userId);
            return ResponseEntity.ok(new ResponseDTO<>(200, userQuotes, "Citas del usuario recuperadas exitosamente."));
        } catch (Exception e) {
            logger.error("Error while retrieving quotes for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar las citas del usuario: " + e.getMessage()));
        }
    }

    //obtener todas la cita de un usuario docente con estado false
    @GetMapping("/therapist/historial/{therapistId}")
    public ResponseEntity<ResponseDTO<List<QuotesDTO>>> getUserQuotesHistorialTherapist(@PathVariable int therapistId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quotes for therapist with ID: {}", therapistId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching user quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesDTO> userQuotes = quoteBl.getUserQuotesHistorial(therapistId, 2);
            logger.info("Quotes for therapist ID {} retrieved successfully", therapistId);
            return ResponseEntity.ok(new ResponseDTO<>(200, userQuotes, "Citas del usuario recuperadas exitosamente."));
        } catch (Exception e) {
            logger.error("Error while retrieving quotes for therapist ID {}: {}", therapistId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO<>(500, null, "Error al recuperar las citas del usuario: " + e.getMessage()));
        }
    }



    @GetMapping("/{quotesId}")
    public ResponseEntity<ResponseDTO<QuotesDTO>> getQuoteById(@PathVariable int quotesId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quote with ID: {}", quotesId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching quote.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            QuotesDTO quote = quoteBl.getQuoteById(quotesId);
            if (quote != null) {
                logger.info("Quote with ID {} retrieved successfully", quotesId);
                return ResponseEntity.ok(new ResponseDTO<>(200, quote, "Cita recuperada exitosamente."));
            } else {
                logger.warn("No quote found with ID: {}", quotesId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error while retrieving quote with ID {}: {}", quotesId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar la cita: " + e.getMessage()));
        }
    }

    @PutMapping("/{quotesId}")
    public ResponseEntity<ResponseDTO<QuotesDTO>> updateQuote(@PathVariable int quotesId, @RequestBody QuotesDTO quoteDTO, @RequestHeader("Authorization") String token) {
        logger.info("Updating quote with ID: {}", quotesId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for updating a quote.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            QuotesDTO updatedQuote = quoteBl.updateQuote(quotesId, quoteDTO);
            logger.info("Quote with ID {} updated successfully", quotesId);
            return ResponseEntity.ok(new ResponseDTO<>(200, updatedQuote, "Cita actualizada exitosamente."));
        } catch (Exception e) {
            logger.error("Error while updating quote with ID {}: {}", quotesId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al actualizar la cita: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{quotesId}")
    public ResponseEntity<ResponseDTO<Void>> deleteQuote(@PathVariable int quotesId, @RequestHeader("Authorization") String token) {
        logger.info("Deleting quote with ID: {}", quotesId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for deleting a quote.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            boolean deleted = quoteBl.deleteQuoteStatus(quotesId);
            if (deleted) {
                logger.info("Quote with ID {} deleted successfully", quotesId);
                return ResponseEntity.ok(new ResponseDTO<>(200, null, "Cita eliminada exitosamente."));
            } else {
                logger.warn("No quote found to delete with ID: {}", quotesId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error while deleting quote with ID {}: {}", quotesId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al eliminar la cita: " + e.getMessage()));
        }
    }

    @GetMapping("/by-therapist/{therapistId}")
    public ResponseEntity<ResponseDTO<List<QuotesDTO>>> getQuotesByTherapistId(@PathVariable int therapistId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quotes for therapist with ID: {}", therapistId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching therapist quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesDTO> therapistQuotes = quoteBl.getQuotesByTherapistId(therapistId);
            if (!therapistQuotes.isEmpty()) {
                logger.info("Quotes for therapist ID {} retrieved successfully", therapistId);
                return ResponseEntity.ok(new ResponseDTO<>(200, therapistQuotes, "Citas del terapeuta recuperadas exitosamente."));
            } else {
                logger.info("No quotes found for therapist ID: {}", therapistId);
                return ResponseEntity.ok(new ResponseDTO<>(200, therapistQuotes, "No se encontraron citas para el terapeuta indicado."));
            }
        } catch (Exception e) {
            logger.error("Error while retrieving quotes for therapist ID {}: {}", therapistId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar las citas del terapeuta: " + e.getMessage()));
        }
    }


    @GetMapping("/available-slots/{therapistId}")
    public ResponseEntity<Map<String, Integer>> getAvailableSlots(@PathVariable int therapistId, @RequestHeader("Authorization") String token) {
        if (!authBl.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Map<String, Integer> slotsAvailability = quoteBl.countAvailableSlots(therapistId);
            if (slotsAvailability.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(slotsAvailability);
        } catch (Exception e) {
            logger.error("Error calculating available slots: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/dashboard-counts")
    public ResponseEntity<Map<String, Object>> getDashboardCounts(@RequestHeader("Authorization") String token) {
        if (!authBl.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Map<String, Object> dashboardCounts = quoteBl.getDashboardCounts();
            return ResponseEntity.ok(dashboardCounts);
        } catch (Exception e) {
            logger.error("Error while fetching dashboard counts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Obtener todas las cita hasta la fecha de hoy de un usuario
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<ResponseDTO<List<QuotesObtenerDTO>>> getUserQuotesToday(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quotes for user with ID: {} until today", userId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching user quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesObtenerDTO> userQuotes = quoteBl.getAllQuotesByDateToday(1,userId);
            logger.info("Quotes for user ID {} retrieved successfully", userId);
            return ResponseEntity.ok(new ResponseDTO<>(200, userQuotes, "Citas del usuario recuperadas exitosamente."));
        } catch (Exception e) {
            logger.error("Error while retrieving quotes for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar las citas del usuario: " + e.getMessage()));
        }
    }

    // Obtener todas las cita hasta la fecha de hoy de un terapeuta
    @GetMapping("/therapist/{therapistId}/today")
    public ResponseEntity<ResponseDTO<List<QuotesObtenerDTO>>> getTherapistQuotesToday(@PathVariable int therapistId, @RequestHeader("Authorization") String token) {
        logger.info("Fetching quotes for therapist with ID: {} until today", therapistId);
        if (!authBl.validateToken(token)) {
            logger.error("Invalid token provided for fetching therapist quotes.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), null, "Unauthorized"));
        }
        try {
            List<QuotesObtenerDTO> therapistQuotes = quoteBl.getAllQuotesByDateToday(2,therapistId);
            logger.info("Quotes for therapist ID {} retrieved successfully", therapistId);
            return ResponseEntity.ok(new ResponseDTO<>(200, therapistQuotes, "Citas del terapeuta recuperadas exitosamente."));
        } catch (Exception e) {
            logger.error("Error while retrieving quotes for therapist ID {}: {}", therapistId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ResponseDTO<>(400, null, "Error al recuperar las citas del terapeuta: " + e.getMessage()));
        }
    }



}
