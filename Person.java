//Tom Li
//There probably could have been a better name for this class...
//Also, to keep it friendly, there are no deaths, for now...

public class Person
{
   public static int totalImmunity;                         //Sum of immunity of all existing Person; used to calculate average immunity
   private int immuneRate;                                  //immunity of a specific person, ranges from 20(lowest) -> 90(highest), initially randomized
   private int infectRate;                                  //0 if isInfected==false, ranges from 50 -> 100; depends on sicknessStage (direct relationship)
   private int sicknessStage;                               //10 stages, 0 -> 9, 0==healthy, infectRate increase if stage increases
   private boolean isInfected;                              //Specify whether this person is infected

/*
*  No-arg constructor
*  Initialize all private instance variables
*/
   public Person()
   {
      immuneRate = (int)(Math.random()*51+40);     //initial immuneRate ranges from [40, 90]
      infectRate = 0;                              //infectRate is set to 0; although this line is redundant since private fields are initialized to 0
      sicknessStage = 0;                           //sicknessStage is set to 0, or healthy stage
      isInfected = false;                          //initialized Person is health, so isInfected==false
      totalImmunity += immuneRate;                 //add this.immuneRate to the total immunity of all existing Persons
   }
   
/*
*  Accessor method for immuneRate
*/
   public int getImmuneRate()
   {
      return immuneRate;
   }

/*
*  Accessor method for infectRate
*/
   public int getInfectRate()
   {
      return infectRate;
   }

/*
*  Infect this.Person and set infectRate to the basic value, infectRate==50
*/
   public void setInfected()
   {
      isInfected = true;
      infectRate = 50;
   }

/*
*  Cures this.Person and reset infectRate to 0, infectRate==0
*/
   public void setHealthy()
   {
      isInfected = false;
      infectRate = 0;
   }
   
/*
*  boolean check to see whether this.Person is infected
*/
   public boolean isInfected()
   {
      return isInfected;
   }

/*
*  Accessor for sicknessStage of this.Person
*/
   public int getSicknessStage()
   {
      return sicknessStage;
   }

/*
*  reset this.Person to initial state like that of constructor
*/
   public void resetStatus()
   {
      totalImmunity -= immuneRate;
      immuneRate = (int)(Math.random()*51+40);     
      infectRate = 0;                              
      sicknessStage = 0;                           
      isInfected = false;                          
      totalImmunity += immuneRate;
   }
   
/*
*  Overall update method of Person; updates infect status, sicknessStage, and all the rates; occurs for every Person every cycle
*/
   public void update()
   {
      totalImmunity -= immuneRate;  //this.Person's immuneRate is temporarily removed from the total
      if(isInfected)    //if infected
      {
         sicknessStage++;                           //sicknessStage only increments if infected
         immuneRate += (int)(Math.random()*10+1);   //immunity increase by value between [1,10] when infected
         infectRate += (int)(Math.random()*10+1);   //infectRate increase by value between [1,10] when infected
         if(immuneRate > 90)     //100% immunity is not possible; max is 90%  
            immuneRate = 90;
         if(sicknessStage == 10) //if stage exceeds 9, then this.Person overcame the infection, reset isInfected==false, sicknessStage==0, infectRate==0
         {
            isInfected = false;
            sicknessStage = 0;
            infectRate = 0;
         }
      }
      else     //healthy Person's immunity toward a infection has a chance of decreasing gradually overtime if no longer exposed to the infection
      {
         if(Math.random()<0.2)   //20% chance that immunity decrease
         {
            immuneRate -= (int)(Math.random()*3+1);   //immuneRate decrease by value between [1,3]
            if(immuneRate < 40)  //less than 40% immunity is not possible  
               immuneRate = 40;
         }
      }
      totalImmunity += immuneRate;  //replaces the previously removed immuneRate for this.Person to the totalImmunity
   }
   
/*
*  return > 0 if infectRate of this.Person is greater than immuneRate of p; if return > 0, this.Person will infect p (this is handled in RoomPanel)
*  return <= 0 if infectRate of this.Person is less than or equal to immuneRate of p; if return < 0, infection will not spread
*/
   public int compareTo(Person p)
   {
      return infectRate - p.getImmuneRate();            
   }
   
/*
*  return a String with this.Person's status
*/
   public String toString()
   {
      String temp = "Status: ";
      if(isInfected)
      {
         temp += "infected";
      }
      else
      {
         temp += "healthy";
      }
      return temp;
   }
}