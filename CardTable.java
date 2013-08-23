import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 *  This class implements a graphical canvas in which card 
 *  piles are placed.  It will also contain a nested listener class
 *  to respond to and handle mouse events.
 *
 *  The canvas is large enough to contain five rows of cards.
 *  Each row has its associated fixed CardPile.  When initialized,
 *  all the cards are in the top pile and the others are empty.
 *
 *  CardTable should implement the following behavior:
 *  - When the user doubleclicks on a card, that card and all those
 *    on top of it on the pile should be flipped over
 *  - When the user drags a card, that card and all those on top of it
 *    on the pile should be removed from the pile they are on and
 *    follow the mouse around.
 *  - When the user releases the mouse while dragging a pile of cards,
 *    the pile should be inserted into some fixed pile according to
 *    where the mouse was released. 
 *  
 *  @author Monique Blake
 *  @version CSC 212, 23 February 2011
 */
public class CardTable extends JComponent {
  /** Gives the number of piles available */
  public static final int NPILE = 5;
  
  /** gives the width of the canvas */
  public static final int WIDTH = 800;
  
  /** gives the height of the canvas */
  public static final int HEIGHT = 500;
  
  /** Storage for each of the piles available */
  CardPile pile[] = new CardPile[NPILE];
  
  /** Do we have a card pile in motion? */
  boolean isDragging;
  
  /** Storage for pile that is in motion */  
  CardPile movingPile;
  
  /** Records card under last mouse press */
  Card cardUnderMouse;
  
  /** Records index of pile under last mouse press */
  int pileUnderMouse;
  
  /** Initialize a table with a deck of cards in the first slot */
  public CardTable() {
    pile[0] = new CardPile(Card.newDeck(),2,2);
    pile[1] = new CardPile(2,102);
    pile[2] = new CardPile(2,202);
    pile[3] = new CardPile(2,302);
    pile[4] = new CardPile(2,402);
    
    isDragging = false;
    movingPile = new CardPile(0,0);
    
    // Card movements for stage 1
    pile[1].append(pile[0].split(pile[0].get(42)));
    pile[2].append(pile[0].split(pile[0].get(32)));
    pile[3].append(pile[0].split(pile[0].get(22)));
    pile[4].append(pile[0].split(pile[0].get(12)));
    pile[4].append(pile[3].split(pile[3].get(5)));   
    getPile(0).flip(getPile(0));
    
    // Mouse Listeners
    addMouseListener(new CardMouseListener());
    addMouseMotionListener(new CardMouseMotionListener());
  }
  
  /** Click event handler flips cards if there is a double click */
  private class CardMouseListener extends MouseAdapter implements MouseMotionListener{
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        // When a user double clicks on card
        // Get the card location in x,y coords (from mouse pressed)
        // Gets index of card under the mouse in the designated pile
        // that is under the mouse, and then flips all the cards following
        // where the mouse is
        
        if (cardUnderMouse != null) {
          int indexOfCard = pile[pileUnderMouse].indexOf(cardUnderMouse);
          
          for( ; indexOfCard < pile[pileUnderMouse].size() ;indexOfCard++) {  
            Card c = pile[pileUnderMouse].get(indexOfCard);
            c.flipCard();
          }
        }
        
        repaint();
      }      
    }
    
    /** Press event handler locates the card under the mouse and its pile */
    public void mousePressed(MouseEvent e) {
      pileUnderMouse = e.getY()/100;
      cardUnderMouse = locateCard(e.getX(), e.getY());
    }
    
    /** 
     * Release event handler places cards in nearest
     * pile if mouse is dragging, if mouse is in between
     * a set of cards, mouseReleased will place new cards
     * in between current pile of cards.
     */
    public void mouseReleased(MouseEvent e) {
      pileUnderMouse = e.getY()/100;
      cardUnderMouse = locateCard(e.getX(), e.getY());
      
      if (isDragging = true){
        if(cardUnderMouse != null) {
          pile[pileUnderMouse].merge(movingPile, cardUnderMouse);
        } else{
          pile[pileUnderMouse].merge(movingPile, cardUnderMouse);
        }          
      }     
      movingPile.clear();
      isDragging = false;
      repaint();
    }
  }
  
  /** More Mouse event handlers: Mouse Dragged */
  private class CardMouseMotionListener extends CardMouseListener implements MouseMotionListener{
    
    /** 
     * Drag event handler splits cards from the pile location where mouse is under
     * if the mouse is under a card it will place cards in a pile called 
     * movingPile (temporary holder) until mouse release
     */
    public void mouseDragged(MouseEvent e) {
      if (cardUnderMouse != null) {
        movingPile = pile[pileUnderMouse].split(cardUnderMouse);
        isDragging = true;
        cardUnderMouse = null;
      }
      if (isDragging = true) {       
        int newX = e.getX();
        int newY = e.getY();
        movingPile.setX(newX);
        movingPile.setY(newY);
        repaint();
      }
    }    
  }
  
  /**
   *  Returns the requested card pile
   *
   *  @param i  The index of the pile requested
   *  @return   The requested pile, or null if the pile is empty
   */
  public CardPile getPile(int i) {
    CardPile pile;
    if ((i >= 0)&&(i < NPILE)) {
      pile = this.pile[i];
    } else {
      pile = null;
    }
    return pile;
  }
  
  /**
   *  Attaches the specified cards to the specified pile.
   *  The location of the pile is set to a fixed location.
   *
   *  @param i  ID of the pile to use
   *  @param pile  Cards to put there
   */
  public void setPile(int i, CardPile pile) {
    if ((i >= 0)&&(i < NPILE)) {
      pile.setX(2);
      pile.setY(2+100*i);
      this.pile[i] = pile;
    }
  }
  
  /**
   *  Draws the table and the cards upon it
   *
   *  @param g The graphics object to draw into
   */
  public void paintComponent(Graphics g) {
    g.setColor(Color.green.darker().darker());
    g.fillRect(0,0,WIDTH,HEIGHT);
    g.setColor(Color.black);
    for (int i = 0; i < pile.length; i++) {
      g.drawRect(2,2+100*i,72,96);
      pile[i].draw(g);
    }
    if (movingPile != null) {
      movingPile.draw(g);
    }
  }
  
  /**
   *  The component will look bad if it is sized smaller than this
   *
   *  @return The minimum dimension
   */
  public Dimension getMinimumSize() {
    return new Dimension(WIDTH,HEIGHT);
  }
  
  /**
   *  The component will look best at this size
   *
   *  @return The preferred dimension
   */
  public Dimension getPreferredSize() {
    return new Dimension(WIDTH,HEIGHT);
  }
  
  /**
   *  For debugging.  Runs validation tests on all piles.
   */
  public void validatePiles() {
    for (int i = 0; i < NPILE; i++) {
      System.out.print("Pile "+i+":  ");
      System.out.print("Location:  ("+pile[i].getX()+","+
                       pile[i].getY()+");  Length:  ");
      System.out.print(pile[i].size()+";  Status:  ");
      System.out.println("Valid.");
    }
    System.out.print("Moving pile:  ");
    System.out.print("Location:  ("+movingPile.getX()+","+
                     movingPile.getY()+");  Length:  ");
    System.out.print(movingPile.size()+";  Status:  ");
    System.out.println("Valid.");
  }
  
  /**
   *  Locates the card clicked on, if any.
   *
   *  @param x,y  Coordinates of mouse click
   *  @return  Card  holding clicked card
   */
  public Card locateCard(int x, int y) {
    return pile[y/100].locateCard(x,y);
  }
}  // end of CardTable
