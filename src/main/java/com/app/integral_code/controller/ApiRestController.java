package com.app.integral_code.controller;


import com.app.integral_code.api.ApiService;
import com.app.integral_code.dto.DownloadFileRequestDTO;
import com.app.integral_code.dto.RequestDTO;
import com.app.integral_code.dto.ResponseDTO;
import com.app.integral_code.util.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class ApiRestController {
    private final ApiService apiService;

    @PostMapping("/api/run")
    public ResponseEntity<?> run(@RequestBody RequestDTO requestDTO) {
        RequestDTO req = new RequestDTO(requestDTO.getN(), requestDTO.getYvalues(), requestDTO.isToText(), requestDTO.isToPdf());
        ResponseDTO resp = apiService.run(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/api/download-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void downloadPdf(HttpServletResponse httpServletResponse, @RequestBody DownloadFileRequestDTO requestDTO) throws Exception {
        try{
            //pdflatex ile generate edilen pdf dosyası indiriliyor.
            Path path = Paths.get(requestDTO.getFilePath());
            if(FileUtils.isFileExist(path)) {
                Path fileStorageLocation = path.toAbsolutePath().normalize();
                Resource resource = new UrlResource(fileStorageLocation.toUri());

                httpServletResponse.setContentType("application/pdf");
                httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "test" + ".pdf");
                httpServletResponse.getOutputStream().write(resource.getContentAsByteArray());
                httpServletResponse.flushBuffer();
            }else {
                throw new Exception("Getting error while creating pdf file.");
            }
        }catch (IOException ex) {
            throw new Exception("Getting error while creating pdf file.");
        }
    }
}
