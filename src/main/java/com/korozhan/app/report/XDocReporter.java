package com.korozhan.app.report;

import com.korozhan.app.util.DateConverter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by veronika.korozhan on 22.09.2017.
 */
public class XDocReporter {

    private static void generateReport(BigDecimal amount, int term) throws IOException, XDocReportException {
        // 1) Load ODT file and set Velocity template engine and cache it to the registry
        InputStream in = XDocReporter.class.getClassLoader().getResourceAsStream("sample/xdocTemplate.odt");
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
        // 2) Create Java model context
        IContext context = report.createContext();
        context.put("amount", amount);
        context.put("amount2x", amount.multiply(new BigDecimal(2)));
        context.put("term", term);

        FieldsMetadata metadata = report.createFieldsMetadata();
        metadata.load("schedules", Loan.class, true);
        report.setFieldsMetadata(metadata);

        List<Loan> schedules = new ArrayList<Loan>();
        schedules.add(new Loan(DateConverter.dateToString(new Date(), DateConverter.DATE_FORMAT_SINPLE), new BigDecimal(1), new BigDecimal(11)));
        schedules.add(new Loan(DateConverter.dateToString(new Date(), DateConverter.DATE_FORMAT_SINPLE), new BigDecimal(2), new BigDecimal(22)));
        schedules.add(new Loan(DateConverter.dateToString(new Date(), DateConverter.DATE_FORMAT_SINPLE), new BigDecimal(3), new BigDecimal(33)));

        context.put("schedules", schedules);

        // 3) Set PDF as format converter
        PdfOptions pdfOptions = PdfOptions.create();
        // if font display problems
//        pdfOptions.fontProvider(new ITextFontRegistry() {
//            public Font getFont(String familyName, String encoding, float size, int style, Color color) {
//                try {
//                    BaseFont bfHelvetica = BaseFont.createFont("/usr/share/fonts/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//                    Font fontHelvetica = new Font(bfHelvetica, size, style, color);
//                    if (familyName != null)
//                        fontHelvetica.setFamily(familyName);
//                    return fontHelvetica;
//                } catch (DocumentException e) {
//                    return ITextFontRegistry.getRegistry().getFont(familyName, encoding, size, style, color);
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                    return ITextFontRegistry.getRegistry().getFont(familyName, encoding, size, style, color);
//                }
//            }
//        });
        Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM).subOptions(pdfOptions);

        OutputStream out = new FileOutputStream(new File("reports/xdocReport.pdf"));
        report.convert(context, options, out);
    }

    public static void main(String[] args) {
        try {
            generateReport(new BigDecimal(11), 12);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }
}