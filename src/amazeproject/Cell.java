/*
 * Cell.java
 *
 * Created on June 20, 2007, 7:08 PM
 *
 */

package amazeproject;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

/**
 * Cell Representation Class
 * @author Tomasz Gebarowski
 */
public class Cell
{
        // Default Cell Size in Pixels
        public static int cellSize = 20;
        
        //Initial cell padding
        public static int padding = 5;
        
        //Wall surrounding ( by default all walls created )
        public int[] walls  = {1, 1, 1, 1};
        
        // Row & Column associated to current cell
        public int row;
        public int col;
        
        //Weight of the cell used for traversal algorithm
        public int weight;
        
        //Determines if the cell was previously visited ( used in traversal )
        public boolean visited;
        
        
        //Flags determining if cell is endpoint or start point
        public boolean isStart = false;
        public boolean isEnd = false;
        
        //Reference to parent object
        private Maze parent;
        
        //Debug mode 
        private boolean debugMode = false;


        /**
         * Constructor
         * @param Maze Reference to parent object
         */
        public Cell(Maze parent)
        {         
            this.parent = parent;
            this.visited = false;
            this.debugMode = parent.debugMode;
        }

        /** Check if a given cell has all wals
         *  @return boolean True if cell has all 4 walls
         */
        public boolean hasAllWalls()
        {
                for (int i = 0; i < 4; i++)
                {
                         if (walls[i] == 0)
                                 return false;
                }
                return true;
        }

        /**
         * Destroy given wall
         * @param int Wall to be destroyed
         */
        public void knockDownWall(int wall)
        {
                walls[wall] = 0;
        }

        /**
         * Destroy wall between this and given cell 
         * @param Cell Reference to cell object
         */
        public void knockDownWall(Cell cell)
        {
                // find adjacent wall
                int wallToRemove = findAdjacentWall(cell);
                walls[wallToRemove] = 0;

                // get opposite wall
                int opWallToRemove = (wallToRemove + 2) % 4;
                cell.walls[opWallToRemove] = 0;
        }


        /**
         * Return index of adjacent wall
         * @param Cell Reference to cell object
         * @return int Index of adjacent cell
         */
        public  int findAdjacentWall(Cell cell)
        {
                // If rows are the same
                if (cell.row == row) 
                {
                        if (cell.col < col)
                                return 0;
                        else
                                return 2;
                }
                // otherwise columns should be the same
                else 
                {
                        if (cell.row < row)
                                return 1;
                        else
                                return 3;
                }
        }
        
        /**
         * Find all possible directions from current cell
         * @return Vector Vector of cells that are connected to this cell
         */
        public Vector findPath() {
            
            Vector vct = new Vector();
            Cell cell;
            
            
            //Store current cell coordinates
            int y = row;
            int x = col;
            
            /*** Check for accessible directions ***/
            
            //top
            if  ( this.walls[0] == 0 ) {

                cell = new Cell(parent);
                
                if ( debugMode )
                    System.out.println("T Row: " + y + " Col: " + x);
                
                if ( x - 1 >= 0 ) {
                    cell.col = x-1;
                    cell.row = y;
                    cell.weight = parent.getCell(cell.row,cell.col).weight;
                    cell.walls = parent.getCell(cell.row,cell.col).walls;
                    cell.visited = parent.getCell(cell.row,cell.col).visited;
                    vct.add(cell);
                    
                    if ( debugMode )
                        System.out.println("Top");
                }
            } 
            
            //left
            if  ( this.walls[1] == 0 ) {
                cell = new Cell(parent);   
                
                if ( debugMode )
                    System.out.println("L Row: " + y + " Col: " + x);
                
                if ( y - 1 >= 0 ) {
                    cell.row = y-1;
                    cell.col = x;
                    cell.weight = parent.getCell(cell.row,cell.col).weight;
                    cell.walls = parent.getCell(cell.row,cell.col).walls;
                    cell.visited = parent.getCell(cell.row,cell.col).visited;
                    vct.add(cell);
                    
                    if ( debugMode )
                        System.out.println("Left");
                }
            } 
            //bottom
            if  ( this.walls[2] == 0 ) {
                cell = new Cell(parent);
                
                if ( debugMode )
                    System.out.println("B Row: " + y + " Col: " + x);
                
                if ( x + 1 < parent.sizeX ) {
                    cell.row = y;
                    cell.col = x+1;
                    cell.weight = parent.getCell(cell.row,cell.col).weight;
                    cell.walls = parent.getCell(cell.row,cell.col).walls;
                    cell.visited = parent.getCell(cell.row,cell.col).visited;
                    vct.add(cell);
                    
                    if ( debugMode )
                        System.out.println("Bottom");
                }
            } 
            //right
            if  ( this.walls[3] == 0 ) {
                
                if ( debugMode )
                    System.out.println("R Row: " + y + " Col: " + x);
                
                cell = new Cell(parent);
                
                if ( y + 1 < parent.sizeY ) {
                    cell.row = y+1;
                    cell.col = x;
                    cell.weight = parent.getCell(cell.row,cell.col).weight;
                    cell.walls = parent.getCell(cell.row,cell.col).walls;
                    cell.visited = parent.getCell(cell.row,cell.col).visited;
                    vct.add(cell);
                    
                    if ( debugMode )
                        System.out.println("Right");
                }
            } 
            
            
            return vct;
        }

        /** Return index of random wall
         *  @return int Index of random wall
         */
        public int getRandomWall()
        {
                int randomWall = (int)Math.round( Math.random() * 3 ) ;
                while ( (walls[randomWall] == 0)  
                ||		((row == 0) && (randomWall == 0)) ||
                                ((row == parent.sizeY - 1) && (randomWall == 2)) ||
                                ((col == 0) && (randomWall == 1)) ||
                                ((col == parent.sizeX - 1) && (randomWall == 3)) 
                           )
                {
                        randomWall =   (int)Math.round( Math.random() * 3 );
                }

                return randomWall;
        }

        /** Paint cell
         *  @param Graphics Graphics object
         */
        public void paint(Graphics g)
        {
             
                g.setColor(Color.black);
        
                //top
                if (walls[0] == 1)
                {
                        g.drawLine(row*cellSize + padding, col*cellSize + padding, (row+1) * cellSize   + padding, col*cellSize +  + padding);
                }
                //left
                if (walls[1] == 1)
                {
                        g.drawLine(row*cellSize  + padding, col*cellSize + padding, row*cellSize + padding, (col+1)*cellSize + padding);
                }
                //bottom
                if (walls[2] == 1)
                {
                        g.drawLine(row*cellSize + padding, (col+1)*cellSize + padding, (row+1)*cellSize + padding, (col+1)*cellSize + padding);
                }
                //right
                if (walls[3] == 1)
                {
                        g.drawLine((row+1)*cellSize + padding , col*cellSize + padding, (row+1)*cellSize + padding, (col+1)*cellSize + padding);
                }

                //Draw starting point
                if ( isStart ) {
                    g.setColor( Color.blue );
                    g.fillRect(row*cellSize + padding, col*cellSize + padding, cellSize, cellSize);
                    g.setColor(Color.white);
                    g.drawString("S", row*cellSize+2*padding, col*cellSize+ 4*padding);
                //Draw endpoint
                } else if ( isEnd ) {
                    g.setColor( Color.red );
                    g.fillRect(row*cellSize + padding, col*cellSize + padding, cellSize, cellSize);
                    g.setColor(Color.black);
                    g.drawString("E", row*cellSize+2*padding, col*cellSize+ 4*padding);
                }


        }
        
        /** Function used for getting a string representation of object
         *  @return String String representation of object
         */
        public String toString() {
            
            return "X:  " + col + " Y: " + row;
         }
}