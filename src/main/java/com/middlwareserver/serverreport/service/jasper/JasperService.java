package com.middlwareserver.serverreport.service.jasper;

import jakarta.annotation.Resource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import org.apache.xmlbeans.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class JasperService {

    public static byte[] generateReportFromJasper(String jrxmlContent, JRBeanCollectionDataSource parameters, String format)
            throws JRException, IOException {
        Path tempFile = null;
        try {
            // Criar um arquivo temporário para armazenar o conteúdo do jrxml e o compilar
            tempFile = Files.createTempFile("temp_report_", ".jrxml");
            Files.writeString(tempFile, jrxmlContent);

            JasperReport jasperReport = JasperCompileManager.compileReport(tempFile.toString());
            if (jasperReport == null) {
                throw new JRException("JasperReport é nulo");
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    new HashMap<>(),
                    parameters
            );

            return switch (format.toLowerCase()) {
                case "xml" -> JasperExportManager.exportReportToXml(jasperPrint).getBytes();
                case "html" -> {
                    Path tempHtmlFile = Files.createTempFile("report_", ".html");
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, tempHtmlFile.toString());
                    byte[] htmlBytes = Files.readAllBytes(tempHtmlFile);
                    Files.delete(tempHtmlFile);
                    yield htmlBytes;
                }
                case "pdf" -> JasperExportManager.exportReportToPdf(jasperPrint);
                default ->
                        throw new IllegalArgumentException("Formato não suportado: " + format + "\nFormatos suportados: XML, PDF\n");
            };

        } catch (JRException e) {
            throw new JRException("Falha ao gerar relatório: " + e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException e) {
                    System.err.println("Falha ao deletar arquivo temporário: " + tempFile);
                }
            }
        }
    }
    public static byte[] generateReportFromJasper(String jrxmlContent, JRBeanCollectionDataSource parameters) throws JRException, IOException {
        return generateReportFromJasper(jrxmlContent, parameters, "pdf");
    }
}
