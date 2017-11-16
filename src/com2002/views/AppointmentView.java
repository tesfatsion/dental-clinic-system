package com2002.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com2002.interfaces.Screen;
import com2002.models.Appointment;
import com2002.models.Doctor;
import com2002.models.HealthPlan;
import com2002.models.Patient;
import com2002.models.Schedule;
import com2002.models.Usage;

public class AppointmentView implements Screen {
	
	private JPanel panel;
	private DisplayFrame frame;
	private Appointment appointment;
	private Patient patient;
	private Usage usage;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JPanel appointmentsPanel;
	private JScrollPane appointmentsScrollPane;
	private List<JPanel> appointmentCards;
	private ArrayList<Appointment> previousAppointments;

	public AppointmentView(DisplayFrame frame, Appointment appointment) {
		try {
			this.frame = frame;
			this.frame.setFrameSize(DisplayFrame.DEFAULT_NUM, 7);
			this.frame.centerFrame();
			this.appointment = appointment;
			this.patient = appointment.getPatient();
			this.usage = new Usage(this.patient.getPatientID());
			this.previousAppointments = Schedule.getDoctorAppointmentsByPatient(this.appointment.getUsername(), this.patient);
			initialize();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame,
				    "Error connecting to the database. Check internet connection.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
				    "Error fetching previous appointments.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void initialize() {
		this.panel = new JPanel();
		this.panel.setLayout(new BorderLayout());
		//Left panel
		this.leftPanel = new JPanel();
		this.leftPanel.setLayout(new BoxLayout(this.leftPanel, BoxLayout.Y_AXIS));
		//Left panel consists of two sections
		//First: patient details
		JPanel patientDetails = new JPanel();
		patientDetails.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		//name
		patientDetails.setLayout(new BoxLayout(patientDetails, BoxLayout.Y_AXIS));
		String fullName = this.patient.getTitle() + " " + this.patient.getFirstName() + " " + this.patient.getLastName();
		JLabel patientName = new JLabel(fullName, SwingConstants.LEFT);
		patientName.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE));
		patientDetails.add(patientName);

	    patientDetails.add(Box.createVerticalStrut(20));
		//dob and age
		String dob = this.patient.getDateOfBirth().toString();
		long years = ChronoUnit.YEARS.between(this.patient.getDateOfBirth(), LocalDate.now());
		dob = String.valueOf(years) + " years old" +" | " + dob;
		JLabel dateOfBirthAndAge = new JLabel(dob, SwingConstants.LEFT);
		dateOfBirthAndAge.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
		patientDetails.add(dateOfBirthAndAge);

	    patientDetails.add(Box.createVerticalStrut(20));
		//health plan
		JPanel hpPanel = new JPanel();
		hpPanel.setLayout(new BoxLayout(hpPanel, BoxLayout.Y_AXIS));
		String nameString = "No Health Plan";
		String checkupString = "Checkup: 0 out of 0";
		String repairString = "Repair: 0 out of 0";
		String hygieneString = "Hygiene: 0 out of 0";
		if (this.usage != null) {
			//hp name
			try {
				nameString = this.usage.getHealthPlanName();
				HealthPlan hp;
				hp = new HealthPlan(nameString);
				//usage
				int currentCheckup = this.usage.getCheckUpUsed();
				int totalCheckup = hp.getCheckUpLevel();
				checkupString = "<html><strong>Checkup:</strong> " + String.valueOf(currentCheckup) + " out of " + String.valueOf(totalCheckup) + "</html>";
				int currentRepair = this.usage.getRepairUsed();
				int totalRepair = hp.getRepairLevel();
				repairString = "<html><strong>Repair:</strong> " + String.valueOf(currentRepair) + " out of " + String.valueOf(totalRepair) + "</html>";
				int currentHygiene = this.usage.getHygieneUsed();
				int totalHygiene = hp.getHygieneLevel();
				hygieneString = "<html><strong>Hygiene:</strong> " + String.valueOf(currentHygiene) + " out of " + String.valueOf(totalHygiene) + "</html>";
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(frame,
					    "Error connecting to the database. Check internet connection.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
		Border border = BorderFactory.createTitledBorder("Health plan");
		((TitledBorder) border).setTitleFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
	    hpPanel.setBorder(border);
	    JLabel hpLabel = new JLabel(nameString);
	    hpLabel.setFont(new Font("Sans Serif", Font.BOLD,
				DisplayFrame.FONT_SIZE / 2));
	    hpPanel.add(hpLabel);
	    JLabel checkupLabel = new JLabel(checkupString);
	    checkupLabel.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
	    hpPanel.add(checkupLabel);
	    JLabel repairLabel = new JLabel(repairString);
	    repairLabel.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
	    hpPanel.add(repairLabel);
	    JLabel hygieneLabel = new JLabel(hygieneString);
	    hygieneLabel.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
	    hpPanel.add(hygieneLabel);
	    patientDetails.add(hpPanel);
		//Second: this appointment's finishing section
		
		//add panels to each other
		this.leftPanel.add(patientDetails);
		this.leftPanel.setMaximumSize(new Dimension(frame.getWidth() / 2, Integer.MAX_VALUE));
		this.panel.add(this.leftPanel, BorderLayout.CENTER);
		//right panel
		this.rightPanel = new JPanel();
		this.rightPanel.setLayout(new BoxLayout(this.rightPanel, BoxLayout.Y_AXIS));
		//label
		JLabel rightPanelLabel = new JLabel("Previous Appointments", SwingConstants.LEFT);
		rightPanelLabel.setFont(new Font("Sans Serif", Font.BOLD,
				DisplayFrame.FONT_SIZE));
		this.rightPanel.add(rightPanelLabel);
		//top section with the selected appointment's details
		
		//bottom section consists of list of all this patient's previous appointments if anys
		this.appointmentsPanel = new JPanel();
		this.appointmentsPanel.setLayout(new BoxLayout(this.appointmentsPanel, BoxLayout.Y_AXIS));
		//We want to be able to scroll through today's appointments
		this.appointmentsScrollPane = new JScrollPane(appointmentsPanel);
		this.appointmentsScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20), BorderFactory.createLineBorder(Color.black)));
		this.appointmentCards = new ArrayList<JPanel>();
		for (int i = 0; i < this.previousAppointments.size(); i++) {
			addAppointment(this.previousAppointments.get(i));
		}
		this.rightPanel.add(this.appointmentsScrollPane);
		this.panel.add(this.rightPanel, BorderLayout.EAST);
	}
	
	@SuppressWarnings("deprecation")
	private void addAppointment(Appointment appointment) {
		//init
		this.appointmentCards.add(new JPanel());
		int index = this.appointmentCards.size() - 1;
		String appointmentType = appointment.getAppointmentType();
		Timestamp startTimeTs = appointment.getStartTime();
		String appointmentDay = String.valueOf(startTimeTs.getDay()) + "/" + String.valueOf(startTimeTs.getMonth()) + "/" + String.valueOf(startTimeTs.getYear());
		//layout
		this.appointmentCards.get(index).setLayout(new BoxLayout(this.appointmentCards.get(index), BoxLayout.Y_AXIS));
		this.appointmentCards.get(index).setMaximumSize(new Dimension(Integer.MAX_VALUE, frame.getFrameHeightStep() / 2));
		// top content
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		JLabel appointmentTypeLabel = new JLabel(appointmentType);
		appointmentTypeLabel.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
		topPanel.add(appointmentTypeLabel);
		topPanel.add(Box.createHorizontalStrut(5));
		topPanel.add(new JSeparator(SwingConstants.VERTICAL));
		topPanel.add(Box.createHorizontalStrut(5));
		JLabel day = new JLabel(appointmentDay);
		day.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
		topPanel.add(day);
		this.appointmentCards.get(index).add(topPanel);
		// bottom content
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		JButton startAppointment = new JButton("More details");
		startAppointment.setFont(new Font("Sans Serif", Font.PLAIN,
				DisplayFrame.FONT_SIZE / 2));
		bottomPanel.add(startAppointment);
		//event listener for the button
		startAppointment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO: action when "more details" button is pressed
				
			}
		});
		this.appointmentCards.get(index).add(bottomPanel);
		//add panel to main panel
		this.appointmentsPanel.add(this.appointmentCards.get(index));
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		separator.setBackground(Color.black);
		this.appointmentsPanel.add(separator);
	}
	
	public JPanel getPanel() {
		return this.panel;
	}
}
