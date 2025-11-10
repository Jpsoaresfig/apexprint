/* package com.middlwareserver.serverreport.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import static com.middlwareserver.serverreport.service.utils.ApexXmlToListMapStrObj.convertXmlToListMapStrObj;
import static com.middlwareserver.serverreport.service.pentaho.PentahoService.generateReportFromPentaho;

@RestController
@RequestMapping(value = "/api/pentaho", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public class Pentaho {

    private static final Logger logger = LoggerFactory.getLogger(Pentaho.class);

    @RequestMapping("/generateReportFromPentaho")
    public ResponseEntity<byte[]> reportFromPentaho(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            String xmlContent = multipartRequest.getParameter("xmlData");
            if (xmlContent == null || xmlContent.trim().isEmpty()) {
                throw new InvalidParameterException("Parâmetro 'xmlData' ausente ou vazio.");
            }

            String templateContent = multipartRequest.getParameter("templateFile");
            if (templateContent == null || templateContent.trim().isEmpty()) {
                throw new InvalidParameterException("Parâmetro 'templateFile' ausente ou vazio.");
            }

            List<Map<String, Object>> convertedParameters = convertXmlToListMapStrObj(xmlContent);
            byte[] reportContent = generateReportFromPentaho(templateContent, convertedParameters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "relatorio.pdf");
            headers.setContentLength(reportContent.length);

            return new ResponseEntity<>(reportContent, headers, HttpStatus.OK);

        } catch (InvalidParameterException e) {
            logger.error("Erro de parâmetro: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro interno ao gerar relatório", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar o relatório.");
        }
    }
}
 */