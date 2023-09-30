package amazeproject;
import gui.aMazeGUI;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JPanel;
/*
 * Maze.java
 *
 * Created on June 20, 2007, 7:15 PM
 *
 */

/**
 * Maze Representation Class
 * @author Tomasz Gebarowski
 */


public class Maze extends JPanel {
    
    
    //Default Dimensions of a maze
    public int sizeX = 15;
    public int sizeY = 15;
    
    //2D array of cell objects
    private Cell cells[][] = null;
    
    //Cell stack used for maze generation using Depth First method
    private Stack cellStack = new Stack();
    
    //Visited cells counter ( generation )
    private int visitedCells = 1;
    
    //Total number of cells to be visited (generation )
    private int totalCells = sizeX * sizeY;
    
    //Currently processed cell
    private Cell currentCell = null;
    
    //Stack used for backtracking in maze solving algorithm
    private Stack steps; 
    private boolean drawRoute = false;
    
    //Routing map used to store distance from end point 
    private int routingMap[][];
    
    //Determines if we run program 
    public boolean debugMode = false;
    
    /** Creates a new instance of Maze */
    public Maze() {
        init(10,10);
    }
    
    /** Initialize Maze structure
     *  @param width Width of maze
     *  @param height Height of maze 
     */
    public void init(int width, int height) {
        
        
        sizeX = width;
        sizeY = height;
        
        // Cell initialization
        cells = new Cell[sizeX][sizeY];
        totalCells = sizeX * sizeY;
        for (int i = 0; i < sizeX; i++)
                for (int j = 0; j < sizeY; j++)
                {
                        cells[i][j] =  new Cell(this);
                        cells[i][j].row = i;
                        cells[i][j].col = j;
                }

        currentCell = cells[0][0];
        visitedCells = 1;
        
        //Clear cell stack
        cellStack.clear();
        
        
    }
    
    /** Return all cell neighbors that have all walls
     *  @param Cell Cell which neighbors we look for
     *  @return ArrayList ArrayList of neighbors
     */
    private ArrayList getNeighborsWithWalls(Cell cCell)
    {
        ArrayList neighbors = new ArrayList();
        int count = 0;
        
        for (int countRow = -1; countRow <= 1; countRow++)
                for (int countCol = -1; countCol <= 1; countCol++)
                {
                        if ( (cCell.row + countRow < sizeX) &&  
                                 (cCell.col+countCol < sizeY) &&
                                 (cCell.row+countRow >= 0) &&
                                 (cCell.col+countCol >= 0) &&
                                 ((countCol == 0) || (countRow == 0)) &&
                                 (countRow != countCol)
                                )
                        {
                                if (cells[cCell.row+countRow][cCell.col+countCol].hasAllWalls())
                                {
                                        neighbors.add( cells[cCell.row+countRow][cCell.col+countCol]);
                                }
                        }
                }

        return neighbors;
    }
    
    /** Generate maze **/
    public void  generate()
    {  
        drawRoute = false;
        
        // If number of visited cells is smaller then totalCells
        while (visitedCells < totalCells)
        {
                // get a list of the neighboring cells with all walls intact
                ArrayList adjacentCells = getNeighborsWithWalls(currentCell);
                // test for cell existance
                if (adjacentCells.size() > 0)
                {
                        // select one wall and knock down the wall between it and the current cell
                        int randomCell = (int)Math.floor(Math.random() * adjacentCells.size());
                        Cell cell = ((Cell)adjacentCells.get(randomCell));
                        currentCell.knockDownWall(cell);
                        cellStack.push(currentCell); // push the current cell onto the stack
                        currentCell = cell; //set the random neighbor the new current cell
                        visitedCells++;
                }
                else
                {
                        // In no cells,  pop current cell from stack
                        currentCell = (Cell)cellStack.pop();
                }

        }
        
        // Set Start & End
        cells[0][0].isStart = true;
        cells[sizeX-1][sizeY-1].isEnd = true;
        cells[sizeX-1][sizeY-1].weight = sizeX * sizeY - 1;
        
        repaint();
     }
    
    
    /** Function starting tracing process ( selecting a proper way from the routingMap ) 
     */
    public void trace() {
        steps = new Stack();
        
        Cell currentCell = cells[sizeX-1][sizeY-1];
        cells[sizeX-1][sizeY-1].visited = true;
        
        //start tracing from last cell
        trace(currentCell);
        drawRoute = true;
    }
    
    
    /** Function following a given path from a currentCell
     *  It uses routingMap to select a proper way and the shortest path
     */
    public void trace(Cell currentCell) {
      
        // Set smallest weight
        int smallestWeight = 1;
        
        // Weight of endpoint
        int endpoint = sizeX * sizeY - 1;
        
        // If current weight is different then endpoint weight then continue
        while ( currentCell.weight != endpoint ) {

            // Select all adjacent paths
            Vector adjCells = currentCell.findPath();

            Iterator  it = adjCells.iterator();

            // By default turn on backtracking
            boolean backtrack = true;

            // If adjacent paths exits then start processing one path
            while ( it.hasNext() ) {
                Cell c = (Cell)it.next();

                // If cell is visited for the first time
                if ( !c.visited ) {
                    c.weight = this.routingMap[c.row][c.col];
                    
                    if ( debugMode )
                        System.out.println("C.weight: " + c.weight + " smallest weight: " + smallestWeight);
                    
                    // End point detected
                    if (  c.weight == endpoint ) {
                        steps.push(c);    
                        backtrack = false;
                        
                    // Follow smallest weight    
                    } else if  ( c.weight == smallestWeight ) {
                        steps.push(c);
                        backtrack = false;
                    }
                    
                }
            }
             
            // Backtracking handler
            if ( backtrack ) {
                if ( debugMode ) {
                    
                    System.out.println("Backtracking");
                    System.out.println("Weight:" + smallestWeight);
                }
                // pop last step
                if ( !steps.isEmpty() ) {
                    steps.pop();
                    // decrement weight
                    smallestWeight--;
                }
            }
      
            try {
                // Peek step to process
                Cell tmp = (Cell)steps.peek();
                
                // If cell was not visited
                if ( !tmp.visited ) {

                    if ( debugMode )
                        System.out.println("Going to : row: " + tmp.row + " col: " + tmp.col );

                    currentCell = tmp;
                    currentCell.visited = true;
                    this.cells[currentCell.row][currentCell.col].visited = true;;
                    // Increment weight
                    smallestWeight++;
                }
            } catch (EmptyStackException e ) {
                
            }
        }
        
    };
    
    
    /** Runs solving algorithm based on routing table 
     */
    public void solve(aMazeGUI parent) {

            // List working as queue used to store cells to be processed 
            LinkedList frontier = new LinkedList();
            
            // allocate memory for routing map
            routingMap = new int[sizeX][sizeY];

            for ( int i = 0; i < sizeX; i++ )
                for ( int j = 0; j < sizeY; j++ )
                    routingMap[i][j] = -1;

            int currentWeight = 0;

            //Define end & start points
            int targetX = sizeX -1;
            int targetY = sizeY -1;
            
            int startX = 0;
            int startY = 0;

            //Give the highest weight to starting point
            routingMap[startX][startY] = sizeX * sizeY -1;


            if ( debugMode ) {
                System.out.println("Start: " + startX + " " +  startY);
                System.out.println("Target: " + targetX + " " +  targetY);
            }
 
            //Set zeroth weight to endpoint
            routingMap[targetX][targetY] = currentWeight;

            
            Cell nC = cells[targetX][targetY];
            nC.weight = currentWeight;
            
            //Add endpoint to queue
            frontier.addLast(nC);

            // If queue is not empty
            while ( !frontier.isEmpty() ) {

                boolean changed = false;

                Cell currentCell = (Cell)frontier.removeFirst();
                int mapValue = routingMap[currentCell.row][currentCell.col];

                // If cell is not the last one
                if ( mapValue != sizeX * sizeY - 1 ) {

                    // Find all possible paths from cell
                    Vector vct = currentCell.findPath();
                    
                    if (debugMode)
                        System.out.println(vct);
                    
                    Iterator it = vct.iterator();

                    // Iterate over all paths
                    while (it.hasNext() ) {
                        Cell c = (Cell)it.next();

                        // If cell was not previously visited
                        if ( routingMap[c.row][c.col] == -1 ) {
                            
                            // Set its weight
                            c.weight = currentCell.weight + 1;
                            routingMap[c.row][c.col] = c.weight;
                            
                            if ( debugMode )
                                System.out.println(c.row + " " + c.col + " weight: " + c.weight);
                            frontier.addLast(c);         
                        }

                    }   
                    // Increment weight
                    currentWeight++;

                }



            }
            
            if ( debugMode ) {
                printRoutingMap();
                System.out.println("--------------Trace----------------");
            }
            
            // Start tracing - process of path selection based on distances in routingMap
            trace();
            
            
            if ( debugMode )
                printTrace();
            
            try {

                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            repaint();
            parent.disableSolveButton();
        }
        
    /** Function used to print trace */
    public void printTrace() {
        System.out.println("STACK Content: \n");
        
        Iterator it = steps.iterator();
        
        while ( it.hasNext() ) {
            Cell tmp = (Cell)it.next();
            if ( tmp.visited )
                System.out.println(tmp);
        }       
    }
    
    /** Function used to draw a trace **/
    public void drawTrace(Graphics g ) {
        
        g.setColor(Color.green);
        Iterator it = steps.iterator();
        while ( it.hasNext() ) {
            Cell tmp = (Cell)it.next();
            if ( tmp.visited ) {
                
                g.fillRect( tmp.row*tmp.cellSize + tmp.padding, tmp.col*tmp.cellSize + tmp.padding, tmp.cellSize, tmp.cellSize );
                
            }
        }
        g.setColor(Color.black);
    }
     
    /** Function used to print a routing map */
    public void printRoutingMap() {
 
        for ( int j = 0; j < sizeY; j++ ) {
            for ( int i = 0; i < sizeX; i++ )
           
               System.out.print(routingMap[i][j] + "\t");
           
           System.out.println("");
        }

    }    
    
    /** Return given cell 
     *  @param int X coefficient
     *  @param int Y coefficient
     *  @return Cell Cell associated to given coordinates
     */
    public Cell getCell(int x, int y ) {
        return this.cells[x][y];
    }
    
    
     /** Paint the whole maze **/
     public void paintComponent(Graphics g)
     {
         
        //Clear Window 
        g.clearRect(0,0, this.getWidth(), this.getHeight() ); 
        
        
        if (drawRoute) {
            this.drawTrace(g);
        }
        
        
        for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++)
                {
                        cells[i][j].paint(g);
                }
        }        
        

         
     }    
    
    
    
}
