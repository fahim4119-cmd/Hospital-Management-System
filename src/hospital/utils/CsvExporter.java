package hospital.utils;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvExporter {
    private CsvExporter() {}

    public static void exportTable(JTable table, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            TableModel model = table.getModel();
            for (int c = 0; c < model.getColumnCount(); c++) {
                writer.write(escape(model.getColumnName(c)));
                writer.write(c == model.getColumnCount() - 1 ? "\n" : ",");
            }
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object value = model.getValueAt(r, c);
                    writer.write(escape(value == null ? "" : value.toString()));
                    writer.write(c == model.getColumnCount() - 1 ? "\n" : ",");
                }
            }
        }
    }

    private static String escape(String value) {
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
