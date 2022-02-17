package com.imperva.stepping;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class Visualizer extends JFrame implements ViewerListener {
    private final Logger logger = LoggerFactory.getLogger(Visualizer.class);
    private final String TITLE = "Stepping Live Visualizer";
    private final String GRAPH_STYLE = "graph {padding: 100px; }";
    private final String NODE_STYLE = "shape:circle;fill-color: #4e81bd;size: 100px; text-alignment: center; text-size: 12px; text-color:#ffffff;";
    private final String EDGE_STYLE = "edge {text-size: 60px; text-color:Blue;}";
    private final String SYSTEM_NODE_STYLE = "shape:circle;fill-color: Yellow;size: 100px; text-alignment: center;";
    private final String REFRESH_TEXT = "Refresh";
    private Graph graph;
    private boolean initialized = false;
    private JButton refreshButton;
    private JLabel metadataLabel;
    private boolean refreshing = false;
    private HashMap<String, EdgeData> edgeWaitingList;
    private HashMap<String, Integer> allEdgeIds;
    private boolean loop = true;
    private List<Subject> subjects;
    private List<StepInfo> stepsInfo;
    private HashMap<String, List<String>> subjectsToFollowers = new HashMap<>();
    private HashMap<String,StatisticsReport> statisticsReports = new HashMap<>();

    public Visualizer(List<Subject> subjects, List<StepInfo> stepsInfo) {
        this.subjects = subjects;
        this.stepsInfo = stepsInfo;

        //* System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        edgeWaitingList = new HashMap<>();
        allEdgeIds = new HashMap<>();

        prepareData(subjects);

        initVisualization();
    }

    private String getDistributeIdByStepID(String stepId) {
        for(StepInfo s : stepsInfo) {
            if(s.getId().equals(stepId)) {
                if(s.getDistributionNodeId().equalsIgnoreCase("default")) return stepId;
                return s.getDistributionNodeId();
            }
        }
        return "";
    }

    private void prepareData(List<Subject> subjects) {
        List<String> subjectsNames = subjects.stream().map(subject -> subject.getSubjectType()).collect(Collectors.toList());
        subjectsToFollowers = getListOfFollowersPerSubject(subjectsNames);
    }

    void draw(String senderId, String subjectType) {
        if (senderId.equals("SYSTEM_STEP_MONITOR"))
            return;
        List<String> listOfFollowers = subjectsToFollowers.get(subjectType);
        logger.debug("**** Step Id: " + senderId + " is sending Subject: " + subjectType + " to the following Steps : " + String.join(",", listOfFollowers));
        addEdge(senderId, subjectType, listOfFollowers);
    }

//    private List<String> getListOfFollowers(String subjectType) {
//        List<String> listOfFollowers = subjectsToFollowers.get(subjectType);
//        if(listOfFollowers == null) {
//            Subject relevantSubjects = subjects.stream().filter(x -> x.getSubjectType().equals(subjectType)).findFirst().get();
//            listOfFollowers = relevantSubjects.getCopyFollowersNames();
//            subjectsToFollowers.put(subjectType, listOfFollowers);
//        }
//        return listOfFollowers;
//    }
//
//    private HashMap<String, List<String>> getListOfFollowersPerSubject(String subjectType) {
//        HashMap<String, List<String>> followerPerSubject = new HashMap<>();
//
//
//        Subject relevantSubjects = subjects.stream().filter(subject -> subject.getSubjectType().equals(subjectType)).findFirst().get();
//        listOfFollowers = relevantSubjects.getCopyFollowersNames();
//        subjectsToFollowers.put(subjectType, listOfFollowers);
//
//        return listOfFollowers;
//    }

    private HashMap<String, List<String>> getListOfFollowersPerSubject(List<String> subjectTypes) {
        HashMap<String, List<String>> followerPerSubject = new HashMap<>();
        subjectTypes.forEach(sub->{
            Subject relevantSubjects = subjects.stream().filter(subject -> subject.getSubjectType().equals(sub)).findFirst().get();
            followerPerSubject.put(sub, relevantSubjects.getCopyFollowersNames());
        });
        return followerPerSubject;
    }

    @Override
    public void viewClosed(String s) {
        loop = false;
    }

    @Override
    public void buttonPushed(String destId) {
        printMetadata(destId);
    }

    private void printMetadata(String destId) {
        String text = "";
        for(StepInfo s : stepsInfo) {
            if(this.statisticsReports.containsKey(s.getId())
                    && getDistributeIdByStepID(s.getId()).equals(destId)) {
                StatisticsReport statisticsReport = this.statisticsReports.get(s.getId());
                text += "<H3>" + s.getId() + "</H3>" +
                        "<b>Avg Processing Time: </b>" + statisticsReport.getAvgProcessingTime() + " seconds" +
                        "<br> <b>Avg  Chunk Size: </b>" + statisticsReport.getAvgChunkSize() +
                        "<br> <b>Queue Size: </b>" + statisticsReport.getLatestQSize();
            }
        }
        if(!text.isEmpty()) {
            metadataLabel.setText("<html>" + text + "</html>");
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

    private void addEdge(String stepId, String subject, List<String> subjectObservers) {
        if (!initialized) return;
        Set<String> distSubjectObservers = new HashSet<>();
        for (String dest : subjectObservers) {
            distSubjectObservers.add(getDistributeIdByStepID(dest));
        }

        for (String dest : distSubjectObservers) {
            if(dest.equals("SYSTEM_STEP_MONITOR"))
                continue;
            String id = renderEdgeId(stepId, subject, dest);

            if (!allEdgeIds.containsKey(id)) {//edge doesn't exist
                allEdgeIds.put(id, 1);
                EdgeData edgeData = new EdgeData(getDistributeIdByStepID(stepId), subject, dest);
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

    private void initVisualization(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
        refreshButton.addActionListener(e -> {
            if(edgeWaitingList.isEmpty()) return;

            viewer.disableAutoLayout();
            refreshing = true;
            refreshButton.setEnabled(false);

            Iterator<Map.Entry<String,EdgeData>> iter = edgeWaitingList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String,EdgeData> entry = iter.next();
                addEdge(entry.getKey(), entry.getValue());
                iter.remove();
            }
            fixOverlappingEdgeLabel();
            refreshing = false;
            updateRefreshButton();
            refreshButton.setEnabled(true);
        });

        metadataLabel = new JLabel("Press any node for more information");
        add(metadataLabel, BorderLayout.SOUTH);

        initialized = true;

        Map<String, StepData> distUniqueList = new HashMap<>();
        for(StepInfo s : stepsInfo) {
            if(s.getId().equals("SYSTEM_STEP_MONITOR"))
                continue;

            String id = getDistributeIdByStepID(s.getId());
            String name = s.getId();
            if(!s.getId().equals(id) && name.contains(".")) { //if has distribution
                name = name.substring(0, name.lastIndexOf('.'));
            }
            StepData stepData = distUniqueList.getOrDefault(id, new StepData(name));
            stepData.count++;
            distUniqueList.put(id, stepData);
        }

        for(Map.Entry<String, StepData> stepEntry : distUniqueList.entrySet()) {
            Node a = graph.addNode(stepEntry.getKey());
            a.setAttribute("ui.label", stepEntry.getValue().getFullName());
            a.setAttribute("ui.style", NODE_STYLE);
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

    private void fixOverlappingEdgeLabel() {
        final int offsetInterval = 15;
        for (Node node : graph) {

            List<Edge> leavingEdgeList = node.leavingEdges().sorted((e1, e2) -> e1.getId().compareTo(e2.getId()))
                    .collect(Collectors.toList());

            List<Edge> enteringEdgeList = node.enteringEdges().sorted((e1, e2) -> e1.getId().compareTo(e2.getId()))
                    .collect(Collectors.toList());

            List<Node> neighbouringNodeList = node.neighborNodes().sorted((n1, n2) -> n1.getId().compareTo(n2.getId()))
                    .collect(Collectors.toList());

            for (Node neighbouringNode : neighbouringNodeList) {
                ArrayList<Edge> edgeList = new ArrayList<Edge>(leavingEdgeList.size());

                for (Edge edge : leavingEdgeList) {
                    if (edge.getTargetNode().equals(neighbouringNode))
                        edgeList.add(edge);
                } // add edge to edgeList if its target node is the same as targetNode

                for (Edge edge : enteringEdgeList) {
                    if (edge.getSourceNode().equals(neighbouringNode)) {
                        edgeList.add(edge);
                    }
                }

                int edgeListSize = edgeList.size();
                BigDecimal factor = BigDecimal.valueOf(edgeListSize / 2).setScale(0, RoundingMode.FLOOR);
                int maxOffset = factor.intValue() * offsetInterval;
                for (int i = 0, offset = maxOffset; i < edgeListSize; i++, offset -= offsetInterval) {
                    edgeList.get(i).setAttribute("ui.style", "text-offset: " + offset +"," + offset + ";");
                }
            } // loop neighbouring nodes

        } // loop each node in a graph

    }// function to fix overlapping edge label

    private void updateRefreshButton() {
        refreshButton.setText(REFRESH_TEXT + " (" + edgeWaitingList.size() + ")");
    }

    class StepData {
        private String name;
        public int count;
        public StepData(String name) {
            this.name = name;
            this.count = 0;
        }
        public String getFullName() {
            if(count > 1) return name + "(" + count + ")";
            return name;
        }
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

