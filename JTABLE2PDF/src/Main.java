import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main extends JFrame {

    private JTable table;

    public Main() {
        setTitle("Raport produktów");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nazwa Produktu");
        model.addColumn("Ilość");
        model.addColumn("Cena");

        model.addRow(new Object[]{1, "myszka", 10, 120});
        model.addRow(new Object[]{2, "Klawiatura", 5, 300});
        model.addRow(new Object[]{3, "Monitor", 3, 800});
        model.addRow(new Object[]{4, "Drukarka laserowa", 2, 2900});

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnPDF = new JButton("Generuj Raport PDF");
        btnPDF.addActionListener((ActionEvent e) -> exportToPDF(table));

        add(scrollPane, BorderLayout.CENTER);
        add(btnPDF, BorderLayout.SOUTH);
    }

    public void exportToPDF(JTable table) {
        JFileChooser chooser = new JFileChooser();
        int state = chooser.showSaveDialog(this);

        if (state == JFileChooser.APPROVE_OPTION) {

            Document document = new Document();

            try {
                PdfWriter.getInstance(document,
                        new FileOutputStream(chooser.getSelectedFile() + ".pdf"));

                document.open();

                BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf",
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);

                Font font = new Font(bf, 12);
                Font headerFont = new Font(bf, 16, Font.BOLD);

                String data = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                Paragraph header = new Paragraph(
                        "Raport produktów\nData wygenerowania: " + data,
                        headerFont);

                header.setAlignment(Element.ALIGN_CENTER);
                header.setSpacingAfter(20);
                document.add(header);

                PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
                pdfTable.setWidthPercentage(100);

                for (int i = 0; i < table.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(
                            new Paragraph(table.getColumnName(i), font));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                for (int rows = 0; rows < table.getRowCount(); rows++) {
                    for (int cols = 0; cols < table.getColumnCount(); cols++) {
                        Object value = table.getValueAt(rows, cols);
                        PdfPCell cell = new PdfPCell(
                                new Paragraph(value != null ? value.toString() : "", font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);

                JOptionPane.showMessageDialog(this,
                        "Eksport zakończony sukcesem!");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Wystąpił błąd podczas eksportu PDF:\n" + e.getMessage());
            } finally {
                document.close();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}