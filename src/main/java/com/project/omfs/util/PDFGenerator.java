package com.project.omfs.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.project.omfs.entity.Customer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PDFGenerator {

    public static ByteArrayInputStream generateLoanReport(Customer customer) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Loan Report for " + customer.getName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            addRow(table, "Name", customer.getName());
            addRow(table, "Email", customer.getEmail());
            addRow(table, "Loan Amount", String.valueOf(customer.getLoanAmount()));
            addRow(table, "Term (months)", String.valueOf(customer.getLoanTermMonths()));
            addRow(table, "Status", customer.getStatus());
            addRow(table, "Date", customer.getDate() != null ? customer.getDate().toString() : "N/A");
            addRow(table, "Lender", customer.getLender() != null ? customer.getLender().getName() : "N/A");

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void addRow(PdfPTable table, String label, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label));
        PdfPCell cell2 = new PdfPCell(new Phrase(value));
        table.addCell(cell1);
        table.addCell(cell2);
    }
}
