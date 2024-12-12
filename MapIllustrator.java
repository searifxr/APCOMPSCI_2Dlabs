import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MapIllustrator
{
/** the 2D array containing the elevations */
private int[][] grid;

/** constructor, parses input from the file into grid
        @param fileName
        * @throws java.io.IOException */
public MapIllustrator(String fileName) throws IOException
{
                Scanner file = new Scanner(new File(fileName));
                int column = file.nextInt();
                int rows = file.nextInt();
                grid = new int[column][rows];
               
                for(int i = 0; i < column; i++)
                {
                    for(int j = 0; j < rows; j++)
                    {
                      grid[i][j] = file.nextInt();  
                    }
                }
                file.close();
}

/** @return the min value in the entire grid */
public int findMin()
{
  int minimum = Integer.MAX_VALUE;
  for(int i = 0; i < grid.length; i++ ) //this finna be row
  {
      for(int j = 0; j < grid[i].length; j++) //this a column
      {
          if(minimum > grid[i][j])
          {
             minimum = grid[i][j];
          }
      }
  }

  return minimum;
}

/** @return the max value in the entire grid */
public int findMax()
{
   int max = Integer.MIN_VALUE;
   for(int i = 0; i < grid.length; i++ ) //this finna be column
   {
       for(int j = 0; j < grid[i].length; j++) //this a row
       {
           if(max < grid[i][j])
           {
              max = grid[i][j];
           }
       }
   }

    return max; 
}

/**
* Draws the grid using the given Graphics object.
* Colors should be grayscale values 0-255, scaled based on mi/max valuens in grid
*/
public void drawMap(Graphics g)
{
    int minimum = findMin();
    int maximum = findMax();
    int range = maximum-minimum;

    for(int row = 0; row < grid.length; row++)
    {
      for(int column = 0; column < grid[1].length; column++)
      {
        int value = grid[row][column];
        int grayscale = (int)((value - minimum)*255.0/range);

        g.setColor(new Color(grayscale, grayscale, grayscale));
        g.fillRect(column, row, 1, 1);
      }
    }
}

/**
* Find a path from West-to-East starting at given row.
* Choose a forward step out of 3 possible forward locations, using greedy method described in assignment.
* @return the total change in elevation traveled from West-to-East
*/
public int drawPath(Graphics g, int Startrow)
{
    int total_elevation_change = 0;
    for(int i = 0; i < grid[0].length-1; i++) //Goign thru caloumn
    {
        int current_elevation = grid[Startrow][i];

        int front = grid[Startrow][i+1];
        int up = Integer.MAX_VALUE; //placeholder
        int down = Integer.MAX_VALUE;

        if(Startrow > 0)
        {
            up = grid[Startrow-1][i+1];
        }

        if(Startrow < grid.length-1)
        {
            down = grid[Startrow+1][i+1];
        }
        //calculating changes in elevation
        int changeFront = Math.abs(current_elevation-front);
        int changeDown = Math.abs(current_elevation-down);
        int changeUp = Math.abs(current_elevation-up);

        //if you want to move up
        if(Startrow > 0 && changeUp < changeFront && changeUp < changeDown)
        {
            Startrow--;
            total_elevation_change += changeUp;
        }
        //if you want to move down
        else if(Startrow < grid.length-1 && changeDown < changeUp && changeDown < changeFront)
        {
            Startrow++;
            total_elevation_change += changeDown;
        }
        //if two are tied
        else if(Startrow > 0 && changeDown == changeFront || changeFront == changeUp)
        {
            //three way tie
            if(changeDown == changeFront && changeDown == changeUp)
            {
                Random rng = new Random();
                int selection = rng.nextInt(3)+1;
                switch (selection) {
                    case 1:
                        Startrow++;
                        total_elevation_change += changeDown;
                        break;
                    case 2:
                        Startrow--;
                        total_elevation_change += changeUp;
                        break;
                    default:
                        total_elevation_change += changeFront;
                        break;
                }
            }
            //two way ties
            //change down
            if(changeDown == changeFront)
            {
                Random rng = new Random();
                int selection = rng.nextInt(2)+1;
                switch (selection) {
                    case 1:
                        Startrow++;
                        total_elevation_change += changeDown;
                        break;
                    case 2:
                        total_elevation_change += changeFront;
                        break;
                }
            }
            else if(changeDown == changeUp)
            {
                Random rng = new Random();
                int selection = rng.nextInt(2)+1;
                switch (selection) {
                    case 1:
                        Startrow++;
                        total_elevation_change += changeDown;
                        break;
                    case 2:
                        Startrow--;
                        total_elevation_change += changeUp;
                        break;
                }
            }
            //change front
            else if(changeFront == changeUp)
            {
                Random rng = new Random();
                int selection = rng.nextInt(2)+1;
                switch (selection) {
                    case 1:
                        total_elevation_change += changeFront;
                        break;
                    case 2:
                        Startrow--;
                        total_elevation_change += changeUp;
                        break;
                }
            }
            else if(changeFront == changeDown)
            {
                Random rng = new Random();
                int selection = rng.nextInt(2)+1;
                switch (selection) {
                    case 1:
                        total_elevation_change += changeFront;
                        break;
                    case 2:
                        Startrow++;
                        total_elevation_change += changeDown;
                        break;
                }
            }
            //change up
            else if(changeUp == changeDown)
            {
                Random rng = new Random();
                int selection = rng.nextInt(2)+1;
                switch (selection) {
                    case 1:
                        Startrow--;
                        total_elevation_change += changeUp;
                        break;
                    case 2:
                        Startrow++;
                        total_elevation_change += changeDown;
                        break;
                }
            }
            else
            {
                Random rng = new Random();
                int selection = rng.nextInt(2)+1;
                switch (selection) {
                    case 1:
                        Startrow--;
                        total_elevation_change += changeUp;
                        break;
                    case 2:
                        total_elevation_change += changeFront;
                        break;
                }
            }
        }
        else
        {
            total_elevation_change += changeFront; 
        }
    g.fillRect(i, Startrow, 1, 1);
    }
    
return total_elevation_change;
}

/** @return the index of the starting row for the lowest-elevation-change path in the entire grid. */
public int getIndexOfLowestPath(Graphics g)
{
    int minimum_elevation_path = Integer.MAX_VALUE;
    int starting_row = 0;
    for(int row = 0; row < grid.length; row++)
    {
        int elevationChange = drawPath(g, row);
        if(minimum_elevation_path > elevationChange)
        {
            minimum_elevation_path = elevationChange;
            starting_row = row;
        }
    }

    return starting_row;
}

/** return the number of rows in grid */
public int getRows()
{
    return grid.length;
}

/** return the number of columns in grid (assumed rectangular) */
public int getCols()
{
    return grid[0].length;
}
}
