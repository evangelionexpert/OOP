package ru.nsu.fit.smolyakov.tableprinter.implementations;

import lombok.Setter;
import ru.nsu.fit.smolyakov.tableprinter.TablePrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlTablePrinter implements TablePrinter {
    private final File file;
    private final List<List<String>> table
        = new ArrayList<>(); // inner are rows, outer are columns
    @Setter
    private String title = "(no title)";

    public HtmlTablePrinter(String filename) {
        this(new File(filename));
    }

    public HtmlTablePrinter(File file) {
        this.file = file;
    }

    private static String convertMultilineCell(String cell) {
        return cell.replace("\n", "<div>").concat("</div>");
    }

    @Override
    public void appendRow(List<String> cells) {
        var convertedRow = cells.stream()
            .map(HtmlTablePrinter::convertMultilineCell)
            .toList();

        table.add(convertedRow);
    }

    @Override
    public void print() throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("""
                <!DOCTYPE html>
                <html>
                                
                <style>
                table {
                    border-collapse: collapse;
                }
                                
                td {
                    border: 1px solid black;
                    line-height: 1.5em;
                    font-size: 14px;
                    font-family: monospace;
                    text-align: center;
                    font-weight: normal;
                }
                                
                h1 {
                    font-family: monospace;
                    font-size: 22px;
                    font-weight: normal;
                }
                </style>
                                
                <head>
                <title>%s</title>
                </head>
                                
                <body>
                <h1>%s</h1>
                <table style="width:90%%">
                """.formatted(title, title));

            for (var row : table) {
                writer.write("<tr>\n");
                for (var cell : row) {
                    writer.write("<td>" + cell + "</td>\n");
                }
                writer.write("</tr>\n");
            }

            writer.write("""
                </table>
                </body>
                </html>
                """);
        }
    }

    @Override
    public void clear() {
        table.clear();
        title = "(no title)";
    }
}