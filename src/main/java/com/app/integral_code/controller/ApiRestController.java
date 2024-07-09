package com.app.integral_code.controller;


import com.app.integral_code.api.ApiService;
import com.app.integral_code.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiRestController {
    private final ApiService apiService;

    @PostMapping("/api/run")
    public ResponseEntity<?> run(@RequestBody RequestDTO requestDTO) {
        RequestDTO req = new RequestDTO(requestDTO.getN(), requestDTO.isToText(), requestDTO.isToPdf());
        return ResponseEntity.ok(apiService.run(req));
    }
}
