package com.aljjabaegi.player.component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * grid component
 *
 * @author GEON LEE
 * @apiNote 2024-01-29 GEON LEE - JTable extends 방식 으로 변경
 * - option 으로 column mapping, column size, editable, sortable 처리 되게 수정
 * - sort method 추가
 * 2024-02-05 GEON LEE - option array to list
 * @since 2024-01-22
 */
public class CustomTable extends JTable {
    private final List<String> columnList;
    private final List<Integer> columnSizeList;
    private final boolean editable;
    private final boolean sortable;
    private final boolean focusable;
    private final boolean reordering;
    private final Map<String, String> selectedRowData = new HashMap<>();

    @SuppressWarnings("unchecked")
    public CustomTable(Map<CustomTableOptions, Object> option) {
        super();
        this.columnList = (List<String>) option.get(CustomTableOptions.COLUMNS); /*column names*/
        this.columnSizeList = (List<Integer>) option.get(CustomTableOptions.COLUMN_SIZE); /*column size, default size/n*/
        this.editable = option.get(CustomTableOptions.EDITABLE) != null; /*editable, default false*/
        this.sortable = option.get(CustomTableOptions.SORTABLE) != null; /*sortable, default false*/
        this.focusable = option.get(CustomTableOptions.FOCUSABLE) != null; /*focusable, default false*/
        this.reordering = option.get(CustomTableOptions.REORDERING) != null; /*header reordering, default false*/
        initGUI();
    }

    /**
     * grid GUI 생성
     */
    public void initGUI() {
        /*default table border 및 padding 설정*/
        super.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Border border = BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY);
                ((JComponent) component).setBorder(BorderFactory.createCompoundBorder(
                        border, // Border 설정
                        new EmptyBorder(0, 5, 0, 0) // Padding 설정
                ));
                return component;

            }
        });

        DefaultTableModel model = new DefaultTableModel(null, this.columnList.toArray()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };

        super.setModel(model);
        super.setFocusable(this.focusable);
        super.getTableHeader().setReorderingAllowed(this.reordering);
        if (this.sortable) {
            super.setRowSorter(new TableRowSorter<>(model));
        }
        if (this.columnSizeList != null) {
            for (int i = 0, n = this.columnSizeList.size(); i < n; i++) {
                /*setPreferredWidth 를 줄 경우 size 가 변경됨.*/
                this.getColumnModel().getColumn(i).setMaxWidth(this.columnSizeList.get(i));
                this.getColumnModel().getColumn(i).setMinWidth(this.columnSizeList.get(i));
            }
        }
    }

    /**
     * 단일 정렬 메서드
     * - 테이블 정렬 메서드, option 에 sortable 이 true 여야 동작
     */
    public void sort(int columnIndex, SortOrder sort) {
        if (this.sortable) {
            this.getRowSorter().setSortKeys(List.of(new RowSorter.SortKey(columnIndex, sort)));
        }
    }

    public void clearRows() {
        DefaultTableModel tableModel = (DefaultTableModel) super.getModel();
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
    }

    public void addRow(Object[] rowData) {
        DefaultTableModel tableModel = (DefaultTableModel) super.getModel();
        tableModel.addRow(rowData);
    }

    public Map<String, String> getSelectedRowData() {
        int selectedRowIndex = super.getSelectedRow();
        if (selectedRowIndex != -1) {
            for (int i = 0, n = super.getColumnCount(); i < n; i++) {
                selectedRowData.put(this.columnList.get(i), String.valueOf(super.getValueAt(selectedRowIndex, i)));
            }
            return selectedRowData;
        } else {
            return null;
        }
    }
}