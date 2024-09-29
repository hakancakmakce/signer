package dijisoz.signer.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class PdfOperation {

    public static File convertHtmlToPdf(String htmlContent) throws IOException, DocumentException {
        // Geçici PDF dosyası oluştur
        File pdfFile = Files.createTempFile("output", ".pdf").toFile();
        // HTML'i temizle ve geçerli hale getir
        Document document = Jsoup.parse(htmlContent);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String xHtml = document.html();

        System.out.println("Temizlenmiş HTML: " + xHtml);
        try (OutputStream outputStream = new FileOutputStream(pdfFile)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xHtml);
            renderer.layout();
            renderer.createPDF(outputStream);
        }

        return pdfFile;
    }

    public static File convertHtmlToPdfm(String htmlContent) throws IOException {
        // Geçici PDF dosyası oluştur
        File pdfFile = Files.createTempFile("output", ".pdf").toFile();
        Document document = Jsoup.parse(htmlContent);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String xHtml = document.html();
        try (OutputStream os = new FileOutputStream(pdfFile)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(xHtml, null);
            builder.toStream(os);
            builder.run();
        }

        return pdfFile;
    }

    public static File convertHtmlToPdfx(String htmlContent) throws IOException, DocumentException {
        // HTML'i XHTML'e dönüştür
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
        doc.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset(StandardCharsets.UTF_8);
        String xHtml = doc.html();

        // Geçici PDF dosyası oluştur
        File pdfFile = Files.createTempFile("output", ".pdf").toFile();

        // PDF oluştur
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(xHtml));

        document.close();
        writer.close();

        return pdfFile;
    }

    public static File convertHtmlToPdfy(String htmlContent, Path path) throws IOException, DocumentException {
        // HTML'i XHTML'e dönüştür
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
        doc.outputSettings()
                .syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.extended)
                .charset(StandardCharsets.UTF_8)
                .prettyPrint(false);
        String xHtml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
                +
                doc.html();

        /*
         * File pdfFile = Files.createFile(path).toFile();
         * 
         * // PDF oluştur
         * com.itextpdf.text.Document document = new com.itextpdf.text.Document();
         * PdfWriter writer = PdfWriter.getInstance(document, new
         * FileOutputStream(pdfFile));
         * document.open();
         * 
         * XMLWorkerHelper.getInstance().parseXHtml(writer, document,
         * new ByteArrayInputStream(xHtml.getBytes(StandardCharsets.UTF_8)),
         * StandardCharsets.UTF_8);
         * 
         * document.close();
         * writer.close();
         * writer.flush();
         */

         File pdfFile = path.toFile();
         FileOutputStream fos = null;
         com.itextpdf.text.Document document = null;
         PdfWriter writer = null;
     
         try {
             fos = new FileOutputStream(pdfFile);
             document = new com.itextpdf.text.Document();
             writer = PdfWriter.getInstance(document, fos);
             document.open();
     
             XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                 new ByteArrayInputStream(xHtml.getBytes(StandardCharsets.UTF_8)),
                 StandardCharsets.UTF_8);
     
         } catch (DocumentException | IOException e) {
             System.out.println(e);
             return null; // Hata durumunda null döndür
         } finally {
             try {
                 if (document != null) {
                     document.close();
                 }
                 if (writer != null) {
                    writer.flush(); 
                    writer.close();

                 }
                 if (fos != null) {
                    fos.flush();
                     fos.close();
                     
                 }
                 
                 // Dosyanın tam olarak yazılması için kısa bir süre bekleyin
                 Thread.sleep(100);
             } catch (IOException | InterruptedException e) {
                 System.err.println(e);
             }
         }

        return pdfFile;
    }

    public static File convertHtmlToPdfj(String htmlContent) throws IOException {
        // Geçici PDF dosyası oluştur
        File pdfFile = Files.createTempFile("output", ".pdf").toFile();

        // HTML'i parse et
        Document doc = Jsoup.parse(htmlContent);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType0Font font = PDType0Font.load(document, new File("path/to/your/font.ttf")); // Türkçe karakter
                                                                                              // destekli bir font
                                                                                              // dosyası

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - 50;
                float margin = 50;
                @SuppressWarnings("unused")
                float width = page.getMediaBox().getWidth() - 2 * margin;

                // HTML içeriğini döngüyle gezin ve PDF'e ekleyin
                Elements elements = doc.body().children();
                for (Element element : elements) {
                    switch (element.tagName()) {
                        case "h1":
                            contentStream.beginText();
                            contentStream.setFont(font, 18);
                            contentStream.newLineAtOffset(margin, yPosition);
                            contentStream.showText(element.text());
                            contentStream.endText();
                            yPosition -= 30;
                            break;
                        case "p":
                            contentStream.beginText();
                            contentStream.setFont(font, 12);
                            contentStream.newLineAtOffset(margin, yPosition);
                            contentStream.showText(element.text());
                            contentStream.endText();
                            yPosition -= 20;
                            break;
                    // Tablo işleme kodunu buraya ekleyin
                    // (Karmaşık olduğu için burada gösterilmemiştir)
                        case "table":
                            break;
                        default:
                            break;
                    }

                    // Yeni sayfa ekleme kontrolü
                    if (yPosition < 50) {
                        page = new PDPage();
                        document.addPage(page);
                        contentStream.close();
                        // contentStream.closeResource();
                        yPosition = page.getMediaBox().getHeight() - 50;
                    }
                }
            }

            document.save(pdfFile);
        }

        return pdfFile;
    }
}
