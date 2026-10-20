package org.example.ui;
import org.example.model.Shift;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ShiftTable extends JTable
{
    private final DefaultTableModel model;

    public ShiftTable()
    {
        String[] columns = {
                "Date", "Shift", "Start", "End", "Working with"
        };

        model = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        setModel(model);
    }

    public void setShifts(List<Shift> shifts)
    {
        model.setRowCount(0);

        for (Shift shift : shifts)
        {
            model.addRow(new Object[] {
                    shift.getDate(),
                    shift.getName(),
                    shift.getStart(),
                    shift.getEnd(),
                    ""
            });
        }
    }
}