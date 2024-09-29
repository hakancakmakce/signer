package dijisoz.signer.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PDFgen {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE_H1 = 18;
    private static final float FONT_SIZE_NORMAL = 12;
    private static final PDType1Font FONT = PDType1Font.HELVETICA;

    public static File convertHtmlToPdf(String htmlContent) throws IOException {
        File pdfFile = Files.createTempFile("output", ".pdf").toFile();
        Document doc = Jsoup.parse(htmlContent);

        try (PDDocument document = new PDDocument()) {
            PDPage page = addPage(document);
            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            Elements elements = doc.body().children();
            for (Element element : elements) {
                switch (element.tagName()) {
                    case "h1":
                        yPosition = processElement(document, page, element, FONT_SIZE_H1, yPosition);
                        break;
                    case "p":
                    case "h4":
                        yPosition = processElement(document, page, element, FONT_SIZE_NORMAL, yPosition);
                        break;
                    case "table":
                        yPosition = processTable(document, page, element, yPosition);
                        break;
                }
            }

            document.save(pdfFile);
        }

        return pdfFile;
    }

    private static PDPage addPage(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return page;
    }

    private static float processElement(PDDocument document, PDPage page, Element element, float fontSize, float yPosition) throws IOException {
        float width = page.getMediaBox().getWidth() - 2 * MARGIN;
        List<String> lines = wrapText(element.text(), fontSize, width);
        
        for (String line : lines) {
            if (yPosition < MARGIN + fontSize) {
                page = addPage(document);
                yPosition = page.getMediaBox().getHeight() - MARGIN;
            }
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.beginText();
                contentStream.setFont(FONT, fontSize);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(line);
                contentStream.endText();
            }
            
            yPosition -= fontSize + 5;
        }
        
        return yPosition - 10;
    }

    private static List<String> wrapText(String text, float fontSize, float width) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        float lineWidth = 0;
        for (String word : words) {
            float wordWidth = FONT.getStringWidth(word) / 1000 * fontSize;
            if (lineWidth + wordWidth > width) {
                lines.add(line.toString());
                line = new StringBuilder(word + " ");
                lineWidth = wordWidth + FONT.getStringWidth(" ") / 1000 * fontSize;
            } else {
                line.append(word).append(" ");
                lineWidth += wordWidth + FONT.getStringWidth(" ") / 1000 * fontSize;
            }
        }
        lines.add(line.toString());
        return lines;
    }

    private static float processTable(PDDocument document, PDPage page, Element table, float yPosition) throws IOException {
        Elements rows = table.select("tr");
        float rowHeight = 20;
        float tableWidth = page.getMediaBox().getWidth() - 2 * MARGIN;
        float cellWidth = tableWidth / (float) rows.first().children().size();
        
        for (Element row : rows) {
            if (yPosition < MARGIN + rowHeight) {
                page = addPage(document);
                yPosition = page.getMediaBox().getHeight() - MARGIN;
            }
            
            float x = MARGIN;
            Elements cells = row.children();
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                for (Element cell : cells) {
                    contentStream.addRect(x, yPosition - rowHeight, cellWidth, rowHeight);
                    contentStream.stroke();
                    
                    contentStream.beginText();
                    contentStream.setFont(FONT, FONT_SIZE_NORMAL);
                    contentStream.newLineAtOffset(x + 2, yPosition - 15);
                    contentStream.showText(cell.text());
                    contentStream.endText();
                    
                    x += cellWidth;
                }
            }
            
            yPosition -= rowHeight;
        }
        
        return yPosition - 10;
    }

    public static void main(String[] args) {
        String htmlContent = "<h1 style=\"text-align: center;\">HİZMET SÖZLEŞMESİ</h1><h4>Sözleşme Numarası: SOB2024000000001</h4>...";

        try {
            File pdfFile = convertHtmlToPdf(htmlContent);
            System.out.println("PDF dosyası oluşturuldu: " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
