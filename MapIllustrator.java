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
public void drawMap(Graphics g) {
    int minimum = findMin();
    int maximum = findMax();
    int range = maximum - minimum;

    for (int row = 0; row < grid.length; row++) {
        for (int column = 0; column < grid[0].length; column++) {
            int value = grid[row][column];
            
            // Normalize elevation to a 0-1 range
            double normalized = (double) (value - minimum) / range;

            // Interpolate color: green (low) to tan (mid) to white (high)
            int red, green, blue;
            if (normalized < 0.5) {
                // Low to mid (green to tan)
                double ratio = normalized / 0.5;
                red = (int) (34 * (1 - ratio) + 210 * ratio);   // Green to Tan
                green = (int) (139 * (1 - ratio) + 180 * ratio);
                blue = (int) (34 * (1 - ratio) + 140 * ratio);
            } else {
                // Mid to high (tan to white)
                double ratio = (normalized - 0.5) / 0.5;
                red = (int) (210 * (1 - ratio) + 255 * ratio);  // Tan to White
                green = (int) (180 * (1 - ratio) + 255 * ratio);
                blue = (int) (140 * (1 - ratio) + 255 * ratio);
            }

            // Set color and draw pixel
            g.setColor(new Color(red, green, blue));
            g.fillRect(column, row, 1, 1);
        }
    }
}


/**
 * Find a path from West-to-East starting at the given row.
 * Choose a forward step out of 5 possible forward locations, using improved greedy logic.
 * @return the total change in elevation traveled from West-to-East
 */
public int drawPath(Graphics g, int startRow) {
    int totalElevationChange = 0;

    for (int col = 0; col < grid[0].length - 1; col++) { // Move through columns
        int currentElevation = grid[startRow][col];

        // Variables to store potential next steps
        int[] elevations = new int[5];
        int[] changes = new int[5];
        Arrays.fill(elevations, Integer.MAX_VALUE);

        // Look at up to 5 possible moves
        if (startRow > 1) elevations[0] = grid[startRow - 2][col + 1]; // 2 steps up
        if (startRow > 0) elevations[1] = grid[startRow - 1][col + 1]; // 1 step up
        elevations[2] = grid[startRow][col + 1]; // Straight ahead
        if (startRow < grid.length - 1) elevations[3] = grid[startRow + 1][col + 1]; // 1 step down
        if (startRow < grid.length - 2) elevations[4] = grid[startRow + 2][col + 1]; // 2 steps down

        // Compute changes in elevation
        for (int i = 0; i < 5; i++) {
            if (elevations[i] != Integer.MAX_VALUE) {
                changes[i] = Math.abs(currentElevation - elevations[i]);
            }
        }

        // Select the move with the lowest elevation change
        int minChange = Integer.MAX_VALUE;
        int bestMove = 2; // Default to moving straight ahead
        for (int i = 0; i < 5; i++) {
            if (changes[i] < minChange) {
                minChange = changes[i];
                bestMove = i;
            }
        }

        // Adjust the starting row based on the best move
        switch (bestMove) {
            case 0: startRow -= 2; break;
            case 1: startRow -= 1; break;
            case 2: break; // No change
            case 3: startRow += 1; break;
            case 4: startRow += 2; break;
        }

        // Add the elevation change to the total
        totalElevationChange += minChange;

        // Draw the path
        g.fillRect(col + 1, startRow, 1, 1);
    }

    return totalElevationChange;
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
