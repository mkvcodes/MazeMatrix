package maze;

import java.awt.Dimension; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Maze {
    
    /* This  is a program to solve simple mazes. The mazes are given in 
        a file and the program must read in the file, solve the maze and output the solution.
        This program uses Breadth First Search Algorithm.
     * */
    private Dimension startPoint, endPoint, mazeDimension;
    private boolean mazeSolved;
    private Node [][] matrixMaze;
    
    private enum NodeType {
        NODE_START, NODE_END, NODE_PASSAGE, NODE_PATH, NODE_WALL
    }
    
    private String inputFile;
    
   

    private static final String WALL = "#";
    private static final String PASSAGE = " ";
    private static final String END = "E";
    private static final String START = "S";
    private static final String PATH = "X";
    private static final String NO_SOLUTION = "No solution is possible";


    public static void main (String [] args) throws IOException {
    	
        for (String arg : args) {
            long startTime = System.currentTimeMillis();

            Maze maze = new Maze();
            maze.setInputFile(arg);
            maze.proocessInput();
            maze.breadthFirstSearch();
            maze.printMazeSolved();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("total time: " + totalTime + "ms");
        }
    }


    
    

    /**
     *This method takes  input file and process it.
     *It  @throws IOException
     */
    public void proocessInput() throws IOException{
        FileReader fr = new FileReader(inputFile);
        BufferedReader bufr = new BufferedReader(fr);

        int lineNo = 0;
        int matrixLine = 0;
        String line = bufr.readLine();

        while (line != null) {
            if (lineNo == 0) {
                setMazeDimension(line);
            } else if (lineNo == 1) {
                setStartPoint(line);
            } else if (lineNo == 2) {
                setEndPoint(line);
            } else {
                lineToNode(line, matrixLine);
                matrixLine++;
            }

            line = bufr.readLine();
            lineNo++;
        }

        bufr.close();
    }


    /**
     * Takes the input and set start point coordinates.
     * @param startLine
     */
    private void setStartPoint(String startLine) {
        String[] dimension = startLine.split("\\s+");
        // height x width
        startPoint = new Dimension( Integer.parseInt(dimension[1]), Integer.parseInt(dimension[0]) );
    }
    
    
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }
   


    /**
     *  This method takes the  input and set end point coordinates.
     * @param endLine
     */
    private void setEndPoint(String endLine) {
        String[] dimension = endLine.split("\\s+");
        // height x width:
        endPoint = new Dimension( Integer.parseInt(dimension[1]), Integer.parseInt(dimension[0]) );
        
        
    }
    
 //Class which sets field in the maze.
    private class Node {
        public int distance;
        public Node parent;
        public Maze.NodeType type;
        private Dimension position = new Dimension();

        public Node(NodeType type) {
            this.type = type;

            distance = -1;
            parent = null;
        }

        public void setWidth(int position) {
            this.position.width = position;
        }

        public int getWidth() {
            return this.position.width;
        }

        public void setHeight(int position) {
            this.position.height = position;
        }

        public int getHeight() {
            return this.position.height;
        }
    }


   
     // This method takes the  input line and set maze dimension.
      private void setMazeDimension(String line) {
        String[] dimension = line.split("\\s+");
        // height x width of the matrix
        mazeDimension = new Dimension( Integer.parseInt(dimension[1]), Integer.parseInt(dimension[0]) );

        matrixMaze = new Node[mazeDimension.height][mazeDimension.width];
    }


    
    
  //This method finds End node using Breadth First Search Algorithm.
  // Breadth-first search (BFS) is an algorithm for traversing or searching tree or graph data structures
    public void breadthFirstSearch() {
        Queue<Node> queue = new LinkedList<>();
        Node start = matrixMaze[startPoint.height][startPoint.width];

        start.distance = 0;
        queue.add(start);

        while ( !queue.isEmpty() ) {
            Node currentNode = queue.remove();

            List<Node> adjacentNodes = getAdjacentNodes(currentNode);

            adjacentNodes.stream().filter(adjacentNode -> adjacentNode.distance < 0).forEach(adjacentNode -> {
                adjacentNode.distance = currentNode.distance + 1;
                adjacentNode.parent = currentNode;
                queue.add(adjacentNode);

                if (adjacentNode.type == NodeType.NODE_END) {
                    queue.clear();
                    mazeSolved = true;
                    setPath();
                }
            });
        }
    }
    
    private void setPath() {
        Node node = matrixMaze[endPoint.height][endPoint.width];

        while (node.parent.type != NodeType.NODE_START) {
            if (node.parent.type == NodeType.NODE_PASSAGE) {
                node.parent.type = NodeType.NODE_PATH;
                node = node.parent;
            }
        }
    }


  //This method returns arraylist of the adjacent nodes.
    private List<Node> getAdjacentNodes(Node node) {
        List<Node> adjacentNodes = new ArrayList<>();

        // get N node
        if (node.getHeight() > 0) {
            Node adjacentNodeN = matrixMaze[node.getHeight() - 1][node.getWidth()];
            if (adjacentNodeN.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeN);
            }
        }
        // get W node
        if (node.getWidth() < mazeDimension.width - 1) {
            Node adjacentNodeW = matrixMaze[node.getHeight()][node.getWidth() + 1];
            if (adjacentNodeW.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeW);
            }
        }
        // get S node
        if (node.getHeight() < mazeDimension.height - 1) {
            Node adjacentNodeS = matrixMaze[node.getHeight() + 1][node.getWidth()];
            if (adjacentNodeS.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeS);
            }
        }
        // get E node
        if (node.getWidth() > 0) {
            Node adjacentNodeE = matrixMaze[node.getHeight()][node.getWidth() - 1];
            if (adjacentNodeE.type != NodeType.NODE_WALL) {
                adjacentNodes.add(adjacentNodeE);
            }
        }

        return adjacentNodes;
    }


   

    
    //This method takes the input line of maze and convert it in to nodes
    private void lineToNode(String line, int matrixLine) {
        if (matrixLine < mazeDimension.height) {
            String[] nodesLine = line.split("\\s+");

            for (int matrixColumn = 0; matrixColumn < nodesLine.length; matrixColumn++) {
                Node node;
                Dimension currentPosition = new Dimension(matrixColumn, matrixLine);

                if ( currentPosition.equals(startPoint) ) {
                    node = new Node(NodeType.NODE_START);
                } else if ( currentPosition.equals(endPoint) ) {
                    node = new Node(NodeType.NODE_END);
                } else {
                    int matrixValue = Integer.parseInt( nodesLine[matrixColumn] );

                    switch (matrixValue) {
                        case 0:
                            node = new Node(NodeType.NODE_PASSAGE);
                            break;
                        case 1: default:
                            node = new Node(NodeType.NODE_WALL);
                    }
                }

                node.setHeight(matrixLine);
                node.setWidth(matrixColumn);
                matrixMaze[matrixLine][matrixColumn] = node;
            }
        }
    }


//This method prints the solved maze to the console
    public void printMazeSolved() {
        if (mazeSolved) {
            for (Node[] nodeLine : matrixMaze) {
                String line = "";

                for (Node node : nodeLine) {
                    switch (node.type) {
                        case NODE_WALL:
                            line += WALL;
                            break;
                        case NODE_START:
                            line += START;
                            break;
                        case NODE_PATH:
                            line += PATH;
                            break;
                        case NODE_PASSAGE:
                            line += PASSAGE;
                            break;
                        case NODE_END:
                            line += END;
                            break;
                        default:
                            break;
                    }
                }

                System.out.println(line);
            }
        } else {
            System.out.println(NO_SOLUTION);
        }
    }
}
