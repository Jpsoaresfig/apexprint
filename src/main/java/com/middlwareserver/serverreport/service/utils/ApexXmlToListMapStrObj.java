package com.middlwareserver.serverreport.service.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;

public class ApexXmlToListMapStrObj {

    public static List<Map<String, Object>> convertXmlToListMapStrObj(String xml) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            SimpleModule module = new SimpleModule();
            module.addDeserializer(Document.class, new DocumentDeserializer());
            xmlMapper.registerModule(module);

            Document document = xmlMapper.readValue(xml, Document.class);
            return document.getRows();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar XML: " + e.getMessage(), e);
        }
    }

    @Setter
    @Getter
    private static class Document {
        private List<Map<String, Object>> rows = new ArrayList<>();

    }

    private static class DocumentDeserializer extends StdDeserializer<Document> {

        public DocumentDeserializer() {
            this(null);
        }

        public DocumentDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Document deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            Document document = new Document();
            List<Map<String, Object>> rows = new ArrayList<>();

            // Navegar até o nó ROWSET/ROW
            JsonNode rowsetNode = node.get("ROWSET");
            if (rowsetNode != null) {
                JsonNode rowNodes = rowsetNode.get("ROW");
                if (rowNodes != null) {
                    if (rowNodes.isArray()) {
                        for (JsonNode rowNode : rowNodes) {
                            Map<String, Object> row = new HashMap<>();
                            Iterator<Map.Entry<String, JsonNode>> fields = rowNode.fields();
                            while (fields.hasNext()) {
                                Map.Entry<String, JsonNode> field = fields.next();
                                row.put(field.getKey(), field.getValue().asText());
                            }
                            rows.add(row);
                        }
                    } else {
                        // Caso seja um único ROW
                        Map<String, Object> row = new HashMap<>();
                        Iterator<Map.Entry<String, JsonNode>> fields = rowNodes.fields();
                        while (fields.hasNext()) {
                            Map.Entry<String, JsonNode> field = fields.next();
                            row.put(field.getKey(), field.getValue().asText());
                        }
                        rows.add(row);
                    }
                }
            }

            document.setRows(rows);
            return document;
        }
    }
}