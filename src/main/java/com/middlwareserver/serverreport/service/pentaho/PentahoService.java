/* package com.middlwareserver.serverreport.service.pentaho;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class PentahoService {

    static {
        ClassicEngineBoot.getInstance().start();
    }

    private static final Logger logger = LoggerFactory.getLogger(PentahoService.class);

    public static byte[] generateReportFromPentaho(String templateContent, List<Map<String, Object>> parametersList, String format) {
        try {
            return createReport(templateContent, parametersList, format);
        } catch (ReportGenerationException e) {
            logger.error("Erro ao gerar relatório: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar relatório", e);
            throw new ReportGenerationException("Erro ao gerar relatório.", e);
        }
    }
    public static byte[] generateReportFromPentaho(String templateContent, List<Map<String, Object>> parametersList) {
        return generateReportFromPentaho(templateContent, parametersList, "pdf");
    }

    private static byte[] createReport(String templateContent, List<Map<String, Object>> parametersList, String format) throws Exception {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("temp_report_", ".prpt");
            Files.writeString(tempFile, templateContent);

            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                MasterReport report = loadReport(tempFile);
                setReportParameters(report, parametersList);
                generateReportByFormat(report, output, format);
                return output.toByteArray();
            }

        } catch (IOException | ResourceException e) {
            logger.error("Erro ao carregar template do relatório", e);
            throw new ReportGenerationException("Erro ao carregar template do relatório.", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    logger.warn("Falha ao deletar arquivo temporário: {}", tempFile);
                }
            }
        }
    }

    private static MasterReport loadReport(Path file) throws IOException, ResourceException {
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.registerDefaults();
        Resource resource = resourceManager.createDirectly(file.toFile(), MasterReport.class);
        return (MasterReport) resource.getResource();
    }

    private static void setReportParameters(MasterReport report, List<Map<String, Object>> parametersList) {
        parametersList.forEach(parameters -> parameters.forEach((key, value) -> {
            try {
                if (report.getParameterDefinition().getParameterDefinition(Integer.parseInt(key)) != null) {
                    report.getParameterValues().put(key, value);
                }
            } catch (Exception e) {
                logger.warn("Parâmetro inválido ignorado: {} = {}", key, value);
            }
        }));
    }

    private static void generateReportByFormat(MasterReport report, ByteArrayOutputStream output, String format) throws Exception {
        switch (format.toLowerCase()) {
            case "pdf":
                PdfReportUtil.createPDF(report, output);
                break;
            case "html":
                HtmlReportUtil.createStreamHTML(report, output);
                break;
            case "xls":
            case "excel":
                ExcelReportUtil.createXLS(report, output);
                break;
            default:
                throw new IllegalArgumentException("Formato não suportado: " + format);
        }
    }

    public static class ReportGenerationException extends RuntimeException {
        public ReportGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
 */