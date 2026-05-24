package schoolmanagement.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public final class CsvExporter {
    private CsvExporter() {
    }

    public static void writeTable(JTable table, Path path) throws IOException {
        TableModel model = table.getModel();
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (int column = 0; column < model.getColumnCount(); column++) {
                if (column > 0) {
                    writer.write(",");
                }
                writer.write(escape(model.getColumnName(column)));
            }
            writer.newLine();

            for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                for (int column = 0; column < model.getColumnCount(); column++) {
                    if (column > 0) {
                        writer.write(",");
                    }
                    writer.write(escape(model.getValueAt(modelRow, column)));
                }
                writer.newLine();
            }
        }
    }

    private static String escape(Object value) {
        String text = value == null ? "" : value.toString();
        boolean requiresQuotes = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r");
        text = text.replace("\"", "\"\"");
        return requiresQuotes ? "\"" + text + "\"" : text;
    }
}
