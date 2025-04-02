import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphUI {

    private Graph_M graph;
    private JFrame frame;
    private JTextArea resultArea;
    private JComboBox<String> sourceComboBox, destinationComboBox;

    private static final String INVALID_INPUT_MESSAGE = "Invalid input or no path found.";
    private static final String DISTANCE_LABEL = "Shortest Distance: ";
    private static final String TIME_LABEL = "Shortest Time: ";

    public GraphUI() throws IOException {
        graph = new Graph_M();
        Graph_M.Create_Metro_Map(graph);

        frame = new JFrame("Delhi Metro App");
        frame.setLayout(new GridBagLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        ArrayList<String> stations = new ArrayList<>(Graph_M.vtces.keySet());

        sourceComboBox = new JComboBox<>(stations.toArray(new String[0]));
        destinationComboBox = new JComboBox<>(stations.toArray(new String[0]));

        addComponents();

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(new JLabel("Source Station:"), gbc);

        gbc.gridx = 1;
        frame.add(sourceComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Destination Station:"), gbc);

        gbc.gridx = 1;
        frame.add(destinationComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addButton("Get Distance", 0, 2);
        addButton("Get Time", 0, 3);
        addButton("Get Path (Distance)", 0, 4);
        addButton("Get Path (Time)", 0, 5);
        addButton("Show Map", 0, 6);
        addButton("Show Stations", 0, 7);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        frame.add(new JScrollPane(resultArea), gbc);
    }

    private void addButton(String label, int gridx, int gridy) {
        JButton button = new JButton(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonClick(label);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(button, gbc);
    }

    private void handleButtonClick(String buttonLabel) {
        String source = (String) sourceComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();

        HashMap<String, Boolean> processed = new HashMap<>();
        if (!graph.containsVertex(source) || !graph.containsVertex(destination) || !graph.hasPath(source, destination, processed)) {
            resultArea.setText(INVALID_INPUT_MESSAGE);
            return;
        }

        switch (buttonLabel) {
            case "Get Distance":
                displayResult(graph.Get_Minimum_Distance(source, destination), DISTANCE_LABEL);
                break;
            case "Get Time":
                displayResult(graph.Get_Minimum_Time(source, destination), TIME_LABEL);
                break;
            case "Get Path (Distance)":
                displayResult(formatPath(graph.get_Interchanges(graph.Get_Minimum_Distance(source, destination))), "");
                break;
            case "Get Path (Time)":
                displayResult(formatPath(graph.get_Interchanges(graph.Get_Minimum_Time(source, destination))), "");
                break;
            case "Show Map":
                displayResult(getMapAsString(), "");
                break;
            case "Show Stations":
                displayResult(getStationsAsString(), "");
                break;
        }
    }

    private void displayResult(String result, String prefix) {
        if (result == null || result.isEmpty()) {
            resultArea.setText(INVALID_INPUT_MESSAGE);
        } else {
            resultArea.setText(prefix + result);
        }
    }

    private String formatPath(ArrayList<String> path) {
        StringBuilder sb = new StringBuilder();
        int len = path.size();

        sb.append("SOURCE STATION : ").append(path.get(0)).append("\n");
        sb.append("DESTINATION STATION : ").append(path.get(len - 3)).append("\n");
        sb.append("NUMBER OF INTERCHANGES : ").append(path.get(len - 2)).append("\n");
        sb.append("~~~~~~~~~~~~~\n");
        sb.append("START  ==>  ").append(path.get(0)).append("\n");

        for (int i = 1; i < len - 3; i++) {
            sb.append(path.get(i)).append("\n");
        }
        sb.append(path.get(len - 3)).append("   ==>    END\n");
        sb.append("~~~~~~~~~~~~~\n");
        sb.append("Distance/Time: ").append(path.get(len - 1)).append("\n");
        return sb.toString();
    }

    private String getMapAsString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<>(Graph_M.vtces.keySet());

        sb.append("\t Delhi Metro Map\n");
        sb.append("\t------------------\n");
        sb.append("----------------------------------------------------\n");

        for (String key : keys) {
            String str = key + " =>\n";
            Graph_M.Vertex vtx = Graph_M.vtces.get(key);
            ArrayList<String> vtxnbrs = new ArrayList<>(vtx.nbrs.keySet());

            for (String nbr : vtxnbrs) {
                str = str + "\t" + nbr + "\t";
                if (nbr.length() < 16)
                    str = str + "\t";
                if (nbr.length() < 8)
                    str = str + "\t";
                str = str + vtx.nbrs.get(nbr) + "\n";
            }
            sb.append(str);
        }
        sb.append("\t------------------\n");
        sb.append("---------------------------------------------------\n");
        return sb.toString();
    }

    private String getStationsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n***********************************************************************\n\n");
        ArrayList<String> keys = new ArrayList<>(Graph_M.vtces.keySet());
        int i = 1;
        for (String key : keys) {
            sb.append(i).append(". ").append(key).append("\n");
            i++;
        }
        sb.append("\n***********************************************************************\n\n");
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        new GraphUI();
    }
}