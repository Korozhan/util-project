package com.korozhan.app.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korozhan.app.document.DocumentCard;
import com.korozhan.app.util.FileUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by veronika.korozhan on 18.01.2018.
 */
public class JasperReporter {

    public static void generateReport(String reportResourcePath, InputStream sourceInputStream, List<String> subreportsNames, String filename) {
        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(FileUtil.getReportName(reportResourcePath));
            Map<String, Object> parametersMap = new HashMap<>();
            //Pass to parameter map
            parametersMap.put(JsonQueryExecuterFactory.JSON_INPUT_STREAM, sourceInputStream);
            JasperReport subreport;
            for (String subreportName : subreportsNames) {
                subreport = (JasperReport) JRLoader.loadObject(FileUtil.getReportFile(subreportName.concat(".jasper")));
                parametersMap.put(subreportName, subreport);
            }
            //Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parametersMap);
            //Export to pdf
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/".concat(filename)));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setMetadataAuthor("Me");
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private static DocumentCard fillJsonAndGenerateReport(String reportResourcePath, InputStream sourceInputStream, List<String> subreportsNames, String filename) {
        try {
            Map<String, String> fillParams = new HashMap<String, String>() {{
                put("NUMBER_PARAM", "123456");
                put("SERIES_PARAM", "SR");
            }};
            InputStream jsonInputStream = FileUtil.fillWithParams(sourceInputStream, fillParams, ".json");
            if (jsonInputStream != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(jsonInputStream, baos);
                InputStream jsonCopiedInputStream = new ByteArrayInputStream(baos.toByteArray());

                generateReport(reportResourcePath, jsonCopiedInputStream, subreportsNames, filename);
                jsonCopiedInputStream.reset();

                return new ObjectMapper().readValue(new ByteArrayInputStream(baos.toByteArray()), DocumentCard.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        generateReport("sample/goods.jasper",
//                FileUtil.getReportName("sample/goods.json"),
//                Arrays.asList("sample/goods_subreport1", "sample/goods_subreport2", "sample/goods_subreport3"),
//                "goods.pdf");

        System.out.println(fillJsonAndGenerateReport("sample/goods.jasper",
                FileUtil.getReportName("sample/goods.json"),
                Arrays.asList("sample/goods_subreport1", "sample/goods_subreport2", "sample/goods_subreport3"),
                "goods.pdf") instanceof DocumentCard);
    }
}
