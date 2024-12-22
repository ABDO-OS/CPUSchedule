import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.List;

public class GUI {
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 50);
    private static final Color GLOW_COLOR = new Color(100, 149, 237, 50);
    private static final Color ACCENT_COLOR = new Color(100, 149, 237);
    private static final Color TABLE_BG = new Color(60, 60, 65);
    private static final Color TABLE_FG = new Color(220, 220, 220);
    private static final Color CHART_BG = new Color(50, 50, 55);

    private JFrame frame;
    private JPanel mainPanel;
    private CustomPanel chartPanel;
    private JScrollPane tablePane;
    private JScrollPane chartPane;
    private JTable table;
    private JButton addBtn;
    private JButton removeBtn;
    private JButton computeBtn;
    private JLabel wtLabel;
    private JLabel wtResultLabel;
    private JLabel tatLabel;
    private JLabel tatResultLabel;
    private JComboBox<String> option;
    private DefaultTableModel model;
    private JPanel teamPanel;


    public GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        model = new DefaultTableModel(new String[]{"Process", "AT", "BT", "Priority", "WT", "TAT"}, 0);

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                comp.setBackground(row % 2 == 0 ? TABLE_BG : TABLE_BG.darker());
                comp.setForeground(TABLE_FG);
                return comp;
            }
        };
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(70, 70, 75));
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setBackground(BACKGROUND_COLOR);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Left panel for table and buttons
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setPreferredSize(new Dimension(500, 600));

        tablePane = new JScrollPane(table);
        tablePane.setBounds(25, 25, 450, 400);
        tablePane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));

        addBtn = createStyledButton("Add", 300, 435);
        removeBtn = createStyledButton("Remove", 390, 435);

        addBtn.addActionListener(e -> model.addRow(new String[]{"", "", "", "", "", ""}));
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row > -1) model.removeRow(row);
        });

        leftPanel.add(tablePane);
        leftPanel.add(addBtn);
        leftPanel.add(removeBtn);

        // Right panel for chart and controls
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setPreferredSize(new Dimension(500, 600));

        chartPanel = new CustomPanel();
        chartPanel.setBackground(CHART_BG);
        chartPane = new JScrollPane(chartPanel);
        chartPane.setBounds(25, 25, 450, 400);
        chartPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));

        wtLabel = createStyledLabel("Average Waiting Time:", 25, 435);
        tatLabel = createStyledLabel("Average Turn Around Time:", 25, 465);
        wtResultLabel = createStyledLabel("", 215, 435);
        tatResultLabel = createStyledLabel("", 215, 465);

        option = new JComboBox<>(new String[]{"FCFS", "SJF", "SRT", "PSN", "PSP", "RR"});
        option.setBounds(25, 505, 85, 25);
        option.setBackground(TABLE_BG);
        option.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        computeBtn = createStyledButton("Compute", 120, 505);
        computeBtn.addActionListener(e -> computeScheduling());

        teamPanel = new JPanel();
        teamPanel.setLayout(new GridLayout(5, 1, 0, 2)); // 5 rows: 1 for title, 4 for members
        teamPanel.setBackground(BACKGROUND_COLOR);
        JLabel teamTitleLabel = new JLabel("Team Members:");
        teamTitleLabel.setForeground(ACCENT_COLOR);
        teamTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel member1Label = new JLabel("Abdalrhman Osama");
        JLabel member2Label = new JLabel("Abdelrhman ashraf");
        JLabel member3Label = new JLabel("Ahmed Emad");
        JLabel member4Label = new JLabel("Abdulrhman Mohamed");

        JLabel[] memberLabels = {member1Label, member2Label, member3Label, member4Label};
        for (JLabel label : memberLabels) {
            label.setForeground(TABLE_FG);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        teamPanel.add(teamTitleLabel);
        teamPanel.add(member1Label);
        teamPanel.add(member2Label);
        teamPanel.add(member3Label);
        teamPanel.add(member4Label);

        teamPanel.setBounds(25, 500, 250, 100);
        leftPanel.add(teamPanel);


        rightPanel.add(chartPane);
        rightPanel.add(wtLabel);
        rightPanel.add(tatLabel);
        rightPanel.add(wtResultLabel);
        rightPanel.add(tatResultLabel);
        rightPanel.add(option);
        rightPanel.add(computeBtn);

        // Main panel with horizontal layout
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int glowSize = 20;
                for (int i = 0; i < glowSize; i++) {
                    float alpha = 1.0f - (float)i / glowSize;
                    g2d.setColor(new Color(
                            GLOW_COLOR.getRed(),
                            GLOW_COLOR.getGreen(),
                            GLOW_COLOR.getBlue(),
                            (int)(alpha * 255)
                    ));
                    g2d.drawRect(i, i, getWidth() - 2*i, getHeight() - 2*i);
                }
            }
        };
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        frame = new JFrame("CPU Scheduler Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(1000, 450));
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Add component listener for resize handling
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth();
                int height = frame.getHeight();

                // Update panel sizes
                leftPanel.setPreferredSize(new Dimension(width/2, height));
                rightPanel.setPreferredSize(new Dimension(width/2, height));

                // Update component positions
                tablePane.setSize(leftPanel.getWidth() - 50, height - 200);
                chartPane.setSize(rightPanel.getWidth() - 50, height - 200);

                // Reposition buttons and labels
                addBtn.setLocation(tablePane.getX() + tablePane.getWidth() - 180, tablePane.getY() + tablePane.getHeight() + 10);
                removeBtn.setLocation(tablePane.getX() + tablePane.getWidth() - 90, tablePane.getY() + tablePane.getHeight() + 10);

                wtLabel.setLocation(25, chartPane.getY() + chartPane.getHeight() + 10);
                tatLabel.setLocation(25, chartPane.getY() + chartPane.getHeight() + 40);
                wtResultLabel.setLocation(215, chartPane.getY() + chartPane.getHeight() + 10);
                tatResultLabel.setLocation(215, chartPane.getY() + chartPane.getHeight() + 40);
                option.setLocation(25, chartPane.getY() + chartPane.getHeight() + 80);
                computeBtn.setLocation(120, chartPane.getY() + chartPane.getHeight() + 80);
                teamPanel.setLocation(25, height - 140);
            }
        });
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 85, 25);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    private JLabel createStyledLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 180, 25);
        label.setForeground(TABLE_FG);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private void computeScheduling() {
        String selected = (String) option.getSelectedItem();
        CPUScheduler scheduler;

        switch (selected) {
            case "FCFS":
                scheduler = new FirstComeFirstServe();
                break;
            case "SJF":
                scheduler = new ShortestJobFirst();
                break;
            case "SRT":
                scheduler = new ShortestRemainingTime();
                break;
            case "PSN":
                scheduler = new PriorityNonPreemptive();
                break;
            case "PSP":
                scheduler = new PriorityPreemptive();
                break;
            case "RR":
                String tq = JOptionPane.showInputDialog("Time Quantum");
                if (tq == null) return;
                scheduler = new RoundRobin();
                scheduler.setTimeQuantum(Integer.parseInt(tq));
                break;
            default:
                return;
        }

        processSchedulerData(scheduler);
    }

    private void processSchedulerData(CPUScheduler scheduler) {
        for (int i = 0; i < model.getRowCount(); i++) {
            String process = (String) model.getValueAt(i, 0);
            int at = Integer.parseInt((String) model.getValueAt(i, 1));
            int bt = Integer.parseInt((String) model.getValueAt(i, 2));
            int pl = (!model.getValueAt(i, 3).equals("") ?
                    Integer.parseInt((String) model.getValueAt(i, 3)) : 1);

            scheduler.add(new Row(process, at, bt, pl));
        }

        scheduler.process();

        for (int i = 0; i < model.getRowCount(); i++) {
            String process = (String) model.getValueAt(i, 0);
            Row row = scheduler.getRow(process);
            model.setValueAt(row.getWaitingTime(), i, 4);
            model.setValueAt(row.getTurnaroundTime(), i, 5);
        }

        wtResultLabel.setText(String.format("%.2f", scheduler.getAverageWaitingTime()));
        tatResultLabel.setText(String.format("%.2f", scheduler.getAverageTurnAroundTime()));
        chartPanel.setTimeline(scheduler.getTimeline());
    }

    class CustomPanel extends JPanel {
        private List<Event> timeline;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (timeline != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 0; i < timeline.size(); i++) {
                    Event event = timeline.get(i);
                    int x = 30 * (i + 1);
                    int y = 20;

                    // Draw process box with gradient
                    GradientPaint gradient = new GradientPaint(
                            x, y, ACCENT_COLOR,
                            x + 30, y + 30, ACCENT_COLOR.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(x, y, 30, 30, 10, 10);
                    g2d.setColor(ACCENT_COLOR.darker());
                    g2d.drawRoundRect(x, y, 30, 30, 10, 10);

                    // Draw text
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    g2d.drawString(event.getProcessName(), x + 10, y + 20);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2d.drawString(Integer.toString(event.getStartTime()), x - 5, y + 45);

                    if (i == timeline.size() - 1) {
                        g2d.drawString(Integer.toString(event.getFinishTime()),
                                x + 27, y + 45);
                    }
                }
            }
        }

        public void setTimeline(List<Event> timeline) {
            this.timeline = timeline;
            repaint();
        }
    }

    class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1,
                    this.radius + 1, this.radius + 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(c.getBackground());
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}