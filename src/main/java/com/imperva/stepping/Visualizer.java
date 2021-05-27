package com.imperva.stepping;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

class Visualizer extends JFrame implements ViewerListener {

    private final String TITLE = "Stepping Live Visualizer";
    private final String GRAPH_STYLE = "graph {padding: 100px; }";
    private final String NODE_STYLE = "shape:circle;fill-color: #4e81bd;size: 100px; text-alignment: center; text-size: 12px; text-color:#ffffff;";
    private final String EDGE_STYLE = "edge {text-size: 60px; text-color:Blue;}";
    private final String SYSTEM_NODE_STYLE = "shape:circle;fill-color: Yellow;size: 100px; text-alignment: center;";
    private final String REFRESH_TEXT = "Refresh";
    private HashMap<String, String> nodes;
    private Graph graph;
    private boolean initialized = false;
    private JButton refreshButton;
    private JLabel metadataLabel;
    private boolean refreshing = false;
    private HashMap<String, EdgeData> edgeWaitingList;
    private HashMap<String, Integer> allEdgeIds;

    private boolean loop = true;

    private List<Subject> subjects;
    private List<String> stepIds;
    private List<StatisticsReport> oldStatisticsReports = new ArrayList<>();
    private HashMap<String,StatisticsReport> statisticsReports = new HashMap<>();

    public Visualizer(List<Subject> subjects, List<String> stepIds) {
        this.subjects = subjects;
        this.stepIds = stepIds;

       // System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        nodes = new HashMap<>();
        edgeWaitingList = new HashMap<>();
        allEdgeIds = new HashMap<>();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        init();
    }

    void draw(String senderId, String subjectType) {
        if (senderId.equals("SYSTEM_STEP_MONITOR"))
            return;
        Subject relevantSubjects = subjects.stream().filter(x -> x.getSubjectType().equals(subjectType)).findFirst().get();
        List<String> stepsReceivers = relevantSubjects.getCopyObserversNames();
        System.out.println("**** Step Id: " + senderId + " is sending Subject: " + subjectType + " to the following Steps : " + String.join(",", stepsReceivers));
        addEdge(senderId, subjectType, stepsReceivers);
    }

    public void addEdge(String stepId, String subject, List<String> subjectObservers) {
        if (!initialized) return;
        for (String dest : subjectObservers) {
            if(dest.equals("SYSTEM_STEP_MONITOR"))
                continue;
            String id = renderEdgeId(stepId, subject, dest);

            if (!allEdgeIds.containsKey(id)) {//edge doesn't exist
                allEdgeIds.put(id,1);
                EdgeData edgeData = new EdgeData(stepId, subject, dest);
                if (refreshing) {
                    addEdge(dest, edgeData);
                } else {
                    edgeWaitingList.put(id, edgeData);
                    updateRefreshButton();
                }
            } else {
              int counter = allEdgeIds.get(id);
                counter++;
                allEdgeIds.put(id, counter);
                Edge edge = graph.getEdge(id);
                if(edge == null)
                    return; //*IT MEANS THAT THE EDGE WAS STILL NOT DRAWN BECAUSE THE USER NEVER CLICKED ON REFRESH BUTTON
                edge.setAttribute("ui.label", subject + ":" + counter);
                edge.setAttribute("ui.stylesheet", EDGE_STYLE);
            }
        }
    }

    private String renderEdgeId(String stepId, String subject, String dest) {
        return stepId + "-" + subject + "-" + dest;
    }

    private void addEdge(String id, EdgeData edgeData) {
        if(edgeData.destinationClass.equals("SYSTEM_STEP_MONITOR"))
            return;
        graph.addEdge(id, edgeData.sourceClass, edgeData.destinationClass, true).setAttribute("ui.label", edgeData.subject);
    }

    void init(){
        graph = new MultiGraph(TITLE);
        graph.setAttribute("ui.stylesheet", GRAPH_STYLE);
        graph.setAttribute("ui.quality");


        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        add((DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer()), BorderLayout.CENTER);

        ViewerPipe pipe = viewer.newViewerPipe();
        pipe.addAttributeSink(graph);
        pipe.addViewerListener(this);
        pipe.pump();

        setTitle(TITLE);
        setSize( 800, 600 );
        setVisible(true);


        refreshButton = new JButton();
        refreshButton.setBounds(750, 550, 600, 300);
        updateRefreshButton();
        add(refreshButton, BorderLayout.NORTH);
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(edgeWaitingList.isEmpty()) return;

                refreshing = true;
                refreshButton.setEnabled(false);

                Iterator<Map.Entry<String,EdgeData>> iter = edgeWaitingList.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String,EdgeData> entry = iter.next();
                    addEdge(entry.getKey(), entry.getValue());
                    iter.remove();
                }
                refreshing = false;
                updateRefreshButton();
                refreshButton.setEnabled(true);
            }
        });

        metadataLabel = new JLabel("Press any node for more infomration");
        add(metadataLabel, BorderLayout.SOUTH);


        initialized = true;



        for(String s : stepIds) {
            if(s.equals("SYSTEM_STEP_MONITOR"))
                continue;
            nodes.put(s, s);

            Node a = graph.addNode(s);
            a.setAttribute("ui.label", s);

            a.setAttribute("ui.style", s.equals("SYSTEM_STEP_MONITOR") ?   SYSTEM_NODE_STYLE : NODE_STYLE);
        }

        //keep listening to events
        new Thread( () ->  {
            while(loop) {
                pipe.pump();
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();
    }


    private void updateRefreshButton() {
        refreshButton.setText(REFRESH_TEXT + " (" + edgeWaitingList.size() + ")");
    }

    @Override
    public void viewClosed(String s) {
        loop = false;
    }

    @Override
    public void buttonPushed(String stepId) {
        if (this.statisticsReports.containsKey(stepId)) {
            StatisticsReport statisticsReport = this.statisticsReports.get(stepId);
            metadataLabel.setText("<html><H3>" + stepId + "</H3>" +
                    "<b>Avg Processing Time: </b>" + statisticsReport.getAvgProcessingTime() + " seconds" +
                    "<br> <b>Avg  Chunk Size: </b>" + statisticsReport.getAvgChunkSize() +
                    "<br> <b>Queue Size: </b>" + statisticsReport.getLatestQSize() +
                    "</html>");
        }
    }

    @Override
    public void buttonReleased(String s) {

    }

    @Override
    public void mouseOver(String s) {

    }

    @Override
    public void mouseLeft(String s) {

    }

    void updateMetadata(List<StatisticsReport> statisticsReports) {



        statisticsReports.forEach(stat->{

            this.statisticsReports.put(stat.getStepSenderId(), stat);
        });

    }

    class EdgeData {
        public String sourceClass;
        public String subject;
        public String destinationClass;

        public EdgeData(String sourceClass, String subject, String destinationClass) {
            this.sourceClass = sourceClass;
            this.subject = subject;
            this.destinationClass = destinationClass;
        }

        public String getId(String sourceClass, String subject, String destinationClass) {
            return sourceClass + "-" + subject + "-" + destinationClass;
        }
    }
}

