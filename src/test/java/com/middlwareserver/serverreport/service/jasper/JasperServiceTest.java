package com.middlwareserver.serverreport.service.jasper;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JasperService.
 * 
 * This test verifies the behavior of the method responsible for generating
 * a PDF report using a simple JRXML (JasperReports XML template).
 * It ensures that the PDF generation works and returns a valid byte array,
 * even with an empty data source.
 */
class JasperServiceTest {

    /**
     * Test for generating a PDF report from a hardcoded JRXML string.
     * 
     * The JRXML defines a simple report that displays "Hello Jasper!".
     * The test uses an empty data source and checks:
     * - That the method does not throw any exception.
     * - That the returned byte array (PDF content) is not null.
     * - That the byte array has a length greater than 0 (i.e., the PDF was
     * generated).
     */
    @Test
    void testGenerateReportFromJasperPdf() {
        // Hardcoded JRXML defining a simple report layout with static text.
        String jrxml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE jasperReport PUBLIC "-//JasperReports//DTD Report Design//EN"
                "http://jasperreports.sourceforge.net/dtds/jasperreport.dtd">
                <jasperReport name="sample" pageWidth="595" pageHeight="842" columnWidth="555"
                              leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" >
                    <detail>
                        <band height="20">
                            <staticText>
                                <reportElement x="0" y="0" width="200" height="20"/>
                                <text><![CDATA[Hello Jasper!]]></text>
                            </staticText>
                        </band>
                    </detail>
                </jasperReport>
                """;

        // Empty data source since the report only contains static text
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.emptyList());

        // Assert that no exception is thrown and the PDF result is valid
        assertDoesNotThrow(() -> {
            byte[] pdf = JasperService.generateReportFromJasper(jrxml, dataSource, "pdf");

            // Ensure that the generated PDF is not null
            assertNotNull(pdf);

            // Ensure that the PDF has content
            assertTrue(pdf.length > 0);
        });
    }

}
