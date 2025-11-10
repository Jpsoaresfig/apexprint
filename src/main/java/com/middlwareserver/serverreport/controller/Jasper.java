package com.middlwareserver.serverreport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.middlwareserver.serverreport.service.jasper.JasperService.generateReportFromJasper;
import static com.middlwareserver.serverreport.service.utils.ApexXmlToListMapStrObj.convertXmlToListMapStrObj;

@Tag(name = "Oracle Apex Print Server", description = "API usada como Remote Print Server no Oracle Apex para geração de relatórios dinâmicos usando Report Queries.")
@RestController
@RequestMapping("/api/jasper")
public class Jasper {

    @Operation(
            summary = "Gerar relatório PDF ou XML a partir de dados enviados do Oracle Apex",
            description = "Recebe um XML com dados e um template JRXML e gera um relatório em formato PDF ou XML.",
            parameters = {
                    @Parameter(name = "xmlData", description = "Conteúdo XML contendo os dados para geração do relatório", required = true),
                    @Parameter(name = "templateFile", description = "Template JRXML para geração do relatório", required = true),
                    @Parameter(name = "format", description = "Formato de saída (pdf ou xml)", required = false)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                    content = {
                            @Content(mediaType = "application/pdf"),
                            @Content(mediaType = "application/xml")
                    }),
            @ApiResponse(responseCode = "400", description = "Requisição inválida - parâmetros ausentes ou inválidos",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a requisição",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping(value = "/generateReportFromJasper", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> reportFromJasper(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            // Obter o XML
            String xmlContent = multipartRequest.getParameter("xmlData");
            if (xmlContent == null || xmlContent.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro 'xmlData' ausente ou vazio.");
            }

            // Obter o template JRXML
            String templateContent = multipartRequest.getParameter("templateFile");
            if (templateContent == null || templateContent.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro 'templateFile' ausente ou vazio.");
            }

            // Obter o formato
            String format = multipartRequest.getParameter("format");
            boolean isDefaultFormat = (format == null || format.trim().isEmpty());

            if (!isDefaultFormat) {
                format = format.toLowerCase();
            }

            // Validar se o template é JRXML
            if (!templateContent.contains("jasperReport")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O template fornecido não parece ser um arquivo JRXML válido.");
            }

            // Converter o XML para um formato compatível com o JasperReports
            List<Map<String, Object>> convertedParameters = convertXmlToListMapStrObj(xmlContent);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(convertedParameters);

            // Gerar o relatório
            byte[] reportContent = isDefaultFormat
                    ? generateReportFromJasper(templateContent, dataSource)
                    : generateReportFromJasper(templateContent, dataSource, format);

            // Definir o Content-Type e o nome do arquivo com base no formato
            MediaType mediaType;
            String fileName;
            switch (isDefaultFormat ? "pdf" : format) {
                case "xml" -> {
                    mediaType = MediaType.APPLICATION_XML;
                    fileName = "report.xml";
                }
                case "pdf" -> {
                    mediaType = MediaType.APPLICATION_PDF;
                    fileName = "report.pdf";
                }
                case "html" -> {
                    mediaType = MediaType.TEXT_HTML;
                    fileName = "report.html";
                }
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato não suportado: " + format);
            }

            // Configurar a resposta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDisposition(ContentDisposition
                    .inline()
                    .filename(fileName)
                    .build());
            headers.setContentLength(reportContent.length);

            return new ResponseEntity<>(reportContent, headers, HttpStatus.OK);

        } catch (JRException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao gerar o relatório: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao processar a requisição: " + e.getMessage(), e);
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getReason());
    }
}
