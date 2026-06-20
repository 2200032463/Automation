package com.assetms.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils – Reads test data from the AssetManagement_TestData.xlsx file.
 *
 * Each sheet maps to a test scenario (e.g. "Login", "AdminDashboard", ...).
 * Row 0  = header row (column names).
 * Row 1+ = data rows.
 *
 * Usage:
 *   List<Map<String,String>> rows = ExcelUtils.getSheetData("Login");
 *   String email = rows.get(0).get("Email");
 */
public class ExcelUtils {

    // Path to the shared test-data Excel file (placed at project root)
    private static final String EXCEL_PATH =
            System.getProperty("user.dir") + "/src/test/resources/AssetManagement_TestData.xlsx";

    /**
     * Returns all data rows from the given sheet as a list of column→value maps.
     *
     * @param sheetName  Name of the Excel sheet (e.g. "Login").
     * @return           List of rows, each row is a Map of {header → cell value}.
     */
    public static List<Map<String, String>> getSheetData(String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.err.println("[ExcelUtils] Sheet '" + sheetName + "' not found in " + EXCEL_PATH);
                return data;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return data;

            // Collect header names
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            // Collect data rows (skip header at index 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowMap = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowMap.put(headers.get(j), getCellValueAsString(cell));
                }
                data.add(rowMap);
            }

        } catch (IOException e) {
            System.err.println("[ExcelUtils] Error reading Excel: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Returns a single data row by row index (0-based, excluding header).
     *
     * @param sheetName  Sheet name.
     * @param rowIndex   0-based data row index (row after header).
     * @return           Map of {header → value} or empty map if not found.
     */
    public static Map<String, String> getRowData(String sheetName, int rowIndex) {
        List<Map<String, String>> allRows = getSheetData(sheetName);
        if (rowIndex < allRows.size()) {
            return allRows.get(rowIndex);
        }
        return new HashMap<>();
    }

    /**
     * Converts any POI Cell type to a safe String value.
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                // Return as integer string if it's a whole number
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }
}
