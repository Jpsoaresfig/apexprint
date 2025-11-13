package com.apexprint.apexprint.services;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class ReportService {

    public byte[] gerarRelatorio(File jrxmlFile, File xmlDataFile, String format) throws Exception {

        // Compila o template .jrxml
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());

        // Fonte de dados XML (baseado no seu XML)
        JRXmlDataSource xmlDataSource = new JRXmlDataSource(xmlDataFile, "/DOCUMENT/REGION/ROWSET/ROW");

        // Preenche o relatório (sem parâmetros)
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, xmlDataSource);

        // Stream de saída
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Escolhe o formato de exportação
        switch (format.toLowerCase()) {
            case "pdf" -> {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }
            case "docx" -> {
                //JasperExportManager.exportReportToHtmlStream(jasperPrint, outputStream);
            }
            case "xlsx" -> {
                JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setOnePagePerSheet(false);
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);
                xlsxExporter.setConfiguration(configuration);

                xlsxExporter.exportReport();
            }
            default -> throw new IllegalArgumentException("Formato não suportado: " + format);
        }

        return outputStream.toByteArray();
    }
}
