package com.manu.lab4.rest;

import com.manu.lab4.listening.requests.AppendingRequest;
import com.manu.lab4.listening.requests.GettingRequest;
import com.manu.lab4.listening.requests.RemovingRequest;
import com.manu.lab4.service.hits.HitsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/hits")
public class HitsRestController {

    HitsService hitsService;

    @Autowired
    public HitsRestController(HitsService hitsService) {
        this.hitsService = hitsService;
    }

    @PostMapping("/append")
    public ResponseEntity<Object> appendHitAndReturnHits(@Valid @RequestBody AppendingRequest appendingRequest) {
        long start = System.currentTimeMillis();
        return hitsService.appendHitAndReturnHits(appendingRequest, start);
    }

    @PostMapping("/get")
    public ResponseEntity<Object> getHits(@Valid @RequestBody GettingRequest gettingRequest) {
        return hitsService.getHits(gettingRequest);
    }

    @PostMapping("/delete")
    public ResponseEntity<Object> getHits(@Valid @RequestBody RemovingRequest removingRequest) {
        return hitsService.deleteHits(removingRequest);
    }
}
