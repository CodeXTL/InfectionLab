//Tom Li

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoomPanel extends JPanel implements MouseListener
{
   private JPanel[][] grid;   //what is actually displayed
   private Person[][] people; //Encapsulated in the program and interacts with grid[][]
   private JLabel weeksDisplay, infectDisplay, infectionRatioDisplay, avgImmuneDisplay, statusDisplay;
   private JPanel gridPanel;  //Center panel
   private JButton StartEndRunButton;
   private int numCycles, numInfected;
   private int numRows, numCols; //number of rows and columns on grid[][]
   private int currRow, currCol; //row and column of the current block (the one the mouse hovers over); if mouse is not over any block, currRow==currCol==-1
   private int runState = 0;  //if runState==0, auto-run is off; if runState==1, auto-run is on
   private Timer timer = new Timer(120, new StepListener());   //A swing.Timer to run the program automatically with the StepListener, first argument is delay time in milliseconds 
   
   public RoomPanel()
   {
      setLayout(new BorderLayout());
      
      initialize(50,50);   //initialized a grid board, or "room", of size 50 by 50 (or any other specified size, code is made to be versatile); each block is a person
      
      //The Top of the panel
      JPanel NORTH = new JPanel(new FlowLayout());
      weeksDisplay = new JLabel("Cycles: " + numCycles + "     ", SwingConstants.CENTER);
      infectDisplay = new JLabel("Infected: " + numInfected + "     ", SwingConstants.CENTER);
      infectionRatioDisplay = new JLabel("Infection Ratio: " + (1.0*numInfected/(numRows*numCols)) + "     ", SwingConstants.CENTER);
      avgImmuneDisplay = new JLabel("Average Immunity: " + ((int)(100*(1.0*Person.totalImmunity/(numRows*numCols)))/100.0) + "     ", SwingConstants.CENTER);  //Spread becomes limited and eventually stops as average immunity increases
      NORTH.add(weeksDisplay);
      NORTH.add(infectDisplay);
      NORTH.add(infectionRatioDisplay);
      NORTH.add(avgImmuneDisplay);
      add(NORTH, BorderLayout.NORTH);
      
      //The Bottom of the panel
      JPanel SOUTH = new JPanel(new FlowLayout());
      addButton(SOUTH, "Reset", new ResetListener());                //Reset Button
      addButton(SOUTH, "Random Infect", new RandomInfectListener()); //Random Infect Button
      addButton(SOUTH, "Step Cycle", new StepListener());            //Step Cycle Button
      StartEndRunButton = new JButton("Run");                        //Auto-Run Button
      StartEndRunButton.addActionListener(new StartEndRunListener());
      SOUTH.add(StartEndRunButton);
      add(SOUTH, BorderLayout.SOUTH);
      
      //The Right of the panel
      JPanel EAST = new JPanel(new BorderLayout());
      EAST.setPreferredSize(new Dimension(100,50));
      statusDisplay = new JLabel("<html>Row:---<br>Col:---<br>Status:---<br>Immune Rate:---<br>Infect Rate:---<br>Stage:---</html>");
      EAST.add(statusDisplay, BorderLayout.CENTER);
      add(EAST, BorderLayout.EAST);
   }
   
   
   //Initailize central grid display BEGIN
   private void initialize(int x, int y)  //Method is not absolutely necessary, but I think it makes the initialization process more concise
   {
      numRows = x;
      numCols = y;
      grid = new JPanel[numRows][numCols]; 
      people = new Person[numRows][numCols];
      gridPanel = new JPanel(new GridLayout(numRows, numCols));
      
      for(int r=0; r<people.length; r++)        //for-loop to cycle through all matrices and instantiated all Persons and JPanel(or block on grid)
      {
         for(int c=0; c<people[0].length; c++)
         {
            people[r][c] = new Person();        //Essentially, grid[][] is the visual representation of people[][]; grid[][] is foreground, people[][] is background
            grid[r][c] = new JPanel();          //grid[r][c] corresponds to people[r][c]
            grid[r][c].addMouseListener(this);  //adds MouseListener to every individual JPanel of grid[][]
            grid[r][c].setBorder(BorderFactory.createLineBorder(Color.black));   //Add black border to separate each block
            gridPanel.add(grid[r][c]);    //add block at row=r and col=c to gridPanel(or center panel)
         }
      }
      updateGridDisplay();    
      add(gridPanel, BorderLayout.CENTER);   //add gridPanel(or center panel) to CENTER
   }
   //Initialize central grid display END
 
   
   //UpdateGridDisplay BEGIN
   private void updateGridDisplay()    //updates the color of the entire grid for each step of a week; basically, updates color
   {
      for(int r=0; r<grid.length; r++)    //cycle through grid[][]
      {
         for(int c=0; c<grid[0].length; c++)
         {
            if(people[r][c].isInfected())    //if people[r][c] is infected, the corresponding grid[r][c] is set to RED 
            {
               grid[r][c].setBackground(Color.RED);
            }
            else                             //else set to BLUE
            {
               grid[r][c].setBackground(Color.BLUE);
            }
         }
      }
   }
   //UpdateGridDisplay END
   
   
   //Minor methods BEGIN
   private void addButton(JPanel p, String s, ActionListener a)   //simple way to add a button with a specific text and ActionListener
   {
      JButton b = new JButton(s);
      b.addActionListener(a);
      p.add(b);
   }
   
   private void updateStatusDisplay(int r, int c, Person p, boolean reset)   //simple way to update status board, which is a pain to format
   {
      if(reset)   //if reset is true, disregard the three prior arguments and reset statusDisplay
         statusDisplay.setText("<html>Row:---<br>Col:---<br>Status:---<br>Immune Rate:---<br>Infect Rate:---<br>Stage:---</html>");
      else
         statusDisplay.setText("<html>Row: " + r + "<br>Col: " + c + "<br>" + p.toString() + "<br>Immune Rate: " + p.getImmuneRate() + "<br>Infect Rate: " + p.getInfectRate() + "<br>Stage: " + p.getSicknessStage() + "</html>");
   }
   
   private void updateTopDisplay()
   {
      weeksDisplay.setText("Cycles: " + numCycles + "     ");
      infectDisplay.setText("Infected: " + numInfected + "     ");
      infectionRatioDisplay.setText("Infection Ratio: " + (1.0*numInfected/(numRows*numCols)) + "     ");
      avgImmuneDisplay.setText("Average Immunity: " + ((int)(100*(1.0*Person.totalImmunity/(numRows*numCols)))/100.0));
   }
   
   private int countNumInfected()   //Count number of infected/red tiles
   {
      int n = 0;
      for(int r=0; r<people.length; r++)
      {
         for(int c=0; c<people[0].length; c++)
         {
            if(people[r][c].isInfected())
               n++;
         }
      }
      return n;
   }
   //Minor methods END
      
   
   //All components of ResetListener BEGIN
   private class ResetListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         reset();             //reset central grid and set all Persons to healthy
         numCycles = 0;       //reset current run and cycle is set back to 0
         numInfected = 0;     //all Persons are healthy
         updateTopDisplay();  //Update the top display to show the correct values
      }
   }
   
   private void reset()    //Resets entire grid[][] to blue, all Persons of people[][] to healthy
   {
      for(int r=0; r<people.length; r++)
      {
         for(int c=0; c<people[0].length; c++)
         {
            people[r][c].resetStatus();  //set Person to the initial state specified in the constructor
         }
      }
      updateGridDisplay();    //resets the colors
   }
   //All components of ResetListener END
   
   
   //All components of RandomInfectListener BEGIN
   private class RandomInfectListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         int r = -1;
         int c = -1;
         for(int i=0; i<2; i++)     //randomly infect two healthy individuals
         {
            do
            {
               r = (int)(Math.random()*people.length);
               c = (int)(Math.random()*people[0].length);
            }
            while(people[r][c].isInfected() && numInfected != numRows*numCols);  //if by chance the people[r][c] is already infected, randomize again; if infecting two is not possible, to prevent indefinite while loop, check second condition
            infect(r, c);
         }
         updateTopDisplay();
         updateGridDisplay();
      }
   }
   
   private void infect(int r, int c)   //infect Person at people[r][c]
   {
      people[r][c].setInfected();
      numInfected++;    
   }
   //All components of RandomInfectListener END
   
   
   //All components of StepListener BEGIN
   private class StepListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         spread();
         updateRates();    //update the rate of all Persons in people[][], including increase of immune and infect rate if infected; and the gradual decrease of immune rate if healthy
         numCycles++;
         numInfected = countNumInfected();
         updateTopDisplay();
         updateGridDisplay();
      }
   }
   
   private void updateRates()
   {
      for(int r=0; r<people.length; r++)
      {
         for(int c=0; c<people[0].length; c++)
         {
            people[r][c].update();
         }
      }
   }
   
   private void spread()
   {
      int[][] temp = new int[people.length][people[0].length];    //a int matrix is used to keep track of spread, so to prevent a domino effect of spread due how the for-loop works; probably not the most efficient way
      for(int r=0; r<people.length; r++)                          //for temp[][]: 1 == infect, 0 == healthy
      {
         for(int c=0; c<people[0].length; c++)
         {
            if(people[r][c].isInfected())    //update temp[][] to fit the status of the Persons of people[][]
            {
               temp[r][c] = 1;
            }
            if(r > 0 && people[r][c].compareTo(people[r-1][c]) > 0)  //compare Person above people[r][c] if one exists
            {
               temp[r-1][c] = 1;
            }
            if(r < people.length-1 && people[r][c].compareTo(people[r+1][c]) > 0)   //compare Person below people[r][c] if one exists
            {
               temp[r+1][c] = 1;
            }
            if(c > 0 && people[r][c].compareTo(people[r][c-1]) > 0)  //compare Person left of people[r][c] if one exists
            {
               temp[r][c-1] = 1;  
            }
            if(c < people[0].length-1 && people[r][c].compareTo(people[r][c+1]) > 0)   //compare Person right of people[r][c] if one exists
            {
               temp[r][c+1] = 1;
            }
         }
      }
      for(int r=0; r<temp.length; r++)                            //map each number in temp[][] to the right state for each Person in People[][]
      {
         for(int c=0; c<temp[0].length; c++)
         {
            if(temp[r][c]==1)
               people[r][c].setInfected();
         }
      }
   }
   //All components of StepListener END
   
   
   //All components of StartEndRunListener BEGIN
   private class StartEndRunListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         if(runState==0)   //If auto-run is off
         {
            runState = 1;                       //runState is on
            StartEndRunButton.setText("Pause"); //set text of the button to Pause
            timer.start();                      //Start Timer for auto-run
         }
         else  //else auto-run is already on
         {
            runState = 0;                       //runState is off
            StartEndRunButton.setText("Run");   //set text of the button to Start
            timer.stop();                       //Pause Timer for auto-run
         }
      }
   }  
   //All components of StartEndRunListener END
   
   
   //All Components of MouseListener BEGIN
   public void mouseClicked(MouseEvent e)    //Left-click to infect
   {
      if(currRow >= 0 && currCol >= 0)    //if currRow and currCol are valid indices
      {
         infect(currRow, currCol);                                                  //when clicked, infect Person at people[currRow, currCol]
         updateStatusDisplay(currRow, currCol, people[currRow][currCol], false);    //update right-hand status display
         updateTopDisplay();                                                        
         updateGridDisplay();                                                       //set grid[currRow, currCol] to RED; not efficient, but its only one line
      }
   }
 
   public void mouseEntered(MouseEvent e)    //Hover over grid block to check status
   {
      for(int r=0; r<grid.length; r++)
      {
         for(int c=0; c<grid[0].length; c++)
         {
            if (e.getSource() == grid[r][c]) 
            {
               currRow = r;                                       //sets currRow to the row of the grid-block the mouse is currently hovering over
               currCol = c;                                       //sets currCol to the col of the grid-block the mouse is currently hovering over
               updateStatusDisplay(r, c, people[r][c], false);    //update the right-hand status display to show the status of the Person at grid[currRow][currCol]
            }
         }
      }
   }
 
   public void mouseExited(MouseEvent e)     //Resets status display to blank if mouse exits grid; this is to prevent unnecessary errors when the mouse is not hovering over any part of the grid
   {
      currRow = -1;
      currCol = -1;
      updateStatusDisplay(-1, -1, null, true);
   }
   
   //NOT USED
   public void mousePressed(MouseEvent e) {}
   public void mouseReleased(MouseEvent e) {}
   //
   //All components of MouseListener END
}