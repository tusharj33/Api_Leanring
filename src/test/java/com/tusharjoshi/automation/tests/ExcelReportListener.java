package com.tusharjoshi.automation.tests;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ExcelReportListener implements ITestListener, ISuiteListener {
    private record Row(String className, String method, String status, long startMillis,
                       long durationMs, String thread, String error) {}

    private static final Queue<Row> RESULTS = new ConcurrentLinkedQueue<>();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String OUTPUT_DIR = "test-output/excel-reports";

    // ---------- collect results (called on the test's own thread) ----------

    @Override
    public void onStart(ISuite suite) {
        RESULTS.clear();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        record(result, "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        record(result, "FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        record(result, "SKIP");
    }

    private void record(ITestResult r, String status) {
        String error = "";
        Throwable t = r.getThrowable();
        if (t != null) {
            String msg = t.getMessage() != null ? t.getMessage() : t.toString();
            error = msg.replaceAll("\\s+", " ").trim();          // one line in Excel
            if (error.length() > 500) {
                error = error.substring(0, 500) + "...";
            }
        }
        long start = r.getStartMillis();
        long duration = (start > 0 && r.getEndMillis() >= start) ? r.getEndMillis() - start : 0;

        RESULTS.add(new Row(
                r.getTestClass().getRealClass().getSimpleName(),
                r.getMethod().getMethodName(),
                status,
                start,
                duration,
                Thread.currentThread().getName(),
                error));
    }

    // ---------- write the Excel file once the suite is finished ----------

    @Override
    public void onFinish(ISuite suite) {
        List<Row> rows = new ArrayList<>(RESULTS);
        rows.sort(Comparator.comparingLong(Row::startMillis));

        long passed = rows.stream().filter(r -> r.status().equals("PASS")).count();
        long failed = rows.stream().filter(r -> r.status().equals("FAIL")).count();
        long skipped = rows.stream().filter(r -> r.status().equals("SKIP")).count();
        int total = rows.size();

        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle header = style(wb, IndexedColors.GREY_25_PERCENT, true);
            CellStyle pass = style(wb, IndexedColors.LIGHT_GREEN, true);
            CellStyle fail = style(wb, IndexedColors.ROSE, true);
            CellStyle skip = style(wb, IndexedColors.LIGHT_YELLOW, true);
            CellStyle normal = style(wb, null, false);

            // ----- Results sheet -----
            Sheet sheet = wb.createSheet("Results");
            String[] headers = {"#", "Test Class", "Test Method", "Status",
                    "Start Time", "Duration (ms)", "Thread", "Error Message"};

            org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(header);
            }

            int rowNum = 1;
            for (Row r : rows) {
                org.apache.poi.ss.usermodel.Row x = sheet.createRow(rowNum);
                cell(x, 0, rowNum, normal);
                cell(x, 1, r.className(), normal);
                cell(x, 2, r.method(), normal);
                Cell status = x.createCell(3);
                status.setCellValue(r.status());
                status.setCellStyle(switch (r.status()) {
                    case "PASS" -> pass;
                    case "FAIL" -> fail;
                    default -> skip;
                });
                cell(x, 4, r.startMillis() > 0 ? format(r.startMillis()) : "", normal);
                cell(x, 5, r.durationMs(), normal);
                cell(x, 6, r.thread(), normal);
                cell(x, 7, r.error(), normal);
                rowNum++;
            }

            for (int i = 0; i < headers.length - 1; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(7, 90 * 256);                   // error column: fixed width
            sheet.createFreezePane(0, 1);                        // keep header visible
            if (total > 0) {
                sheet.setAutoFilter(new CellRangeAddress(0, total, 0, headers.length - 1));
            }

            // ----- Summary sheet -----
            Sheet sum = wb.createSheet("Summary");
            Object[][] summary = {
                    {"Suite", suite.getName()},
                    {"Run Time", LocalDateTime.now().format(TS)},
                    {"Total", total},
                    {"Passed", passed},
                    {"Failed", failed},
                    {"Skipped", skipped},
                    {"Pass %", total == 0 ? "0.0%" : String.format("%.1f%%", passed * 100.0 / total)}
            };
            for (int i = 0; i < summary.length; i++) {
                org.apache.poi.ss.usermodel.Row x = sum.createRow(i);
                cell(x, 0, summary[i][0], header);
                CellStyle valueStyle = switch (String.valueOf(summary[i][0])) {
                    case "Passed" -> pass;
                    case "Failed" -> fail;
                    case "Skipped" -> skip;
                    default -> normal;
                };
                cell(x, 1, summary[i][1], valueStyle);
            }
            sum.autoSizeColumn(0);
            sum.autoSizeColumn(1);
            wb.setSheetOrder("Summary", 0);                      // open on Summary
            wb.setActiveSheet(0);

            // ----- save -----
            Path dir = Paths.get(OUTPUT_DIR);
            Files.createDirectories(dir);
            Path file = dir.resolve("API_Results_" + LocalDateTime.now().format(FILE_TS) + ".xlsx");
            try (OutputStream out = Files.newOutputStream(file)) {
                wb.write(out);
            }
            System.out.println("Excel report written: " + file.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Could not write Excel report: " + e.getMessage());
        }
    }

    // ---------- helpers ----------

    private static void cell(org.apache.poi.ss.usermodel.Row row, int col, Object value, CellStyle style) {
        Cell c = row.createCell(col);
        if (value instanceof Number n) {
            c.setCellValue(n.doubleValue());
        } else {
            c.setCellValue(value == null ? "" : value.toString());
        }
        c.setCellStyle(style);
    }

    private static String format(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).format(TS);
    }

    private static CellStyle style(Workbook wb, IndexedColors fill, boolean bold) {
        CellStyle s = wb.createCellStyle();
        if (fill != null) {
            s.setFillForegroundColor(fill.getIndex());
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        Font f = wb.createFont();
        f.setBold(bold);
        s.setFont(f);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        s.setVerticalAlignment(VerticalAlignment.TOP);
        return s;
    }
}
