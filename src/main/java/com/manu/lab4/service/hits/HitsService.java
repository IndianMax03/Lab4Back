package com.manu.lab4.service.hits;

import com.manu.lab4.listening.requests.AppendingRequest;
import com.manu.lab4.listening.requests.GettingRequest;
import com.manu.lab4.listening.requests.RemovingRequest;
import com.manu.lab4.listening.responses.MessageResponse;
import com.manu.lab4.model.Hit;
import com.manu.lab4.model.User;
import com.manu.lab4.repositories.HitRepository;
import com.manu.lab4.repositories.UserRepository;
import com.manu.lab4.security.JwtUtils;
import com.manu.lab4.service.businessLogic.Validator;
import jakarta.transaction.Transactional;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import io.jsonwebtoken.SignatureException;

@Service
public class HitsService {

    private final HitRepository hitRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;


    public HitsService(HitRepository hitRepository, UserRepository userRepository, JwtUtils jwtUtils) {
        this.hitRepository = hitRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<Object> appendHitAndReturnHits(@RequestBody AppendingRequest appendingRequest, long startMs) {
        final double x = appendingRequest.getX();
        final double y = appendingRequest.getY();
        final double r = appendingRequest.getR();
        if (!Validator.checkCoordinatesValidity(x, y, r)) {
            return ResponseEntity.status(406)
                    .body("""
                            not acceptable:
                            x in [-4; 4] - double (length = 12)
                            y in [-5; 5] - double (length = 12)
                            r in {-4, -3, -2, -1, 0, 1, 2, 3, 4} - integer
                            """);
        }
        final int validR = (int) r;
        final String token = appendingRequest.getToken();
        final boolean success = Validator.calculate(x, y, validR);

        try {

            User user = userRepository.findByUsername(jwtUtils.extractUsername(token));
            Hit hit = new Hit();

            hit.setX(x);
            hit.setY(y);
            hit.setR(validR);
            hit.setSuccess(success);
            hit.setUser(user);
            hit.setExecutionTime(System.currentTimeMillis() - startMs + 73);

            hitRepository.save(hit);

            return ResponseEntity.ok().body(hitRepository.findAllByUserId(user.getId()));

        } catch (SignatureException exception) {
            return ResponseEntity.status(401).body(new MessageResponse("token lifecycle ended or token is invalid"));
        } catch (InvalidDataAccessResourceUsageException | NullPointerException exception) {
            return ResponseEntity.status(409).body(new MessageResponse("database not available"));
        } catch (Exception exception) {
            System.out.println("Error in appendHitAndReturnHits(): " + exception.getClass());
            return ResponseEntity.status(500)
                    .body("server error: cannot save hit or return hits.\nerror class: " + exception.getClass());
        }

    }

    public ResponseEntity<Object> getHits(@RequestBody GettingRequest gettingRequest) {
        final String token = gettingRequest.getToken();
        try {

            User user = userRepository.findByUsername(jwtUtils.extractUsername(token));
            return ResponseEntity.ok().body(hitRepository.findAllByUserId(user.getId()));

        } catch (SignatureException exception) {
            return ResponseEntity.status(401).body(new MessageResponse("token lifecycle ended or token is invalid"));
        } catch (InvalidDataAccessResourceUsageException | NullPointerException exception) {
            return ResponseEntity.status(409).body(new MessageResponse("database not available"));
        } catch (Exception exception) {
            System.out.println("Error in getHits(): " + exception.getClass());
            return ResponseEntity.status(500)
                    .body("server error: cannot save hit or return hits.\nerror class: " + exception.getClass());
        }
    }

    @Transactional
    public ResponseEntity<Object> deleteHits(@RequestBody RemovingRequest removingRequest) {
        try {
            final String token = removingRequest.getToken();
            User user = userRepository.findByUsername(jwtUtils.extractUsername(token));
            hitRepository.deleteAllByUserId(user.getId());
            return ResponseEntity.ok().body(new MessageResponse("success"));
        } catch (SignatureException exception) {
            return ResponseEntity.status(401).body(new MessageResponse("token lifecycle ended or token is invalid"));
        } catch (InvalidDataAccessResourceUsageException | NullPointerException exception) {
            return ResponseEntity.status(409).body(new MessageResponse("database not available"));
        } catch (Exception exception) {
            System.out.println("Error in deleteHits(): " + exception.getClass());
            return ResponseEntity.status(500)
                    .body("server error: cannot delete.\nerror class: " + exception.getClass());
        }
    }

}
