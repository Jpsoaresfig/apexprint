package com.apexprint.apexprint.controllers;

import com.apexprint.apexprint.services.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;

@RestController
@RequestMapping("/report")
@Tag(name = "Report Generator", description = "Gera relatórios Jasper a partir de JRXML e XML de dados")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "Gera um relatório em PDF, HTML ou XLSX a partir do body JSON")
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> gerarRelatorio(
            @RequestParam("templateFile") String jrxmlContent,
            @RequestParam("xmlData") String xmlDataContent,
            @RequestParam(value = "format", defaultValue = "pdf") String format
    ) {
        try {

            System.out.println("Recebido JRXML: " + jrxmlContent);
            System.out.println("Recebido XML Data: " + xmlDataContent);

            // Cria arquivos temporários a partir do texto recebido
            File tempJrxml = File.createTempFile("report-", ".jrxml");
            try (FileWriter writer = new FileWriter(tempJrxml)) {
                writer.write(jrxmlContent);
            }

            File tempXml = File.createTempFile("data-", ".xml");
            try (FileWriter writer = new FileWriter(tempXml)) {
                writer.write(xmlDataContent);
            }

            // Gera o relatório
            byte[] bytes = reportService.gerarRelatorio(tempJrxml, tempXml, format);

            // Define Content-Type e nome do arquivo
            String contentType;
            String fileExtension;

            switch (format.toLowerCase()) {
                case "pdf" -> {
                    contentType = MediaType.APPLICATION_PDF_VALUE;
                    fileExtension = "pdf";
                }
                case "docx" -> {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                    fileExtension = "docx";
                }
                case "xlsx" -> {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    fileExtension = "xlsx";
                }
                default -> throw new IllegalArgumentException("Formato não suportado: " + format);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", "relatorio." + fileExtension);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar relatório: " + e.getMessage()).getBytes());
        }
    }
}


//https://apexprint.joaopessoa.pb.gov.br/api/jasper/generateReportFromJasper