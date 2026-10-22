package org.example.employee.request;

import org.example.model.RequestDecision;
import org.example.model.RequestKind;
import org.example.model.ShiftChoice;
import org.example.model.TimeOffType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class RequestPane extends JPanel
{

//    Show the form
//    Expose the selected values
//    Allow the controller to register a submit listener
//    Enable or disable submission
//    Display results



    private final JComboBox<RequestKind> requestKindBox =
            new JComboBox<>(RequestKind.values());

    private final JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());

    private final JComboBox<TimeOffType> timeOffTypeBox = new JComboBox<>(TimeOffType.values());

    private final JComboBox<ShiftChoice> shiftBox = new JComboBox<>();

    private final JButton submitButton = new JButton("Submit Request");

    private final JLabel statusLabel = new JLabel(" ");

    private final CardLayout requestCardLayout = new CardLayout();

    private final JPanel requestCards =
            new JPanel(requestCardLayout);

    public RequestPane()
    {
        setLayout(new BorderLayout(10, 10));

        dateSpinner.setEditor(
                new JSpinner.DateEditor(
                        dateSpinner,
                        "yyyy-MM-dd"
                )
        );

        JPanel commonFields = new JPanel(
                new GridLayout(2, 2, 5, 5)
        );

        commonFields.add(new JLabel("Request:"));
        commonFields.add(requestKindBox);
        commonFields.add(new JLabel("Date:"));
        commonFields.add(dateSpinner);

        JPanel timeOffCard = new JPanel(
                new GridLayout(1, 2, 5, 5)
        );

        timeOffCard.add(new JLabel("Time-off type:"));
        timeOffCard.add(timeOffTypeBox);

        JPanel workCard = new JPanel(
                new GridLayout(1, 2, 5, 5)
        );

        workCard.add(new JLabel("Shift:"));
        workCard.add(shiftBox);

        requestCards.add(
                workCard,
                RequestKind.WORK.name()
        );

        requestCards.add(
                timeOffCard,
                RequestKind.TIME_OFF.name()
        );

        JPanel form = new JPanel(new BorderLayout(10, 10));
        form.add(commonFields, BorderLayout.NORTH);
        form.add(requestCards, BorderLayout.CENTER);
        form.add(submitButton, BorderLayout.SOUTH);

        add(form, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        requestKindBox.addActionListener(event ->
                showSelectedRequestCard()
        );

        showSelectedRequestCard();
    }

    private void showSelectedRequestCard()
    {
        RequestKind kind = getSelectedRequestKind();

        requestCardLayout.show(
                requestCards,
                kind.name()
        );
    }

    public RequestKind getSelectedRequestKind()
    {
        return (RequestKind) requestKindBox.getSelectedItem();
    }

    public LocalDate getSelectedDate()
    {
        Date selected = (Date) dateSpinner.getValue();

        return selected.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public TimeOffType getSelectedTimeOffType()
    {
        return (TimeOffType) timeOffTypeBox.getSelectedItem();
    }

    public ShiftChoice getSelectedShift()
    {
        return (ShiftChoice) shiftBox.getSelectedItem();
    }

    public void setShiftChoices(List<ShiftChoice> choices)
    {
        DefaultComboBoxModel<ShiftChoice> model =
                new DefaultComboBoxModel<>();

        for (ShiftChoice choice : choices)
        {
            model.addElement(choice);
        }

        shiftBox.setModel(model);
    }

    public void addSubmitListener(ActionListener listener)
    {
        submitButton.addActionListener(listener);
    }

    public void setSubmitting(boolean submitting)
    {
        submitButton.setEnabled(!submitting);
        requestKindBox.setEnabled(!submitting);
        dateSpinner.setEnabled(!submitting);
        timeOffTypeBox.setEnabled(!submitting);
        shiftBox.setEnabled(!submitting);

        if (submitting)
        {
            statusLabel.setText("Submitting request...");
        }
    }

    public void showDecision(RequestDecision decision)
    {
        statusLabel.setText(decision.message());

        int messageType = decision.approved()
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.WARNING_MESSAGE;

        JOptionPane.showMessageDialog(
                this,
                decision.message(),
                decision.approved()
                        ? "Request Approved"
                        : "Request Denied",
                messageType
        );
    }

    public void showError(String message)
    {
        statusLabel.setText(message);

        JOptionPane.showMessageDialog(
                this,
                message,
                "Request Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


}

