package representativeColors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ColorBox extends JPanel {
	Color mainColor;
	public void paint(Graphics g) {
	    Dimension d = this.getPreferredSize();
	    int fontSize = 20;

	    g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
	     
	    g.setColor(mainColor);
	    
	    
	  }
	
	public ColorBox(Color backColor) {
		super();
		this.mainColor = backColor;
		this.setBackground(this.mainColor);
	}

	  
}
