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
    private Graph graph;
    private boolean initialized = false;
    private JButton refreshButton;
    private JLabel metadataLabel;
    private boolean refreshing = false;
    private HashMap<String, EdgeData> edgeWaitingList = new HashMap<>();
    private HashMap<String, Integer> allEdgeIds = new HashMap<>();
    private boolean loop = true;
    private List<Subject> subjects;
    private List<VisualStep> visualSteps;
    private Map<String, VisualStep> visualStepsMap = new HashMap<>();
    private HashMap<String, List<String>> subjectsToFollowers;
    private HashMap<String, StatisticsReport> statisticsReports = new HashMap<>();

    Visualizer(List<Subject> subjects, List<VisualStep> visualSteps) {
        this.subjects = subjects;
        this.visualSteps = visualSteps;

        for (VisualStep vstep : visualSteps) {
            visualStepsMap.put(vstep.getId(), vstep);
        }
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        subjectsToFollowers = getFollowers(subjects);
    }

    private String getDistributeIdByStepID(String stepId) {
        if (visualStepsMap.containsKey(stepId)) {
            VisualStep s = visualStepsMap.get(stepId);
            return !s.hasMultipleNodes() ? s.getId() : s.getDistributionNodeId();
        } else {
            return "";
        }
    }

    private HashMap<String, List<String>> getFollowers(List<Subject> subjects) {
        List<String> subjectsNames = subjects.stream().map(subject -> subject.getSubjectType()).collect(Collectors.toList());
        return getListOfFollowersPerSubject(subjectsNames);
    }

    void draw(String senderId, String subjectType) {
        if (senderId.equals("SYSTEM_STEP_MONITOR"))
            return;
        List<String> listOfFollowers = subjectsToFollowers.get(subjectType);
        logger.debug("**** Step Id: " + senderId + " is sending Subject: " + subjectType + " to the following Steps : " + String.join(",", listOfFollowers));
        addEdge(senderId, subjectType, listOfFollowers);
    }

    private HashMap<String, List<String>> getListOfFollowersPerSubject(List<String> subjectTypes) {
        HashMap<String, List<String>> followerPerSubject = new HashMap<>();
        subjectTypes.forEach(sub -> {
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
        for (VisualStep s : visualSteps) {
            if (this.statisticsReports.containsKey(s.getId())
                    && getDistributeIdByStepID(s.getId()).equals(destId)) {
                StatisticsReport statisticsReport = this.statisticsReports.get(s.getId());
                text += TextConst.METADATA
                        .replace("{STEP_ID}", s.getId())
                        .replace("{AVG_PROCESSING_TIME}", statisticsReport.getAvgProcessingTime() + "")
                        .replace("{AVG_CHUNK_SIZE}", statisticsReport.getAvgChunkSize() + "")
                        .replace("{QUEUE_SIZE}", statisticsReport.getLatestQSize() + "");
            }
        }
        if (!text.isEmpty()) {
            metadataLabel.setText("<html>" + text + "</html>");
        }
    }

    void updateMetadata(List<StatisticsReport> statisticsReports) {
        statisticsReports.forEach(stat -> {
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
            if (dest.equals("SYSTEM_STEP_MONITOR"))
                continue;
            String id = renderEdgeId(stepId, subject, dest);

            if (!allEdgeIds.containsKey(id)) {//edge doesn't exist
                allEdgeIds.put(id, 1);
                EdgeData edgeData = new EdgeData(getDistributeIdByStepID(stepId), subject, dest);

                if (refreshing) {
                    addEdge(dest, edgeData);
                } else {
                    edgeWaitingList.put(id, edgeData);
                    updateRefreshButtonTxt(edgeWaitingList.size());
                }
            } else {
                int counter = allEdgeIds.get(id);
                counter++;
                allEdgeIds.put(id, counter);
                Edge edge = graph.getEdge(id);
                if (edge == null)
                    return; //*IT MEANS THAT THE EDGE WAS STILL NOT DRAWN BECAUSE THE USER NEVER CLICKED ON REFRESH BUTTON


                edge.setAttribute("ui.label", subject + ":" + counter);
            }
        }
    }

    private String renderEdgeId(String stepId, String subject, String dest) {
        return stepId + "-" + subject + "-" + dest;
    }

    private void addEdge(String id, EdgeData edgeData) {
        if (edgeData.destinationClass.equals("SYSTEM_STEP_MONITOR"))
            return;
        Edge edge  = graph.addEdge(id, edgeData.sourceClass, edgeData.destinationClass, true);
        edge.setAttribute("ui.label", edgeData.subject + ": (calculating...)");
        edge.setAttribute("ui.style", StyleConst.EDGE_STYLE);
    }

    void initVisualization() {
        if (initialized) return;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        graph = createGraph(TextConst.TITLE,  StyleConst.GRAPH_STYLE);

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        add((DefaultView) viewer.addDefaultView(false, new SwingGraphRenderer()), BorderLayout.CENTER);

        ViewerPipe pipe = viewer.newViewerPipe();
        pipe.addAttributeSink(graph);
        pipe.addViewerListener(this);
        pipe.pump();

        setTitle(TextConst.TITLE);
        setSize(800, 600);
        setVisible(true);

        refreshButton = new JButton();
        refreshButton.setBounds(750, 550, 600, 300);
        updateRefreshButtonTxt(edgeWaitingList.size());
        add(refreshButton, BorderLayout.NORTH);
        refreshButton.addActionListener(e -> {
            if (edgeWaitingList.isEmpty()) return;

            viewer.disableAutoLayout();
            refreshing = true;
            refreshButton.setEnabled(false);

            Iterator<Map.Entry<String, EdgeData>> iter = edgeWaitingList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, EdgeData> entry = iter.next();
                addEdge(entry.getKey(), entry.getValue());
                iter.remove();
            }
            fixOverlappingEdgeLabel();
            refreshing = false;
            updateRefreshButtonTxt(edgeWaitingList.size());
            refreshButton.setEnabled(true);
        });

        metadataLabel = new JLabel("Press any node for more information");
        add(metadataLabel, BorderLayout.SOUTH);


        Map<String, VisualStep> distUniqueList = new HashMap<>();
        for (VisualStep s : visualSteps) {
            if (s.getId().equals("SYSTEM_STEP_MONITOR"))
                continue;

            String id = getDistributeIdByStepID(s.getId());
            String name = s.getId();
            if (!s.getId().equals(id) && name.contains(".")) { //if has distribution
                name = name.substring(0, name.lastIndexOf('.'));
            }
            VisualStep stepData = distUniqueList.getOrDefault(id, new VisualStep(s.getId(), name, s.getDistributionNodeId(), s.getNumOfNodes(), s.isSystem()));
            distUniqueList.put(id, stepData);
        }


        for (Map.Entry<String, VisualStep> stepEntry : distUniqueList.entrySet()) {
            Node a = graph.addNode(stepEntry.getKey());

            String nodeLabel = "";
            if(!stepEntry.getValue().isSystem())
             nodeLabel = stepEntry.getValue().hasMultipleNodes() ? stepEntry.getValue().getFriendlyName() + " (" + stepEntry.getValue().getNumOfNodes() + ") " : stepEntry.getValue().getFriendlyName();
           else
               nodeLabel = stepEntry.getValue().getFriendlyName() + " (System Step) ";
            a.setAttribute("ui.label", nodeLabel);


            String nodeStyle = StyleConst.NODE_STYLE;
            if (stepEntry.getValue().hasMultipleNodes()) {
                nodeStyle =  StyleConst.DISTRIBUTED_NODE_STYLE;
            } else if (stepEntry.getValue().isSystem()) {
                nodeStyle =  StyleConst.SYSTEM_NODE_STYLE;
            }


            a.setAttribute("ui.style", nodeStyle);
        }

        initialized = true;

        //keep listening to events
        new Thread(() -> {
            while (loop) {
                pipe.pump();
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Graph createGraph(String title, String style) {
        Graph graph = new MultiGraph(title);
        graph.setAttribute("ui.stylesheet", style);
        graph.setAttribute("ui.quality");
        return graph;
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
                    edgeList.get(i).setAttribute("ui.style", "text-offset: " + offset + "," + offset + ";");
                }
            } // loop neighbouring nodes

        } // loop each node in a graph

    }// function to fix overlapping edge label

    private void updateRefreshButtonTxt(int numOfChanges) {
        refreshButton.setText(TextConst.REFRESH_TEXT + " (" + numOfChanges + ")");
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

    class StyleConst {
        static final String GRAPH_STYLE = "graph {padding: 100px; }";
        static final String NODE_STYLE = "shape:circle;fill-color: #4e81bd;size: 150px; text-alignment: center; text-size: 14px; text-color:#ffffff;";
        static final String EDGE_STYLE = "arrow-size: 14px,5px;text-size: 14px;";
        static final String SYSTEM_NODE_STYLE = "shape:circle;fill-color: Gray;size: 150px; text-alignment: center; text-size: 10px;";
        static final String DISTRIBUTED_NODE_STYLE = "shape:circle;fill-color: Orange;size: 150px; text-alignment: center; text-size: 14px;";
    }

    class TextConst{
        static final String TITLE = "Stepping Live Visualizer";
        static final String REFRESH_TEXT = "Refresh";
        static final String METADATA = "<H3> {STEP_ID} </H3>" +
                "<b>Avg Processing Time: </b>{AVG_PROCESSING_TIME} seconds" +
                "<br> <b>Avg  Chunk Size: </b>{AVG_CHUNK_SIZE}" +
                "<br> <b>Queue Size: </b>{QUEUE_SIZE}";
    }
}

