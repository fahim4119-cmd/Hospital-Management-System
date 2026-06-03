package hospital.utils;

import hospital.models.Bill;
import hospital.models.BillItem;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class InvoicePdfExporter {
    private InvoicePdfExporter() {}

    public static void export(Bill bill, File file) throws Exception {
        Class<?> documentClass = Class.forName("com.itextpdf.text.Document");
        Class<?> paragraphClass = Class.forName("com.itextpdf.text.Paragraph");
        Class<?> pdfWriterClass = Class.forName("com.itextpdf.text.pdf.PdfWriter");
        Object document = documentClass.getConstructor().newInstance();
        Method getInstance = pdfWriterClass.getMethod("getInstance", documentClass, java.io.OutputStream.class);
        getInstance.invoke(null, document, new FileOutputStream(file));
        documentClass.getMethod("open").invoke(document);
        Method add = documentClass.getMethod("add", Class.forName("com.itextpdf.text.Element"));
        Constructor<?> paragraph = paragraphClass.getConstructor(String.class);

        add.invoke(document, paragraph.newInstance("Hospital Management System - Invoice #" + bill.getId()));
        add.invoke(document, paragraph.newInstance("Patient: " + bill.getPatientName()));
        add.invoke(document, paragraph.newInstance("Doctor: " + bill.getDoctorName()));
        add.invoke(document, paragraph.newInstance("Date: " + bill.getCreatedAt()));
        add.invoke(document, paragraph.newInstance(" "));
        add.invoke(document, paragraph.newInstance("Items"));
        for (BillItem item : bill.getItems()) {
            add.invoke(document, paragraph.newInstance(item.getMedicineName() + " x " + item.getQuantity()
                    + " @ Rs " + item.getUnitPrice() + " = Rs " + item.getSubtotal()));
        }
        add.invoke(document, paragraph.newInstance(" "));
        add.invoke(document, paragraph.newInstance("Total: Rs " + String.format("%.2f", bill.getTotalAmount())));
        documentClass.getMethod("close").invoke(document);
    }
}
